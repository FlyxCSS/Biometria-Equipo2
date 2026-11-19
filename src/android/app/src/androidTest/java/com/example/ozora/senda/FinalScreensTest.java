package com.example.ozora.senda;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.network.MeasurementSync;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.ui.AccountActivity;
import com.example.ozora.senda.ui.PermissionsActivity;
import org.junit.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

/** Ejecutar con aplicación aislada y SENDA_API_BASE=http://127.0.0.1:18765/api/v1. */
public class FinalScreensTest {
    private final Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Worker worker=new Worker(2147477000,2147477000,"Prueba ELIA");
    private ServerSocket server;
    private Thread thread;
    private final AtomicInteger posts=new AtomicInteger();
    private volatile int postStatus=200;
    private volatile String history="{\"mediciones\":[{\"valor\":42,\"fecha_hora\":\"2026-11-19T10:00:00.000Z\",\"nombre_micro\":\"S-PRUEBA\",\"simulada\":false}],\"siguiente\":null}";
    @Before public void setup() throws Exception {
        Assume.assumeTrue("Solo instalación aislada",context.getPackageName().endsWith(".elia.validation"));
        assertEquals("http://127.0.0.1:18765/api/v1",context.getString(R.string.senda_api_base));
        SessionManager.open(worker,Long.MAX_VALUE,"token-local-prueba");
        server=new ServerSocket(18765,20,InetAddress.getByName("127.0.0.1"));
        thread=new Thread(() -> {
            while(!server.isClosed()) try(Socket socket=server.accept()) {
                socket.setSoTimeout(5000);
                BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
                String request=reader.readLine(),line;int length=0;
                while((line=reader.readLine())!=null && !line.isEmpty()) if(line.toLowerCase().startsWith("content-length:")) length=Integer.parseInt(line.substring(15).trim());
                for(int i=0;i<length;i++) reader.read();
                int status=503;String data="{}";
                if(request.startsWith("GET /api/v1/usuarios/actual/mediciones")) { status=200;data=history; }
                if(request.startsWith("POST /api/v1/mediciones ")) { status=postStatus;posts.incrementAndGet();data="{\"guardada\":true}"; }
                String body=status==200?"{\"ok\":true,\"data\":"+data+"}":"{\"ok\":false,\"error\":{\"mensaje\":\"Error de prueba\"}}";
                byte[] bytes=body.getBytes(StandardCharsets.UTF_8);
                socket.getOutputStream().write(("HTTP/1.1 "+status+" Test\r\nContent-Type: application/json\r\nContent-Length: "+bytes.length+"\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
                socket.getOutputStream().write(bytes);
            } catch(IOException error) { if(!server.isClosed()) throw new RuntimeException(error); }
        }); thread.start();
    }
    @After public void cleanup() throws Exception {
        if(server==null)return;
        waitSync(); SessionManager.clear();server.close();thread.join(5000);
        try(RouteManager db=new RouteManager(context)) {
            db.getWritableDatabase().execSQL("DELETE FROM lectura WHERE ruta IN (SELECT id FROM ruta WHERE usuario IN (?,?))",new Object[]{worker.id,worker.id+1});
            db.getWritableDatabase().execSQL("DELETE FROM ruta WHERE usuario IN (?,?)",new Object[]{worker.id,worker.id+1});
        }
    }
    private void waitSync() throws Exception {
        long end=System.currentTimeMillis()+15000;
        while(MeasurementSync.enCurso() && System.currentTimeMillis()<end) Thread.sleep(30);
        assertFalse("La cola debe terminar",MeasurementSync.enCurso());
    }
    @Test public void retryDrainsMoreThanOneBatchAndPreservesOtherAccounts() throws Exception {
        try(RouteManager db=new RouteManager(context)) {
            long route=db.iniciar(worker.id,worker.parkId,"S-PRUEBA");
            long other=db.iniciar(worker.id+1,worker.parkId,"S-OTRO");
            for(int i=0;i<61;i++) db.lectura(route,new MedicionAmbiental("S-PRUEBA",42,39,-.1,0,"2026-11-19T10:00:00.000Z",i));
            db.lectura(other,new MedicionAmbiental("S-OTRO",99,39,-.1,0,"2026-11-19T10:00:00.000Z",0));
            db.getWritableDatabase().execSQL("UPDATE lectura SET enviado=2 WHERE ruta=?",new Object[]{route});
            assertTrue(MeasurementSync.reintentar(context));waitSync();
            assertEquals(61,posts.get());
            try(Cursor c=db.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM lectura WHERE ruta=? AND enviado=1",new String[]{""+route})) { assertTrue(c.moveToFirst());assertEquals(61,c.getInt(0)); }
            try(Cursor c=db.getReadableDatabase().rawQuery("SELECT enviado FROM lectura WHERE ruta=?",new String[]{""+other})) { assertTrue(c.moveToFirst());assertEquals(0,c.getInt(0)); }
            assertTrue(MeasurementSync.estado(SessionManager.current()).startsWith("61 enviadas"));
        }
    }
    @Test public void networkFailurePreservesQueueAndRetryConfirms() throws Exception {
        try(RouteManager db=new RouteManager(context)) {
            long route=db.iniciar(worker.id,worker.parkId,"S-PRUEBA");
            db.lectura(route,new MedicionAmbiental("S-PRUEBA",42,39,-.1,0,"2026-11-19T10:00:00.000Z",0));
            postStatus=503;assertTrue(MeasurementSync.reintentar(context));waitSync();
            assertTrue(db.estadoEnvios(worker).startsWith("1 pendientes"));
            postStatus=200;assertTrue(MeasurementSync.reintentar(context));waitSync();
            assertTrue(db.estadoEnvios(worker).startsWith("0 pendientes"));
        }
    }
    @Test public void securityAndPermissionsRenderWithBackNavigation() throws Exception {
        try(ActivityScenario<AccountActivity> screen=ActivityScenario.launch(new Intent(context,AccountActivity.class).putExtra("security",true))) {
            screen.onActivity(a -> {
                assertNotNull(a.findViewById(R.id.security_current));
                a.findViewById(R.id.security_submit).performClick();
                assertNotNull(((TextView)a.findViewById(R.id.security_current)).getError());
            }); capture("security.png");
            screen.recreate();screen.onActivity(a -> assertNotNull(a.findViewById(R.id.security_back)));
        }
        for(boolean notifications:new boolean[]{false,true}) try(ActivityScenario<PermissionsActivity> screen=ActivityScenario.launch(new Intent(context,PermissionsActivity.class).putExtra("notifications",notifications))) {
            screen.onActivity(a -> assertEquals(notifications?"Notificaciones":"Ubicación",((TextView)a.findViewById(R.id.permissions_title)).getText().toString()));
            capture(notifications?"notifications.png":"location.png");
            screen.onActivity(a -> a.findViewById(R.id.permissions_back).performClick());
        }
    }
    @Test public void historyReadsServerAndEmptyState() throws Exception {
        try(ActivityScenario<MainActivity> screen=ActivityScenario.launch(new Intent(context,MainActivity.class).putExtra(MainActivity.EXTRA_SECTION,R.id.tab_historial))) {
            awaitHistory(screen);
            capture("history-empty.png");
            screen.onActivity(a -> a.findViewById(R.id.history_account).performClick());
            awaitHistory(screen);
            screen.onActivity(a -> assertEquals(1,((android.widget.LinearLayout)a.findViewById(R.id.list_history)).getChildCount()));
            capture("history-server.png");
            history="{\"mediciones\":[],\"siguiente\":null}";
            screen.onActivity(a -> a.findViewById(R.id.button_refresh_history).performClick());
            awaitHistory(screen);
            screen.onActivity(a -> assertEquals(View.VISIBLE,a.findViewById(R.id.text_list_empty).getVisibility()));
        }
    }
    private void awaitHistory(ActivityScenario<MainActivity> screen) throws Exception {
        long end=System.currentTimeMillis()+12000; java.util.concurrent.atomic.AtomicBoolean done=new java.util.concurrent.atomic.AtomicBoolean();
        do { screen.onActivity(a -> done.set(a.findViewById(R.id.history_loading).getVisibility()==View.GONE));if(done.get())return;Thread.sleep(50); }while(System.currentTimeMillis()<end);
        fail("Historial bloqueado en carga");
    }
    private void capture(String name) throws Exception {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Bitmap bitmap=InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        File folder=new File(context.getExternalFilesDir(null),"elia-screens");folder.mkdirs();
        try(FileOutputStream output=new FileOutputStream(new File(folder,name))) { bitmap.compress(Bitmap.CompressFormat.PNG,100,output); }
        bitmap.recycle();
    }
}
