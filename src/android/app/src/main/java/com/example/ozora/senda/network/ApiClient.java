/*
 * Nombre: ApiClient.java
 * Descripción: Cliente JSON autenticado del backend PHP.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Equipo Android y Julia (integración con asistencia de Codex).
 * Aportación: Conservar los errores del contrato para primer acceso y sincronización.
 */
package com.example.ozora.senda.network;

import android.content.Context;
import com.example.ozora.senda.R;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public final class ApiClient {
    public static final class Error extends IOException {
        public final int status;
        /** Diseño lógico: (estado HTTP, mensaje) -> Error -> error de API.
         * Descripción: Conserva el estado para decidir si reintentar o pedir acceso. */
        public Error(int status,String msg) { super(msg); this.status=status; }
    }
    final String base;
    private final boolean depuracion;
    /** Diseño lógico: (contexto) -> ApiClient -> cliente.
     * Descripción: Lee la URL pública configurada sin incluir secretos en el APK. */
    public ApiClient(Context c) {
        base=c.getString(R.string.senda_api_base).trim().replaceAll("/+$","");
        depuracion=(c.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE)!=0;
    }
    /** Diseño lógico: (URL, depuración) -> ApiClient -> cliente para pruebas del contrato.
     * Descripción: Permite comprobar el transporte Java contra PHP sin inicializar una pantalla. */
    ApiClient(String direccion,boolean debug) {
        base=direccion.trim().replaceAll("/+$","");
        depuracion=debug;
    }
    /** Diseño lógico: (método, ruta, token, JSON) -> pedir -> datos o error.
     * Descripción: Usa HTTPS, límites de tiempo y mensajes JSON del servidor. */
    public JSONObject pedir(String metodo,String ruta,String token,JSONObject datos) throws Exception {
        if(base.isEmpty()) throw new Error(0,"Falta configurar SENDA_API_BASE con la dirección HTTPS del servidor.");
        URL url=new URL(base+ruta);
        boolean emuladorLocal=depuracion && "http".equals(url.getProtocol())
                && ("10.0.2.2".equals(url.getHost()) || "127.0.0.1".equals(url.getHost()));
        if(!url.getProtocol().equals("https") && !emuladorLocal) throw new Error(0,"La dirección del servidor debe usar HTTPS.");
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        try {
            con.setConnectTimeout(8000); con.setReadTimeout(8000); con.setInstanceFollowRedirects(false);
            con.setRequestMethod(metodo);
            con.setRequestProperty("Accept","application/json");
            if(token!=null) con.setRequestProperty("Authorization","Bearer "+token);
            if(datos!=null) {
                con.setDoOutput(true); con.setRequestProperty("Content-Type","application/json");
                try(OutputStream out=con.getOutputStream()){ out.write(datos.toString().getBytes(StandardCharsets.UTF_8)); }
            }
            int status=con.getResponseCode();
            String cuerpo=leer(status>=400 ? con.getErrorStream() : con.getInputStream());
            if(status<200 || status>=300) {
                String msg=status==401 ? "Credenciales incorrectas, cuenta inactiva o sesión caducada." :
                    status==403 ? "Tu cuenta no tiene permiso para esta operación." :
                    status==429 ? "Demasiados intentos. Inténtalo más tarde." : "El servidor no pudo completar la operación ("+status+").";
                try {
                    JSONObject error=new JSONObject(cuerpo).optJSONObject("error");
                    if(error!=null) msg=error.optString("mensaje",msg);
                } catch(org.json.JSONException ignorado) { /* Mantener mensaje seguro si el proxy devuelve HTML. */ }
                throw new Error(status,msg);
            }
            JSONObject response=new JSONObject(cuerpo);
            if(!response.getBoolean("ok")) throw new Error(status,"El servidor rechazó la petición.");
            return response.getJSONObject("data");
        } finally { con.disconnect(); }
    }
    /** Diseño lógico: (flujo HTTP) -> leer -> texto.
     * Descripción: Cierra el flujo y limita el tamaño de respuesta sin registrar cuerpos. */
    private String leer(InputStream flujo) throws IOException {
        if(flujo==null) return "";
        StringBuilder texto=new StringBuilder();
        try(Reader in=new InputStreamReader(flujo,StandardCharsets.UTF_8)) {
            char[] bloque=new char[2048];
            int leidos;
            while((leidos=in.read(bloque))!=-1) {
                if(texto.length()+leidos>1_048_576) throw new IOException("Respuesta demasiado grande.");
                texto.append(bloque,0,leidos);
            }
        }
        return texto.toString();
    }
}
