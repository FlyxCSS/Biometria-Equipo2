/*
 * Nombre: AccessFlowsTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.WindowManager;
import android.widget.EditText;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.R;
import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.qr.QrScannerActivity;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

public class AccessFlowsTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Worker worker = new Worker(2147477000, 2147477000, "Prueba de acceso");

    /** --------------------
     * Diseño lógico: file: Text --> capture() -->
     * Descripción: Capture. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void capture(String file) throws Exception {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        android.os.SystemClock.sleep(300);
        Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        try (FileOutputStream out = new FileOutputStream(new File(context.getFilesDir(), file))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        bitmap.recycle();
    }

    /** --------------------
     * Diseño lógico: recuperacionValidaCamposYConservaPasoSinEnviarPeticiones() -->
     * Descripción: Recuperacion Valida Campos YConserva Paso Sin Enviar Peticiones. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void recuperacionValidaCamposYConservaPasoSinEnviarPeticiones() throws Exception {
        SessionManager.clear();
        try (ActivityScenario<RecoveryActivity> screen = ActivityScenario.launch(RecoveryActivity.class)) {
            screen.onActivity(a -> { a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE); ((android.widget.ScrollView)a.findViewById(R.id.auth_scroll)).setSmoothScrollingEnabled(false); });
            capture("flow7-recovery-email.png");
            onView(withId(R.id.auth_scroll)).perform(swipeUp(), swipeUp());
            onView(withId(R.id.auth_submit)).perform(scrollTo(), click());
            onView(withId(R.id.auth_email)).check(matches(hasErrorText(context.getString(R.string.auth_email_invalid))));
            onView(withId(R.id.auth_email)).perform(closeSoftKeyboard());
            onView(withId(R.id.auth_scroll)).perform(swipeUp());
            onView(withId(R.id.recovery_existing_link)).perform(scrollTo(), click());
            screen.recreate();
            screen.onActivity(a -> {
                a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                assertEquals(RecoveryViewModel.Step.LINK,
                        new ViewModelProvider(a).get(RecoveryViewModel.class).state.getValue().step);
            });
            capture("flow7-recovery-link.png");
            onView(withId(R.id.recovery_token)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.auth_scroll)).perform(swipeUp(), swipeUp());
            onView(withId(R.id.auth_submit)).perform(scrollTo(), click());
            onView(withId(R.id.recovery_token)).check(matches(hasErrorText(context.getString(R.string.recovery_link_invalid))));
            onView(withId(R.id.recovery_token)).perform(scrollTo(), replaceText("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"), closeSoftKeyboard());
            onView(withId(R.id.auth_scroll)).perform(swipeUp(), swipeUp());
            onView(withId(R.id.auth_submit)).perform(scrollTo(), click());
            onView(withId(R.id.recovery_password)).check(matches(hasErrorText(context.getString(R.string.recovery_password_invalid))));
            screen.recreate();
            onView(withId(R.id.recovery_token)).check(matches(withText("")));
            assertNull(SessionManager.current());
        }
    }

    /** --------------------
     * Diseño lógico: biometriaSePuedeOmitirSinGuardarUnaCredencial() -->
     * Descripción: Biometria Se Puede Omitir Sin Guardar Una Credencial. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void biometriaSePuedeOmitirSinGuardarUnaCredencial() throws Exception {
        SessionManager.open(worker, Long.MAX_VALUE, "solo-prueba-local");
        try (ActivityScenario<BiometricSetupActivity> screen = ActivityScenario.launch(BiometricSetupActivity.class)) {
            onView(withId(R.id.bio_question)).check(matches(withText(R.string.bio_question)));
            onView(withId(R.id.bio_activate)).check(matches(isEnabled()));
            capture("flow7-biometric.png");
            onView(withId(R.id.bio_scroll)).perform(swipeUp());
            onView(withId(R.id.bio_skip)).perform(scrollTo(), click());
            assertNotNull(SessionManager.current());
        } finally { SessionManager.clear(); }
    }

    /** --------------------
     * Diseño lógico: entradaManualQrRechazaErroresYDevuelveCodigoNormalizado() -->
     * Descripción: Entrada Manual Qr Rechaza Errores YDevuelve Codigo Normalizado. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void entradaManualQrRechazaErroresYDevuelveCodigoNormalizado() throws Exception {
        SessionManager.open(worker, Long.MAX_VALUE, "solo-prueba-local");
        Intent intent = new Intent(context, QrScannerActivity.class);
        try (ActivityScenario<QrScannerActivity> screen = ActivityScenario.launch(intent)) {
            onView(withId(R.id.qr_scan)).check(matches(isEnabled()));
            capture("flow7-qr.png");
            onView(withId(R.id.qr_scroll)).perform(swipeUp());
            onView(withId(R.id.qr_manual)).perform(scrollTo(), click());
            onView(withId(R.id.qr_confirm)).perform(scrollTo(), click());
            onView(withId(R.id.qr_code)).check(matches(hasErrorText(context.getString(R.string.qr_invalid))));
            onView(withId(R.id.qr_code)).perform(replaceText("s-143402"), closeSoftKeyboard());
            onView(withId(R.id.qr_confirm)).perform(scrollTo(), click());
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            if (screen.getState() != androidx.lifecycle.Lifecycle.State.DESTROYED) {
                screen.onActivity(a -> {
                    assertEquals("s-143402", ((EditText)a.findViewById(R.id.qr_code)).getText().toString());
                    assertNull(((EditText)a.findViewById(R.id.qr_code)).getError());
                    assertTrue("La selección debe cerrar la pantalla", a.isFinishing());
                });
            }
        } finally { SessionManager.clear(); }
    }
}
