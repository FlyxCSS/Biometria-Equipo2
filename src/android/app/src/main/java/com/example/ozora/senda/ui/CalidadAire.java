package com.example.ozora.senda.ui;

import android.content.Context;
import com.example.ozora.senda.auth.SessionManager;

/**
 * Calidad del aire orientativa según el umbral de ozono configurado (60 ppb por defecto).
 * Mismos cortes que la tarjeta de Inicio: buena < 75 % del umbral, mala >= 125 %.
 */
public enum CalidadAire {
    BUENA("Buena calidad", "Aire bueno", 0xff3e9b5a, 0xffe4ede2, 0xff6ba57a),
    MODERADA("Calidad moderada", "Aire moderado", 0xffb9781a, 0xfffbf0da, 0xffe2b54a),
    MALA("Mala calidad", "Aire malo", 0xffb3261e, 0xfffbe7e4, 0xffd2493a);

    public final String etiqueta, corta;
    public final int texto, fondo, punto;

    CalidadAire(String etiqueta, String corta, int texto, int fondo, int punto) {
        this.etiqueta = etiqueta; this.corta = corta; this.texto = texto; this.fondo = fondo; this.punto = punto;
    }

    public static CalidadAire de(double ppb, int umbral) {
        if (ppb >= umbral * 1.25) return MALA;
        if (ppb >= umbral * 0.75) return MODERADA;
        return BUENA;
    }

    /** Umbral guardado en los ajustes de campo del trabajador. */
    public static int umbral(Context context, SessionManager session) {
        return context.getSharedPreferences("campo-" + session.worker.id + "-" + session.worker.parkId, Context.MODE_PRIVATE)
                .getInt("umbral", 60);
    }

    /** Consejo breve para el aviso durante el recorrido. */
    public String consejo() {
        switch (this) {
            case MALA: return "Ozono alto en esta zona. Limita el esfuerzo intenso y avisa si notas molestias.";
            case MODERADA: return "Ozono moderado. Las personas sensibles deberían reducir el esfuerzo prolongado.";
            default: return "El aire en esta zona es bueno.";
        }
    }
}
