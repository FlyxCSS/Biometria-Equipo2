package com.example.ozora.senda;

import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.routes.RouteActivity;
import com.example.ozora.senda.routes.RouteManager;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class RouteScreensTest {
    @Test public void finalizarAbreElResumenConLasLecturasDeLaRuta() {
        android.content.Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Worker worker = new Worker(2147480020, 2147480020, "Prueba");
        SessionManager.open(worker, Long.MAX_VALUE, "solo-prueba-local");
        try (RouteManager routes = new RouteManager(context)) {
            long id = routes.iniciar(worker.id, worker.parkId, "S-001");
            try {
                Location point = new Location("test");
                point.setLatitude(39.47);
                point.setLongitude(-0.37);
                point.setTime(System.currentTimeMillis());
                point.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                routes.punto(id, point, 1, 0);
                for (int value : new int[]{51, 62, 68, 86}) {
                    routes.lectura(id, new MedicionAmbiental("S-001", value, 39.47, -0.37, 5,
                            "2026-11-13T08:31:00.000Z", value));
                }

                Intent intent = new Intent(context, RouteActivity.class).putExtra(MainActivity.EXTRA_ROUTE_ID, id);
                try (ActivityScenario<RouteActivity> ignored = ActivityScenario.launch(intent)) {
                    onView(withText(R.string.route_live)).check(matches(isDisplayed()));
                    onView(withText("4")).check(matches(isDisplayed()));
                    onView(withText(R.string.main_finish)).perform(click());
                    onView(withText(R.string.route_keep_running)).perform(click());
                    onView(withText(R.string.route_live)).check(matches(isDisplayed()));
                    onView(withText(R.string.main_finish)).perform(click());
                    onView(withText(R.string.route_confirm)).perform(click());

                    onView(withText(R.string.route_info)).check(matches(isDisplayed()));
                    onView(withText("67 ppb")).perform(scrollTo()).check(matches(isDisplayed()));
                    onView(withText(R.string.route_show_all)).perform(scrollTo(), click());
                    onView(withText(R.string.route_show_less)).check(matches(isDisplayed()));
                    onView(withText(R.string.route_temperature)).perform(scrollTo(), click());
                    onView(withText(R.string.route_no_temperature)).perform(scrollTo()).check(matches(isDisplayed()));
                }
                org.junit.Assert.assertNotNull(routes.ruta(worker, id).end);
            } finally {
                routes.finalizar(id);
                android.database.sqlite.SQLiteDatabase db = routes.getWritableDatabase();
                String[] args = {String.valueOf(id)};
                db.delete("lectura", "ruta=?", args);
                db.delete("punto", "ruta=?", args);
                db.delete("ruta", "id=?", args);
            }
        } finally {
            SessionManager.clear();
        }
    }
}
