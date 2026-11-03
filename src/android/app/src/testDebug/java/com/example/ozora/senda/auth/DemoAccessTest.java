package com.example.ozora.senda.auth;
import com.example.ozora.senda.model.Worker;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
public class DemoAccessTest {
    @After public void clear() { SessionManager.clear(); }
    @Test public void demoEsLocalTemporalYFicticia() {
        long antes=System.currentTimeMillis();
        assertTrue(DemoAccess.available()); DemoAccess.open();
        SessionManager s=SessionManager.current();
        assertNotNull(s); assertTrue(s.demo); assertNull(s.token);
        assertEquals(-1001,s.worker.id); assertEquals(-2001,s.worker.parkId);
        assertTrue(s.worker.name.contains("ficticio"));
        assertTrue(s.expiresAt>antes); assertTrue(s.expiresAt<=System.currentTimeMillis()+28800000L);
    }
    @Test public void accesoRealConservaSuContrato() {
        DemoAccess.open();
        SessionManager.open(new Worker(12,3,"Trabajador"),System.currentTimeMillis()+60000,"token-test");
        assertFalse(SessionManager.current().demo); assertFalse(SessionManager.isDemo());
        assertEquals(12,SessionManager.current().worker.id);
        SessionManager.clear(); assertNull(SessionManager.current()); assertFalse(SessionManager.isDemo());
    }
}