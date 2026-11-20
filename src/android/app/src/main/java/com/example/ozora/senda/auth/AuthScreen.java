/*
 * Nombre: AuthScreen.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.auth;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.ui.FieldScreen;

/** Presentación compartida del acceso y la solicitud de recuperación. */
public abstract class AuthScreen extends FieldScreen {
    protected EditText email;
    protected EditText password;
    private TextView feedback;


    /** --------------------
     * Diseño lógico: mostrarFormulario() -->
     * Descripción: Mostrar Formulario. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    protected void mostrarFormulario() {
        setContentView(R.layout.activity_auth);
        protegerBarras(findViewById(R.id.auth_scroll));
        androidx.core.view.WindowInsetsControllerCompat bars =
                new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        bars.setAppearanceLightStatusBars(true);
        bars.setAppearanceLightNavigationBars(false);
        View hero = findViewById(R.id.auth_hero);
        android.view.ViewGroup.LayoutParams heroSize = hero.getLayoutParams();
        heroSize.height = dp(Math.max(160, Math.min(230, getResources().getConfiguration().screenHeightDp - 570)));
        hero.setLayoutParams(heroSize);
        email = findViewById(R.id.auth_email);
        password = findViewById(R.id.auth_password);
        ojoContrasena(password);
        feedback = findViewById(R.id.auth_feedback);
    }


    /** --------------------
     * Diseño lógico: titulo: Text --> pagina() -->
     * Descripción: Pagina. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void pagina(String titulo) {
        feedback = null;
        super.pagina(titulo);
    }


    /** --------------------
     * Diseño lógico: texto: Text --> mensaje() -->
     * Descripción: Mensaje. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void mensaje(String texto) {
        if (feedback == null) {
            super.mensaje(texto);
            return;
        }
        if (isFinishing() || isDestroyed()) return;
        feedback.setText(texto);
        feedback.setVisibility(texto == null || texto.isEmpty() ? View.GONE : View.VISIBLE);
    }


    /** --------------------
     * Diseño lógico: esperando: B --> mostrarEspera() -->
     * Descripción: Mostrar Espera. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    protected void mostrarEspera(boolean esperando) {
        View progress = findViewById(R.id.auth_progress);
        if (progress == null) return;
        progress.setVisibility(esperando ? View.VISIBLE : View.GONE);
        for (int id : new int[]{R.id.auth_submit, R.id.auth_biometric, R.id.auth_forgot}) {
            findViewById(id).setEnabled(!esperando);
        }
        email.setEnabled(!esperando);
        password.setEnabled(!esperando);
    }
}
