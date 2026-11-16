package com.example.ozora.senda;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.routes.RoutePeriod;
import org.junit.Test;
import java.io.*;
import java.util.TimeZone;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

public class RoutesScreenTest {
    private long insert(RouteManager db,int user,int park,long start,boolean active) {
        android.content.ContentValues value=new android.content.ContentValues();
        value.put("usuario",user); value.put("parque",park); value.put("inicio",start);
        value.put("sensor","S-001"); value.put("metros",1200);
        if(!active)value.put("fin",start+90*60*1000);
        return db.getWritableDatabase().insertOrThrow("ruta",null,value);
    }
    @Test public void filtraPorInicioYCuentaSinDuplicarLimites() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        String file="routes-test-"+java.util.UUID.randomUUID()+".db";
        try(RouteManager db=new RouteManager(context,file)) {
            insert(db,1,3,100,false); insert(db,1,3,200,false);
            insert(db,1,3,300,false); insert(db,2,3,150,false); insert(db,1,4,150,false);
            try(Cursor c=db.listarPeriodo(new Worker(1,3,"Test"),100,300,20,0)) {
                assertTrue(c.moveToNext()); assertEquals(200,c.getLong(1));
                assertTrue(c.moveToNext()); assertEquals(100,c.getLong(1)); assertFalse(c.moveToNext());
            }
            try(Cursor c=db.listarPeriodo(new Worker(1,3,"Test"),100,300,1,1)) {
                assertTrue(c.moveToNext()); assertEquals(100,c.getLong(1)); assertFalse(c.moveToNext());
            }
        } finally { context.deleteDatabase(file); }
    }

    @Test public void tarjetasYFiltrosMuestranLosDatosDelPeriodo() throws Exception {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        Worker worker=new Worker(2147479000,2147479000,"Prueba de rutas");
        long now=System.currentTimeMillis();
        long[] today=RoutePeriod.TODAY.bounds(now,TimeZone.getDefault());
        long[] yesterday=RoutePeriod.YESTERDAY.bounds(now,TimeZone.getDefault());
        try(RouteManager db=new RouteManager(context)) {
            long current=insert(db,worker.id,worker.parkId,Math.max(today[0],now-600000),true);
            long completed=insert(db,worker.id,worker.parkId,today[0],false);
            long older=insert(db,worker.id,worker.parkId,yesterday[0]+3600000,false);
            try {
                // Solo rutas de prueba sin lecturas: la sincronización no transmite credenciales ficticias.
                db.getWritableDatabase().execSQL("INSERT INTO punto(ruta,lat,lon,fecha,segmento) VALUES (?,?,?,?,?)",
                        new Object[]{current,39,-.1,now-1000,1});
                db.getWritableDatabase().execSQL("INSERT INTO punto(ruta,lat,lon,fecha,segmento) VALUES (?,?,?,?,?)",
                        new Object[]{current,39.002,-.102,now,1});
                SessionManager.open(worker,Long.MAX_VALUE,"solo-prueba-local");
                android.content.Intent intent=new android.content.Intent(context,MainActivity.class)
                        .putExtra(MainActivity.EXTRA_SHOW_ROUTES,true);
                try(ActivityScenario<MainActivity> screen=ActivityScenario.launch(intent)) {
                    onView(withId(R.id.routes_today)).check(matches(isChecked()));
                    onView(withId(R.id.routes_primary_action)).check(matches(withText(R.string.main_finish)));
                    screen.onActivity(activity -> assertEquals(2,((android.widget.LinearLayout)activity.findViewById(R.id.list_routes)).getChildCount()));
                    capture("routes-today.png");
                    onView(withId(R.id.routes_yesterday)).perform(click());
                    screen.onActivity(activity -> assertEquals(1,((android.widget.LinearLayout)activity.findViewById(R.id.list_routes)).getChildCount()));
                    capture("routes-yesterday.png");
                    screen.recreate();
                    onView(withId(R.id.routes_yesterday)).check(matches(isChecked()));
                    onView(withId(R.id.routes_seven)).perform(click());
                    screen.onActivity(activity -> assertEquals(3,((android.widget.LinearLayout)activity.findViewById(R.id.list_routes)).getChildCount()));
                }
            } finally {
                SessionManager.clear();
                db.getWritableDatabase().execSQL("DELETE FROM punto WHERE ruta IN (?,?,?)",new Object[]{current,completed,older});
                db.getWritableDatabase().execSQL("DELETE FROM ruta WHERE id IN (?,?,?)",new Object[]{current,completed,older});
            }
        }
    }
    private void capture(String name) throws Exception {
        android.app.Instrumentation i=InstrumentationRegistry.getInstrumentation();
        i.waitForIdleSync();
        Bitmap image=i.getUiAutomation().takeScreenshot();
        try(FileOutputStream output=new FileOutputStream(new File(i.getTargetContext().getFilesDir(),name))) {
            image.compress(Bitmap.CompressFormat.PNG,100,output);
        }
        image.recycle();
    }
}
