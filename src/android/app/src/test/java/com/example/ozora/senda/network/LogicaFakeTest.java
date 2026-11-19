package com.example.ozora.senda.network;

import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.MedicionTemperatura;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

/** Verifica las peticiones HTTP del proxy contra un servidor local de prueba. */
public class LogicaFakeTest {
    private ServerSocket servidor;
    private Thread receptor;
    private volatile Throwable errorServidor;
    private LogicaFake logica;
    private volatile String metodo, ruta, autorizacion, cuerpo;
    private volatile int estado = 200;
    private volatile String respuesta = "{\"ok\":true,\"data\":{\"guardada\":true}}";

    @Before public void preparar() throws Exception {
        servidor = new ServerSocket(0, 10, java.net.InetAddress.getByName("127.0.0.1"));
        receptor = new Thread(() -> {
            try {
                while (!servidor.isClosed()) {
                    try (Socket socket = servidor.accept()) {
                        socket.setSoTimeout(5000);
                        BufferedInputStream entrada = new BufferedInputStream(socket.getInputStream());
                        String[] peticion = linea(entrada).split(" ");
                        metodo = peticion[0];
                        ruta = peticion[1];
                        autorizacion = null;
                        int longitud = 0;
                        String cabecera;
                        while (!(cabecera = linea(entrada)).isEmpty()) {
                            int separador = cabecera.indexOf(':');
                            String nombre = cabecera.substring(0, separador);
                            String valor = cabecera.substring(separador + 1).trim();
                            if (nombre.equalsIgnoreCase("Authorization")) autorizacion = valor;
                            if (nombre.equalsIgnoreCase("Content-Length")) longitud = Integer.parseInt(valor);
                        }
                        cuerpo = new String(entrada.readNBytes(longitud), StandardCharsets.UTF_8);
                        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
                        String cabeceras = "HTTP/1.1 " + estado + " Test\r\nContent-Type: application/json"
                                + "\r\nContent-Length: " + bytes.length + "\r\nConnection: close\r\n\r\n";
                        socket.getOutputStream().write(cabeceras.getBytes(StandardCharsets.US_ASCII));
                        socket.getOutputStream().write(bytes);
                    }
                }
            } catch (Throwable error) {
                if (!servidor.isClosed()) errorServidor = error;
            }
        }, "servidor-http-prueba");
        receptor.setDaemon(true);
        receptor.start();
        logica = new LogicaFake(new ApiClient("http://127.0.0.1:"
                + servidor.getLocalPort() + "/api/v1", true));
    }

    @After public void cerrar() throws Exception {
        if (servidor != null) servidor.close();
        if (receptor != null) receptor.join(5000);
        assertNull("Fallo en el servidor de prueba", errorServidor);
    }

