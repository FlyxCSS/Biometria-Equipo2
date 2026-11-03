package com.example.ozora.senda.auth;

import com.example.ozora.senda.model.Worker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class SessionManager {
    private static SessionManager current;
    public final Worker worker;
    public final long expiresAt;
    public final String token;

    public final boolean demo;
    SessionManager(Worker worker, long expiresAt, String token, boolean demo) {
        this.demo = demo;
        this.worker = worker;
        this.expiresAt = expiresAt;
        this.token = token;
    }
    public static void open(Worker worker, long expiresAt, String token) {
        current = new SessionManager(worker, expiresAt, token, false);
    }
    static void openDemo(SessionManager session) {
        if (!DemoAccess.available() || !session.demo || session.token != null)
            throw new SecurityException("Demostración no disponible");
        current = session;
    }
    public static SessionManager current() {
        return current != null && System.currentTimeMillis() < current.expiresAt ? current : null;
    }
    public static boolean isDemo() { return current != null && current.demo; }
    public static void clear() { current = null; }
    public static long instante(String iso) throws ParseException {
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ROOT);
        f.setTimeZone(TimeZone.getTimeZone("UTC")); return f.parse(iso).getTime();
    }
}
