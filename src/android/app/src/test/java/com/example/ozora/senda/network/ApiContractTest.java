/*
 * Nombre: ApiContractTest.java
 * Descripción: Prueba HTTP real de Java contra el backend y MySQL de pruebas.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Validar primer acceso, revocación, envío y lectura del jefe con el cliente Android.
 */
package com.example.ozora.senda.network;

import com.example.ozora.senda.model.MedicionAmbiental;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApiContractTest {
    /** Diseño lógico: (cliente, correo, clave) -> entrar -> sesión JSON.
     * Descripción: Usa exactamente el transporte HTTP de Android sin imprimir tokens. */
    private JSONObject entrar(ApiClient api,String correo,String clave) throws Exception {
        return api.pedir("POST","/sesiones",null,new JSONObject().put("correo",correo).put("contrasena",clave));
    }
    /** Diseño lógico: () -> trabajadorEnviaYJefeRecibe -> vacío.
     * Descripción: Solo se ejecuta con la fixture local explícita; las cuentas son distintas por variante. */
    @Test public void trabajadorEnviaYJefeRecibe() throws Exception {
        String ruta=System.getProperty("senda.contract.fixture");
        Assume.assumeTrue("Preparar fixture local para probar HTTP real",ruta!=null);
        JSONObject fixture=new JSONObject(new String(Files.readAllBytes(Paths.get(ruta)),java.nio.charset.StandardCharsets.UTF_8));
        JSONObject f=fixture.getJSONObject(System.getProperty("senda.variant","debug"));
        assertTrue(fixture.getString("base").startsWith("http://127.0.0.1:"));
        ApiClient api=new ApiClient(fixture.getString("base"),true);
        JSONObject sesion=entrar(api,f.getString("correo"),f.getString("inicial"));
        assertEquals("trabajador",sesion.getJSONObject("usuario").getString("rol"));
        assertTrue(sesion.getJSONObject("usuario").getBoolean("debe_cambiar_contrasena"));
        String inicial=sesion.getString("token");
        assertNotNull(api.pedir("GET","/sesiones/actual",inicial,null).getJSONObject("usuario"));
        String fecha=DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(java.time.ZoneOffset.UTC).format(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        JSONObject medicion=new JSONObject(MeasurementPayload.datos(new MedicionAmbiental(f.getString("ble"),123,
                39.1234567890123,-0.1234567890123,5,fecha,8)));
        try { api.pedir("POST","/mediciones",inicial,medicion); fail("No debe operar sin cambiar la clave inicial"); }
        catch(ApiClient.Error e) { assertEquals(403,e.status); }
        assertTrue(api.pedir("POST","/usuarios/actual/contrasena",inicial,new JSONObject()
                .put("contrasena_actual",f.getString("inicial")).put("contrasena_nueva",f.getString("nueva"))).getBoolean("actualizada"));
        try { api.pedir("GET","/sesiones/actual",inicial,null); fail("El token anterior debe quedar revocado"); }
        catch(ApiClient.Error e) { assertEquals(401,e.status); }
        sesion=entrar(api,f.getString("correo"),f.getString("nueva"));
        assertFalse(sesion.getJSONObject("usuario").getBoolean("debe_cambiar_contrasena"));
        String token=sesion.getString("token");
        assertFalse(api.pedir("POST","/mediciones",token,medicion).getBoolean("duplicada"));
        assertTrue(api.pedir("POST","/mediciones",token,medicion).getBoolean("duplicada"));
        String jefe=entrar(api,f.getString("correoJefe"),f.getString("claveJefe")).getString("token");
        JSONObject lectura=api.pedir("GET","/parques/"+f.getInt("parque")+"/mediciones/ultima",jefe,null).getJSONObject("medicion");
        assertEquals(f.getString("ble"),lectura.getString("nombre_micro"));
        assertEquals(123,lectura.getInt("valor"));
        assertEquals(medicion.getDouble("latitud"),lectura.getDouble("latitud"),0);
        assertEquals(fecha,lectura.getString("fecha_hora"));
        api.pedir("DELETE","/sesiones/actual",token,null);
        api.pedir("DELETE","/sesiones/actual",jefe,null);
    }
    /** Diseño lógico: () -> releaseRechazaHttp -> vacío.
     * Descripción: La excepción de HTTP local requiere explícitamente depuración. */
    @Test public void releaseRechazaHttp() throws Exception {
        try { new ApiClient("http://127.0.0.1:8081/api/v1",false).pedir("GET","/sesiones/actual",null,null); fail(); }
        catch(ApiClient.Error e) { assertEquals(0,e.status); }
    }
}
