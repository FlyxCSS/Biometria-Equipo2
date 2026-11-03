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
        public Error(int status,String msg) { super(msg); this.status=status; }
    }
    final String base;
    private final boolean depuracion;
    public ApiClient(Context c) {
        base=c.getString(R.string.senda_api_base).trim().replaceAll("/+$","");
        depuracion=(c.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE)!=0;
    }
    public JSONObject pedir(String metodo,String ruta,String token,JSONObject datos) throws Exception {
        if(base.isEmpty()) throw new Error(0,"Falta configurar SENDA_API_BASE con la dirección HTTPS del servidor.");
        URL url=new URL(base+ruta);
        boolean emuladorLocal=depuracion && "http".equals(url.getProtocol()) && "10.0.2.2".equals(url.getHost());
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
            if(status<200 || status>=300) {
                String msg=status==401 ? "Credenciales incorrectas, cuenta inactiva o sesión caducada." :
                    status==403 ? "Tu cuenta no tiene permiso para esta operación." :
                    status==429 ? "Demasiados intentos. Inténtalo más tarde." : "El servidor no pudo completar la operación ("+status+").";
                throw new Error(status,msg);
            }
            StringBuilder body=new StringBuilder();
            try(BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream(),StandardCharsets.UTF_8))) {
                String line;
                while((line=in.readLine())!=null) body.append(line);
            }
            JSONObject response=new JSONObject(body.toString());
            if(!response.getBoolean("ok")) throw new Error(status,"El servidor rechazó la petición.");
            return response.getJSONObject("data");
        } finally { con.disconnect(); }
    }
}
