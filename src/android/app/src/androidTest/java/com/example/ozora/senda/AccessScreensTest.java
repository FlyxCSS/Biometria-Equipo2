/*
 * Nombre: AccessScreensTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda;

import android.graphics.Bitmap;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.auth.Login;
import com.example.ozora.senda.auth.SessionManager;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

public class AccessScreensTest {

    /** --------------------
     * Diseño lógico: loginReutilizaLaSesionVigente() -->
     * Descripción: Login Reutiliza La Sesion Vigente. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void loginReutilizaLaSesionVigente() {
        SessionManager.open(new com.example.ozora.senda.model.Worker(1, 1, "Trabajador"),
                Long.MAX_VALUE, "token-de-prueba");
        try (ActivityScenario<Login> activity = ActivityScenario.launch(Login.class)) {
            onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
        } finally { SessionManager.clear(); }
    }

    /** --------------------
     * Diseño lógico: nombre: Text --> captura() -->
     * Descripción: Captura. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void captura(String nombre) throws Exception {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        File archivo = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir(), nombre);
        try (FileOutputStream stream = new FileOutputStream(archivo)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
        bitmap.recycle();
    }


    /** --------------------
     * Diseño lógico: accesoYRecuperacionValidanSinCrearSesion() -->
     * Descripción: Acceso YRecuperacion Validan Sin Crear Sesion. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void accesoYRecuperacionValidanSinCrearSesion() throws Exception {
        SessionManager.clear();
        try (ActivityScenario<Login> activity = ActivityScenario.launch(Login.class)) {
            activity.onActivity(screen -> ((android.widget.ScrollView) screen.findViewById(R.id.auth_scroll)).setSmoothScrollingEnabled(false));
            onView(withId(R.id.auth_title)).check(matches(withText(R.string.auth_title)));
            captura("login.png");
            onView(withId(R.id.auth_submit)).perform(scrollTo(), click());
            onView(withId(R.id.auth_email)).check(matches(hasErrorText(
                    InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.auth_email_invalid))));
            assertNull(SessionManager.current());
            onView(withId(R.id.auth_email)).perform(replaceText("trabajador@example.org"), closeSoftKeyboard());
            onView(withId(R.id.auth_forgot)).perform(scrollTo(), click());
            onView(withId(R.id.auth_title)).check(matches(withText(R.string.auth_recovery_title)));
            onView(withId(R.id.auth_email)).check(matches(withText("trabajador@example.org")));
            captura("recovery.png");
            onView(withId(R.id.auth_email)).perform(replaceText("correo-invalido"), closeSoftKeyboard());
            onView(withId(R.id.auth_submit)).perform(scrollTo(), click());
            onView(withId(R.id.auth_email)).check(matches(hasErrorText(
                    InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.auth_email_invalid))));
            pressBack();
            onView(withId(R.id.auth_title)).check(matches(withText(R.string.auth_title)));
            assertNull(SessionManager.current());
        }
    }
}
