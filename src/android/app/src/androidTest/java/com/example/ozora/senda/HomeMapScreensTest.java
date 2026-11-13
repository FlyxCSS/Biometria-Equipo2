package com.example.ozora.senda;

import android.Manifest;
import android.graphics.Bitmap;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Worker;
import org.junit.Test;
import java.io.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

public class HomeMapScreensTest {
    @Test public void mapaYFiltrosConservanElEstadoEntreSecciones() throws Exception {
        android.app.Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        String pkg = instrumentation.getTargetContext().getPackageName();
        instrumentation.getUiAutomation().grantRuntimePermission(pkg, Manifest.permission.ACCESS_COARSE_LOCATION);
        instrumentation.getUiAutomation().grantRuntimePermission(pkg, Manifest.permission.ACCESS_FINE_LOCATION);
        // Identidad exclusiva de la prueba. No hay lecturas pendientes ni peticiones autenticadas.
        SessionManager.open(new Worker(2147480000,2147480000,"Prueba"),Long.MAX_VALUE,"solo-prueba-local");
        try (ActivityScenario<MainActivity> screen = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.home_map)).check(matches(isDisplayed()));
            onView(withId(R.id.text_account)).check(matches(withText("Hola, Prueba")));
            esperarMapa(screen);
            captura("home-normal.png");
            onView(withId(R.id.home_heat_toggle)).perform(click());
            onView(withId(R.id.home_legend)).check(matches(isDisplayed()));
            onView(withId(R.id.home_week)).perform(click());
            onView(withId(R.id.home_week)).check(matches(isChecked()));
            captura("home-heat.png");
            onView(withId(R.id.tab_historial)).perform(click());
            onView(withId(R.id.tab_inicio)).perform(click());
            onView(withId(R.id.home_legend)).check(matches(isDisplayed()));
            onView(withId(R.id.home_week)).check(matches(isChecked()));
            screen.recreate();
            onView(withId(R.id.home_legend)).check(matches(isDisplayed()));
            onView(withId(R.id.home_week)).check(matches(isChecked()));
        } finally { SessionManager.clear(); }
    }

    @Test public void calorAislaCuentaYFechasYAgrupaConcentraciones() {
        android.content.Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String file="heat-test-"+java.util.UUID.randomUUID()+".db";
        try(com.example.ozora.senda.routes.RouteManager routes = new com.example.ozora.senda.routes.RouteManager(context,file)) {
            long route=routes.iniciar(1,3,"S-001");
            long other=routes.iniciar(2,3,"S-002");
            for(int value:new int[]{20,40}) routes.lectura(route,new com.example.ozora.senda.model.MedicionAmbiental(
                    "S-001",value,39,-.1,5,"2026-11-13T12:00:00.000Z",value));
            routes.lectura(route,new com.example.ozora.senda.model.MedicionAmbiental(
                    "S-001",100,40,-.2,5,"2026-11-01T12:00:00.000Z",1));
            routes.lectura(other,new com.example.ozora.senda.model.MedicionAmbiental(
                    "S-002",999,39,-.1,5,"2026-11-13T12:00:00.000Z",1));
            try(android.database.Cursor c=routes.puntosCalor(new Worker(1,3,"Prueba"),
                    "2026-11-13T00:00:00.000Z","2026-11-13T23:59:59.999Z")) {
                assertTrue(c.moveToFirst());
                assertEquals(30,c.getDouble(2),0);
                assertFalse(c.moveToNext());
            }
            try(android.database.Cursor c=routes.puntosCalor(new Worker(1,4,"Otro parque"),
                    "2026-11-13T00:00:00.000Z","2026-11-13T23:59:59.999Z")) {
                assertFalse(c.moveToFirst());
            }
        } finally { context.deleteDatabase(file); }
    }

    private void esperarMapa(ActivityScenario<MainActivity> screen) throws Exception {
        for (int attempt=0; attempt<30; attempt++) {
            java.util.concurrent.CountDownLatch latch=new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.atomic.AtomicReference<String> value=new java.util.concurrent.atomic.AtomicReference<>();
            screen.onActivity(activity -> {
                android.webkit.WebView web=activity.findViewById(R.id.home_map);
                web.evaluateJavascript("typeof window.setPosition", result -> { value.set(result); latch.countDown(); });
            });
            assertTrue(latch.await(2,java.util.concurrent.TimeUnit.SECONDS));
            if ("\"function\"".equals(value.get())) return;
            Thread.sleep(100);
        }
        fail("El motor del mapa debe inicializarse con Leaflet incluido en el APK");
    }

    private void captura(String name) throws Exception {
        android.app.Instrumentation i=InstrumentationRegistry.getInstrumentation();
        i.waitForIdleSync();
        Bitmap bitmap=i.getUiAutomation().takeScreenshot();
        try(FileOutputStream output=new FileOutputStream(new File(i.getTargetContext().getFilesDir(),name))) {
            bitmap.compress(Bitmap.CompressFormat.PNG,100,output);
        }
        bitmap.recycle();
    }
}
