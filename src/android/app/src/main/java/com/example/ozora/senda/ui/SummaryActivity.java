package com.example.ozora.senda.ui;

import android.os.Bundle;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.statistics.StatisticsCalculator;

public final class SummaryActivity extends FieldScreen {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        SessionManager session=SessionManager.current();
        if(session==null) { finish(); return; }
        pagina("Resumen del recorrido"); if(session.demo) texto("DEMOSTRACIÓN DEBUG · Datos locales de prueba",16,true);
        try(RouteManager manager=new RouteManager(this)) {
            Route route=manager.ultimaRuta(session.worker);
            tarjeta("Duración, distancia y ozono",18).setText(StatisticsCalculator.resumen(route,System.currentTimeMillis()));
        }
        boton("Volver",this::finish);
    }
}
