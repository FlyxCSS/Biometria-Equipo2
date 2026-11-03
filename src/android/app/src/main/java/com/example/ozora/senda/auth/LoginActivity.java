package com.example.ozora.senda.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.ui.FieldScreen;
import com.example.ozora.senda.ui.HomeActivity;

public final class LoginActivity extends FieldScreen {
    private static final ExecutorService RED=Executors.newSingleThreadExecutor();
    private ApiClient api;
    private BiometricHelper bio;
    private boolean ocupado;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        api=new ApiClient(this);
        bio=new BiometricHelper(this);
        if(SessionManager.current()!=null) abrirPanel();
        else { acceso(); mensaje(getIntent().getStringExtra("mensaje")); }
    }
    private void abrirPanel() {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
    @Override protected void onDestroy() { bio.cancelar(); super.onDestroy(); }
    private void acceso() {
        pagina("Acceso de campo"); texto("Identifícate con la cuenta de tu parque.",16,false);
        EditText email=campo("Correo electrónico",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText pass=campo("Contraseña",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        contenido.addView(email); contenido.addView(pass);
        boton("Iniciar sesión",()->{
            String correo=email.getText().toString().trim(), clave=pass.getText().toString();
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() || clave.isEmpty()) {
                mensaje("Introduce un correo válido y tu contraseña."); return;
            }
            autenticar(correo,clave,null);
        });
        if(bio.activo()) boton("Acceder con biometría",()->{
            if(ocupado) return;
            if(!bio.disponible()) { mensaje("Biometría no disponible. Usa tu contraseña."); return; }
            bio.abrir(new BiometricHelper.Resultado(){
                public void aceptar(String t){ autenticar(null,null,t); }
                public void error(String s){ mensaje(s); }
            });
        });
        if(DemoAccess.available()) {
            texto("DEMOSTRACIÓN DEBUG · Cuenta y parque ficticios. Sin conexión al backend.",16,true);
            boton("Entrar en demostración (Debug)",()->{
                if(ocupado) return;
                DemoAccess.open(); abrirPanel();
            });
        }
        mensajes();
    }
    private void autenticar(String correo,String clave,String guardado) {
        if(ocupado) return;
        ocupado=true; mensaje("Comprobando la sesión…");
        RED.execute(()->{
            try {
                JSONObject d=guardado==null ? api.pedir("POST","/sesiones",null,new JSONObject().put("correo",correo).put("contrasena",clave))
                    : api.pedir("GET","/sesiones/actual",guardado,null);
                JSONObject u=d.getJSONObject("usuario");
                if(!"trabajador".equals(u.getString("rol"))) throw new ApiClient.Error(403,"Esta aplicación requiere una cuenta de trabajador.");
                String nuevo=guardado==null ? d.getString("token") : guardado;
                long fin=SessionManager.instante(d.getString("expira_en"));
                if(fin<=System.currentTimeMillis()) throw new ApiClient.Error(401,"Sesión caducada.");
                int uid=u.getInt("id_usuario"), pid=u.getInt("id_parque"); String n=u.getString("nombre");
                runOnUiThread(()->{
                    if(isDestroyed() || isFinishing()) return;
                    if(guardado==null) bio.borrar();
                    SessionManager.open(new Worker(uid,pid,n),fin,nuevo); ocupado=false; abrirPanel();
                });
            } catch(Exception e) {
                runOnUiThread(()->{
                    if(isDestroyed() || isFinishing()) return;
                    ocupado=false;
                    if(guardado!=null && e instanceof ApiClient.Error && (((ApiClient.Error)e).status==401 || ((ApiClient.Error)e).status==403)) {
                        bio.borrar(); acceso();
                    }
                    mensaje(e instanceof ApiClient.Error ? e.getMessage() : "No se pudo comprobar la sesión. Revisa la conexión y el servidor.");
                });
            }
        });
    }
}
