package com.example.ozora.senda.auth;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/** Extrae el token del contrato vigente; no abre enlaces ni reduce su longitud. */
public final class RecoveryToken {
    private RecoveryToken() {}
    public static String extract(String input) {
        if (input == null) return null;
        String value = input.trim();
        if (value.matches("[a-f0-9]{64}")) return value;
        try {
            URI uri = new URI(value);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) return null;
            String fragment = uri.getRawFragment();
            if (fragment == null) return null;
            String token = null;
            for (String part : fragment.split("&")) {
                String[] pair = part.split("=", 2);
                if (pair.length == 2 && "token".equals(pair[0])) {
                    if (token != null) return null;
                    token = URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
                }
            }
            return token != null && token.matches("[a-f0-9]{64}") ? token : null;
        } catch (Exception ignored) { return null; }
    }
}
