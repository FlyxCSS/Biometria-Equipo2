package com.example.ozora.senda;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.ui.AccountActivity;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.routes.RutaService;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.not;

public class AccountScreensTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Worker worker = new Worker(2147478000, 2147478000, "Trabajadora de prueba");
    private SharedPreferences prefs() { return context.getSharedPreferences("campo-" + worker.id + "-" + worker.parkId, Context.MODE_PRIVATE); }
    private void open() { SessionManager.open(worker, Long.MAX_VALUE, "solo-prueba-local"); }
    private Intent section(int tab) { return new Intent(context, MainActivity.class).putExtra(MainActivity.EXTRA_SECTION, tab); }
    @Test public void llaveroSinVincularDesvinculacionYAjustesDelPerfil() throws Exception {
        prefs().edit().clear().commit();
        open();
        try {
            try (ActivityScenario<MainActivity> screen = ActivityScenario.launch(section(R.id.tab_sensor))) {
                onView(withId(R.id.keyring_status)).check(matches(withText(R.string.keyring_none)));
                onView(withId(R.id.button_select_sensor)).check(matches(withText(R.string.keyring_scan)));
                capture("keyring-empty.png");
                screen.onActivity(a -> prefs().edit().putString("sensor", "S-001").putLong("sensor_linked_at", System.currentTimeMillis()).commit());
                screen.recreate();
                onView(withId(R.id.keyring_id)).check(matches(withText("S-001")));
                onView(withId(R.id.keyring_status)).check(matches(withText(R.string.keyring_silent)));
                onView(withId(R.id.text_last_o3)).check(matches(withText(R.string.keyring_last)));
                capture("keyring-selected.png");
                onView(withId(R.id.button_select_sensor)).perform(scrollTo(), click());
                onView(withText("Cancelar")).perform(click());
                assertEquals("S-001", prefs().getString("sensor", null));
                onView(withId(R.id.button_select_sensor)).perform(scrollTo(), click());
                onView(withText("Desvincular")).perform(click());
                onView(withId(R.id.keyring_status)).check(matches(withText(R.string.keyring_none)));
                assertFalse(prefs().contains("sensor_linked_at"));
            }
            open();
            prefs().edit().putString("account_email", "trabajadora@example.invalid").commit();
            try (ActivityScenario<MainActivity> screen = ActivityScenario.launch(section(R.id.tab_mas))) {
                onView(withId(R.id.text_account)).check(matches(withText(worker.name)));
                capture("account-settings.png");
                onView(withId(R.id.button_profile)).perform(scrollTo(), click());
                onView(withId(R.id.profile_name)).check(matches(withText(worker.name)));
                onView(withId(R.id.profile_email)).check(matches(withText("trabajadora@example.invalid")));
                capture("account-profile.png");
                onView(withId(R.id.profile_name)).perform(scrollTo(), replaceText("Judith de prueba"), closeSoftKeyboard());
                onView(withId(R.id.account_root)).perform(swipeUp());
                onView(withId(R.id.profile_phone)).perform(scrollTo(), replaceText("+34 600 123 456"), closeSoftKeyboard());
                onView(withId(R.id.profile_save)).perform(scrollTo(), click());
                assertEquals("Judith de prueba", prefs().getString("profile_name", null));
                assertEquals("+34 600 123 456", prefs().getString("profile_phone", null));
                pressBack();
                onView(withId(R.id.text_account)).check(matches(withText("Judith de prueba")));
            }
        } finally { prefs().edit().clear().commit(); SessionManager.clear(); }
    }
    @Test public void unRecorridoActivoImpideDesvincularYLaRecepcionCaduca() throws Exception {
        open();
        prefs().edit().putString("sensor", "S-001").commit();
        try (RouteManager db = new RouteManager(context)) {
            long route = db.iniciar(worker.id, worker.parkId, "S-001");
            RutaService.actual = route;
            RutaService.ultimoAnuncio = android.os.SystemClock.elapsedRealtime();
            try (ActivityScenario<MainActivity> screen = ActivityScenario.launch(section(R.id.tab_sensor))) {
                onView(withId(R.id.button_select_sensor)).check(matches(not(isEnabled())));
                onView(withId(R.id.keyring_status)).check(matches(withText(R.string.keyring_receiving)));
                capture("keyring-receiving.png");
                screen.onActivity(a -> RutaService.ultimoAnuncio = android.os.SystemClock.elapsedRealtime() - 30000);
                screen.recreate();
                onView(withId(R.id.keyring_status)).check(matches(withText(R.string.keyring_silent)));
            } finally {
                RutaService.actual = 0; RutaService.ultimoAnuncio = 0;
                db.getWritableDatabase().delete("ruta", "id=?", new String[]{String.valueOf(route)});
                prefs().edit().clear().commit(); SessionManager.clear();
            }
        }
    }
    private void capture(String name) throws Exception {
        android.app.Instrumentation i = InstrumentationRegistry.getInstrumentation();
        i.waitForIdleSync();
        Bitmap image = i.getUiAutomation().takeScreenshot();
        try (FileOutputStream output = new FileOutputStream(new File(context.getFilesDir(), name))) {
            image.compress(Bitmap.CompressFormat.PNG, 100, output);
        }
        image.recycle();
    }
}