    private String linea(BufferedInputStream entrada) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int dato;
        while ((dato = entrada.read()) != -1 && dato != '\n') {
            if (dato != '\r') bytes.write(dato);
        }
        return bytes.toString(StandardCharsets.US_ASCII.name());
    }

    @Test public void historialAutenticadoConservaCursorSinAlterarlo() throws Exception {
        respuesta = "{\"ok\":true,\"data\":{\"mediciones\":[],\"siguiente\":null}}";
        assertEquals(0, logica.historialMediciones("token-test", null).getJSONArray("mediciones").length());
        comprobar("GET", "/usuarios/actual/mediciones", "token-test");
        logica.historialMediciones("token-test", "a+b/c==");
        comprobar("GET", "/usuarios/actual/mediciones?cursor=a%2Bb%2Fc%3D%3D", "token-test");
    }

    @Test public void guardarMedicionConservaContratoYConfirmacion() throws Exception {
        MedicionAmbiental m = new MedicionAmbiental("S-001", 2, 39, -0.1, 5,
                "2026-11-18T10:00:00.000Z", 8);
        assertTrue(logica.guardarMedicion("token-test", m).getBoolean("guardada"));
        comprobar("POST", "/mediciones", "token-test");
        JSONObject datos = new JSONObject(cuerpo);
        assertEquals(8, datos.length());
        assertEquals("S-001", datos.getString("nombre_micro"));
        assertEquals("O3", datos.getString("tipo"));
        assertEquals(2, datos.getInt("valor"));
        assertEquals("ppb", datos.getString("unidad"));
        assertEquals(39, datos.getDouble("latitud"), 0);
        assertEquals(-0.1, datos.getDouble("longitud"), 0);
        assertEquals(m.fechaIso8601, datos.getString("fecha_hora"));
        assertFalse(datos.getBoolean("simulada"));
    }

    @Test public void sesionYRecuperacionUsanLasRutasYCredencialesAdecuadas() throws Exception {
        JSONObject datos = new JSONObject().put("correo", "test@example.org");
        logica.iniciarSesion(datos);
        comprobar("POST", "/sesiones", null);
        assertEquals(datos.toString(), cuerpo);
        logica.consultarSesion("token-test");
        comprobar("GET", "/sesiones/actual", "token-test");
        assertEquals("", cuerpo);
        logica.cerrarSesion("token-test");
        comprobar("DELETE", "/sesiones/actual", "token-test");
        logica.cambiarContrasena("token-test", datos);
        comprobar("POST", "/usuarios/actual/contrasena", "token-test");
        assertEquals(datos.toString(), cuerpo);
        logica.solicitarRecuperacion(datos);
        comprobar("POST", "/recuperaciones", null);
        logica.restablecerContrasena(datos);
        comprobar("POST", "/recuperaciones/confirmacion", null);
        assertEquals(datos.toString(), cuerpo);
    }

    @Test public void propagaErroresDelServidorSinConfirmarGuardado() throws Exception {
        estado = 401;
        respuesta = "{\"ok\":false,\"error\":{\"mensaje\":\"Sesión caducada\"}}";
        try {
            logica.consultarSesion("token-test");
            fail("Debe propagarse el error HTTP");
        } catch (ApiClient.Error error) {
            assertEquals(401, error.status);
            assertEquals("Sesión caducada", error.getMessage());
        }
    }

    @Test public void requiereConfirmacionExplicitaAntesDeDarPorGuardadaUnaLectura() throws Exception {
        MedicionAmbiental m = new MedicionAmbiental("S-001", 2, 39, -0.1, 5,
                "2026-11-18T10:00:00.000Z", 8);
        for (String datos : new String[]{"{}", "{\"guardada\":false}", "{\"guardada\":\"true\"}"}) {
            respuesta = "{\"ok\":true,\"data\":" + datos + "}";
            try {
                logica.guardarMedicion("token-test", m);
                fail("Debe conservarse pendiente si falta la confirmación de guardado");
            } catch (IOException esperado) {
                assertTrue(esperado.getMessage().contains("no ha confirmado"));
            }
        }
        respuesta = "{\"ok\":true,\"data\":{\"guardada\":true,\"duplicada\":true}}";
        assertTrue(logica.guardarMedicion("token-test", m).getBoolean("duplicada"));
    }

    @Test public void perfilYLlaveroUsanCuentaAutenticada() throws Exception {
        logica.actualizarFotoPerfil("token-test", "data:image/jpeg;base64,imagen");
        comprobar("POST", "/usuarios/actual/foto", "token-test");
        assertEquals("data:image/jpeg;base64,imagen", new JSONObject(cuerpo).getString("foto_perfil"));
        logica.actualizarFotoPerfil("token-test", null);
        assertTrue(new JSONObject(cuerpo).isNull("foto_perfil"));
        logica.vincularLlavero("token-test", "S-001");
        comprobar("POST", "/llaveros/actual", "token-test");
        assertEquals("S-001", new JSONObject(cuerpo).getString("nombre_micro"));
        logica.desvincularLlavero("token-test");
        comprobar("DELETE", "/llaveros/actual", "token-test");
        assertEquals("", cuerpo);
    }

    @Test public void temperaturaUsaElEndpointAutenticadoYExigeConfirmacion() throws Exception {
        MedicionTemperatura m=new MedicionTemperatura("S-001",2939,"2026-11-19T10:00:00.000Z",39.123456789,-0.376312345);
        assertTrue(logica.guardarTemperatura("token-test",m).getBoolean("guardada"));
        comprobar("POST","/mediciones","token-test");
        JSONObject json=new JSONObject(cuerpo);
        assertEquals(29.39,json.getDouble("valor"),0.000001);
        assertEquals("TEMPERATURA",json.getString("tipo"));
        assertEquals(39.123456789,json.getDouble("latitud"),0);
        respuesta="{\"ok\":true,\"data\":{\"guardada\":false}}";
        try {
            logica.guardarTemperatura("token-test",m);
            fail("Sin confirmación la temperatura debe seguir pendiente");
        } catch (IOException esperado) {
            assertTrue(esperado.getMessage().contains("no ha confirmado"));
        }
    }

    private void comprobar(String esperado, String destino, String token) {
        assertEquals(esperado, metodo);
        assertEquals("/api/v1" + destino, ruta);
        assertEquals(token == null ? null : "Bearer " + token, autorizacion);
    }
}
