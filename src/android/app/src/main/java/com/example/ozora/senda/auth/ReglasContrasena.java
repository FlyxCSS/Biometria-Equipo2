package com.example.ozora.senda.auth;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Mismas reglas de contraseña segura que el servidor (Apoyo.php::validarContrasena), para avisar
 * al momento: 12-72 caracteres, mayúscula, minúscula, número y símbolo, y que no sea fácil de adivinar.
 */
public final class ReglasContrasena {
    private ReglasContrasena() {}

    /** Texto de ayuda que se muestra junto a los campos de contraseña nueva. */
    public static final String AYUDA = "Mínimo 12 caracteres, con mayúscula, minúscula, número y símbolo (! ? # . -).";

    /** @return null si es segura, o el motivo para mostrárselo al usuario. */
    public static String problema(String valor, String correo) {
        int bytes = valor.getBytes(StandardCharsets.UTF_8).length;
        if (bytes < 12 || bytes > 72 || valor.indexOf(0) >= 0) return "La contraseña debe tener entre 12 y 72 caracteres.";
        List<String> faltan = new ArrayList<>();
        if (!valor.codePoints().anyMatch(Character::isUpperCase)) faltan.add("una mayúscula");
        if (!valor.codePoints().anyMatch(Character::isLowerCase)) faltan.add("una minúscula");
        if (!valor.codePoints().anyMatch(Character::isDigit)) faltan.add("un número");
        if (!valor.codePoints().anyMatch(c -> !Character.isLetterOrDigit(c))) faltan.add("un símbolo (! ? # . -)");
        if (!faltan.isEmpty()) return "La contraseña no es segura: le falta " + String.join(", ", faltan) + ".";
        String minusculas = valor.toLowerCase(Locale.ROOT);
        boolean personal = false;
        if (correo != null && correo.contains("@")) {
            for (String parte : correo.substring(0, correo.indexOf('@')).toLowerCase(Locale.ROOT).split("[._+\\-\\d]+"))
                if (parte.length() >= 4 && minusculas.contains(parte)) personal = true;
        }
        if (personal || minusculas.matches(".*(.)\\1{3,}.*")
                || minusculas.matches(".*(1234|2345|3456|4567|5678|6789|abcd|qwer|asdf|password|contrase|senda|ozora).*"))
            return "La contraseña es fácil de adivinar: evita tu correo, el nombre de la app, secuencias (1234, abcd) o caracteres repetidos.";
        return null;
    }
}
