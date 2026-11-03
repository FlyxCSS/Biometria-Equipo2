package com.example.ozora.senda.auth;
import com.example.ozora.senda.model.Worker;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
public class DemoAccessTest {
    @After public void clear() { SessionManager.clear(); }
    @Test public void releaseNoPermiteDemo() {
        assertFalse(DemoAccess.available());
        try { DemoAccess.open(); fail("Release debe bloquear demo"); } catch(SecurityException esperado) {}
        assertNull(SessionManager.current());
    }
    @Test public void tampocoPermiteInyectarSesionDemo() {
        try {
            SessionManager.openDemo(new SessionManager(new Worker(-1,-1,"test"),Long.MAX_VALUE,null,true));
            fail("No debe aceptar sesiones demo en Release");
        } catch(SecurityException esperado) {}
        assertNull(SessionManager.current());
    }
}