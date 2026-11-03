package com.example.ozora.senda.statistics;

import com.example.ozora.senda.model.Route;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public final class StatisticsCalculator {
    public static String resumen(Route route, long now) {
        if (route == null) return "Todavía no has iniciado un recorrido.";
        long end = route.end == null ? now : route.end;
        long seconds = Math.max(0, end - route.start) / 1000;
        DateFormat date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String state = route.end == null ? "En curso" : "Finalizado";
        String ozone = route.count == 0 ? "Sin datos de ozono" : String.format(Locale.getDefault(),
                "Media %.1f · Mín. %d · Máx. %d ppb", route.mean, route.minimum, route.maximum);
        return state + " · " + date.format(new Date(route.start))
                + (route.end == null ? "" : "\nFin · " + date.format(new Date(end)))
                + String.format(Locale.getDefault(), "\n%d:%02d:%02d  ·  %.2f km\n%d mediciones locales\n%s",
                    seconds / 3600, seconds / 60 % 60, seconds % 60, route.meters / 1000, route.count, ozone);
    }
}
