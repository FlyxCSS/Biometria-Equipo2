package com.example.ozora.senda.auth;

import com.example.ozora.senda.model.Worker;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class SessionManagerTest {
    @After public void limpiar() { SessionManager.clear(); }
    @Test public void requiereCredencialYTrabajadorReal() {
        for (String token : new String[]{null, "", "  "}) {
            try {
                SessionManager.open(new Worker(1, 1, "Trabajador"), Long.MAX_VALUE, token);
                fail("Debe rechazar una sesión sin credencial");
            } catch (IllegalArgumentException expected) { assertNull(SessionManager.current()); }
        }
        try {
            SessionManager.open(new Worker(-1001, -2001, "Ficticio"), Long.MAX_VALUE, "token");
            fail("Debe rechazar identidades ficticias del acceso anterior");
        } catch (IllegalArgumentException expected) { assertNull(SessionManager.current()); }
    }
    @Test public void caducidadYCierreImpidenReutilizarLaSesion() {
        SessionManager.open(new Worker(1, 1, "Trabajador"), System.currentTimeMillis()-1, "token");
        assertNull(SessionManager.current());
        SessionManager.open(new Worker(1, 1, "Trabajador"), Long.MAX_VALUE, "token");
        assertNotNull(SessionManager.current());
        SessionManager.clear();
        assertNull(SessionManager.current());
    }
}
