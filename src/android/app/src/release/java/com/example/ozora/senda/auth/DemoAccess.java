package com.example.ozora.senda.auth;

/** Release no incluye ningún mecanismo de creación de cuentas demo. */
public final class DemoAccess {
    private DemoAccess() {}
    public static boolean available() { return false; }
    public static void open() { throw new SecurityException("Demostración no disponible en Release"); }
}