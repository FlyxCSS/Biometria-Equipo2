package com.example.ozora.senda.auth;

/** Solo se compila en Debug. Sin contraseña, token ni autorización remota. */
public final class DemoAccess {
    private DemoAccess() {}
    public static boolean available() { return true; }
    public static void open() {
        SessionManager.openDemo(new SessionManager(
                new com.example.ozora.senda.model.Worker(-1001, -2001, "Trabajador DEMO (ficticio)"),
                System.currentTimeMillis() + 8 * 60 * 60 * 1000L, null, true));
    }
}