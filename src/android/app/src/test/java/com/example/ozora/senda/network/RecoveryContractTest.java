package com.example.ozora.senda.network;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecoveryContractTest {
    /** Servidor HTTP aislado para probar el transporte sin enviar correos reales. */
    private String responder(ServerSocket server, int status, String response) throws Exception {
        server.setSoTimeout(5000);
        try (Socket socket = server.accept()) {
            socket.setSoTimeout(5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String request = reader.readLine();
            int length = 0;
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                assertFalse(line.toLowerCase().startsWith("authorization:"));
                if (line.toLowerCase().startsWith("content-length:")) length = Integer.parseInt(line.split(":")[1].trim());
            }
            char[] body = new char[length];
            int offset = 0;
            while (offset < length) {
                int count = reader.read(body, offset, length - offset);
                if (count < 0) throw new EOFException();
                offset += count;
            }
            assertEquals("POST /api/v1/recuperaciones HTTP/1.1", request);
            assertEquals("prueba@example.org", new JSONObject(new String(body)).getString("correo"));
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            OutputStream output = socket.getOutputStream();
            output.write(("HTTP/1.1 " + status + " Response\r\nContent-Type: application/json\r\nContent-Length: "
                    + bytes.length + "\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
            output.write(bytes);
            output.flush();
            return request;
        }
    }

    @Test public void solicitudAceptadaYLimiteDeIntentos() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            for (int status : new int[]{202, 429}) {
                try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))) {
                    String reply = status == 202
                            ? "{\"ok\":true,\"data\":{\"mensaje\":\"Solicitud recibida\"}}"
                            : "{\"ok\":false,\"error\":{\"mensaje\":\"Inténtalo más tarde.\"}}";
                    Future<String> handled = executor.submit(() -> responder(server, status, reply));
                    ApiClient api = new ApiClient("http://127.0.0.1:" + server.getLocalPort() + "/api/v1", true);
                    try {
                        JSONObject result = api.pedir("POST", "/recuperaciones", null,
                                new JSONObject().put("correo", "prueba@example.org"));
                        assertEquals(202, status);
                        assertEquals("Solicitud recibida", result.getString("mensaje"));
                    } catch (ApiClient.Error error) {
                        assertEquals(429, status);
                        assertEquals(429, error.status);
                        assertEquals("Inténtalo más tarde.", error.getMessage());
                    }
                    handled.get(5, TimeUnit.SECONDS);
                }
            }
        } finally { executor.shutdownNow(); }
    }
}
