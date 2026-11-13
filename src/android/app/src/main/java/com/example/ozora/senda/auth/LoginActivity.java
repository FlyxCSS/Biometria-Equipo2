/*
 * Nombre: LoginActivity.java
 * Descripción: Acceso de trabajador y cambio obligatorio de contraseña inicial.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Equipo Android y Julia (integración con asistencia de Codex).
 * Aportación: Impedir entrar al panel hasta completar el primer cambio en PHP.
 */
package com.example.ozora.senda.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.text.method.PasswordTransformationMethod;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.R;
import android.view.inputmethod.EditorInfo;
import com.example.ozora.senda.MainActivity;

public final class LoginActivity extends AuthScreen {
    private static final ExecutorService RED=Executors.newSingleThreadExecutor();
    private ApiClient api;
    private BiometricHelper bio;
    private boolean ocupado;
    private String tokenInicial;
    private long venceInicial;
    /** Diseño lógico: (estado) -> onCreate -> vacío.
     * Descripción: Prepara acceso o reutiliza exclusivamente una sesión ya admitida. */
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        api=new ApiClient(this);
        bio=new BiometricHelper(this);
        if(SessionManager.current()!=null) abrirPanel();
        else { acceso(); mensaje(getIntent().getStringExtra("mensaje")); }
    }
    /** Diseño lógico: () -> abrirPanel -> vacío.
     * Descripción: Abre el cuaderno tras un login sin cambio pendiente. */
    private void abrirPanel() {
        startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
    /** Diseño lógico: () -> onDestroy -> vacío.
     * Descripción: Cancela la biometría y descarta el token temporal de primer acceso. */
    @Override protected void onDestroy() { tokenInicial=null; bio.cancelar(); super.onDestroy(); }
    private void acceso() {
        mostrarFormulario();
        findViewById(R.id.auth_submit).setOnClickListener(v -> entrar());
        password.setOnEditorActionListener((v, action, event) -> {
            if (action != EditorInfo.IME_ACTION_DONE) return false;
            entrar();
            return true;
        });
        findViewById(R.id.auth_forgot).setOnClickListener(v -> {
            if (!ocupado) startActivity(new Intent(this, RecoveryActivity.class)
                    .putExtra("correo", email.getText().toString().trim()));
        });
        findViewById(R.id.auth_biometric).setOnClickListener(v -> {
            if (ocupado) return;
            if (!bio.activo()) { mensaje(getString(R.string.auth_bio_setup)); return; }
            if (!bio.disponible()) { mensaje("Biometría no disponible. Usa tu contraseña."); return; }
            bio.abrir(new BiometricHelper.Resultado() {
                public void aceptar(String token) { autenticar(null, null, token); }
                public void error(String texto) { mensaje(texto); }
            });
        });
    }

    private void entrar() {
        if (ocupado) return;
        String correo = email.getText().toString().trim();
        String clave = password.getText().toString();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            email.setError(getString(R.string.auth_email_invalid));
            email.requestFocus();
            return;
        }
        if (clave.isEmpty()) {
            password.setError("Introduce tu contraseña.");
            password.requestFocus();
            return;
        }
        autenticar(correo, clave, null);
    }

    /** Diseño lógico: (correo, clave, token biométrico) -> autenticar -> vacío.
     * Descripción: Verifica sesión y deriva al primer cambio antes de guardar el acceso al panel. */
    private void autenticar(String correo,String clave,String guardado) {
        if(ocupado) return;
        ocupado=true; mostrarEspera(true); mensaje(getString(R.string.auth_busy));
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
                if (uid <= 0 || pid <= 0 || nuevo.trim().isEmpty())
                    throw new ApiClient.Error(401, "La respuesta del servidor no contiene una sesión válida.");
                boolean cambioInicial=u.optBoolean("debe_cambiar_contrasena",false);
                runOnUiThread(()->{
                    if(isDestroyed() || isFinishing()) return;
                    mostrarEspera(false);
                    password.setText("");
                    if(guardado==null) bio.borrar();
                    if(cambioInicial) {
                        bio.borrar();
                        tokenInicial=nuevo; venceInicial=fin; ocupado=false;
                        primerAcceso(correo);
                        return;
                    }
                    SessionManager.open(new Worker(uid,pid,n),fin,nuevo); ocupado=false; abrirPanel();
                });
            } catch(Exception e) {
                runOnUiThread(()->{
                    if(isDestroyed() || isFinishing()) return;
                    ocupado=false; mostrarEspera(false);
                    if(guardado!=null && e instanceof ApiClient.Error && (((ApiClient.Error)e).status==401 || ((ApiClient.Error)e).status==403)) {
                        bio.borrar(); acceso();
                    }
                    mensaje(e instanceof ApiClient.Error ? e.getMessage() : "No se pudo comprobar la sesión. Revisa la conexión y el servidor.");
                });
            }
        });
    }

    /** Diseño lógico: (campo contraseña) -> agregarClave -> vacío.
     * Descripción: Añade un icono de ojo con nombre accesible sin cambiar el texto escrito. */
    private void agregarClave(EditText clave) {
        LinearLayout fila=new LinearLayout(this);
        fila.setGravity(android.view.Gravity.CENTER_VERTICAL);
        fila.addView(clave,new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        ImageButton ojo=new ImageButton(this);
        ojo.setImageResource(android.R.drawable.ic_menu_view);
        ojo.setContentDescription("Mostrar contraseña");
        ojo.setOnClickListener(v->{
            boolean mostrar=clave.getTransformationMethod() instanceof PasswordTransformationMethod;
            clave.setTransformationMethod(mostrar ? null : PasswordTransformationMethod.getInstance());
            clave.setSelection(clave.length());
            ojo.setContentDescription(mostrar ? "Ocultar contraseña" : "Mostrar contraseña");
        });
        fila.addView(ojo,new LinearLayout.LayoutParams(dp(48),dp(48)));
        contenido.addView(fila);
    }

    /** Diseño lógico: (correo utilizado) -> primerAcceso -> vacío.
     * Descripción: Solicita la clave actual y la nueva; el token temporal no abre recorridos ni biometría. */
    private void primerAcceso(String correo) {
        pagina("Cambia tu contraseña inicial");
        texto("Antes de comenzar, elige una contraseña propia de 12 a 72 bytes UTF-8.",16,false);
        EditText actual=campo("Contraseña inicial",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText nueva=campo("Nueva contraseña",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText repetir=campo("Repetir nueva contraseña",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        agregarClave(actual); agregarClave(nueva); agregarClave(repetir);
        boton("Guardar nueva contraseña",()->{
            if(ocupado) return;
            String valor=nueva.getText().toString(), anterior=actual.getText().toString();
            int bytes=valor.getBytes(StandardCharsets.UTF_8).length;
            if(bytes<12 || bytes>72 || !valor.equals(repetir.getText().toString()) || valor.equals(anterior)) {
                mensaje("La nueva clave debe coincidir en ambos campos, ser distinta de la inicial y ocupar entre 12 y 72 bytes UTF-8.");
                return;
            }
            if(tokenInicial==null || System.currentTimeMillis()>=venceInicial) {
                tokenInicial=null; acceso(); mensaje("La sesión ha caducado. Vuelve a entrar con tu contraseña inicial."); return;
            }
            ocupado=true;
            String token=tokenInicial;
            mensaje("Guardando la nueva contraseña…");
            RED.execute(()->{
                try {
                    api.pedir("POST","/usuarios/actual/contrasena",token,new JSONObject()
                            .put("contrasena_actual",anterior).put("contrasena_nueva",valor));
                    runOnUiThread(()->{
                        if(isDestroyed() || isFinishing()) return;
                        tokenInicial=null; ocupado=false; SessionManager.clear(); bio.borrar();
                        actual.setText(""); nueva.setText(""); repetir.setText("");
                        acceso(); mensaje("Contraseña cambiada. Inicia sesión con la nueva contraseña.");
                    });
                } catch(Exception error) {
                    runOnUiThread(()->{
                        if(isDestroyed() || isFinishing()) return;
                        ocupado=false;
                        if(error instanceof ApiClient.Error && ((ApiClient.Error)error).status==401) {
                            tokenInicial=null; acceso();
                        }
                        mensaje(error instanceof ApiClient.Error ? error.getMessage() : "No se pudo confirmar el cambio. Revisa la conexión; si ya se guardó, entra con la nueva clave.");
                    });
                }
            });
        });
        boton("Volver al acceso",()->{
            if(ocupado) return;
            String anterior=tokenInicial; tokenInicial=null;
            if(anterior!=null) RED.execute(()->{
                try { api.pedir("DELETE","/sesiones/actual",anterior,null); }
                catch(Exception ignorado) { /* El token sigue limitado por su caducidad en PHP. */ }
            });
            acceso();
        });
        mensajes();
    }
}
