package com.example.ozora.senda.ui;

import android.os.Bundle;
import android.content.Intent;
import com.example.ozora.senda.MainActivity;
import com.example.ozora.senda.routes.RouteActivity;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.statistics.StatisticsCalculator;

public final class SummaryActivity extends FieldScreen {
    @Override protected void onStart() {
        super.onStart();
        if (com.example.ozora.senda.auth.SessionManager.current() == null) finish();
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        SessionManager session=SessionManager.current();
        if(session==null) { finish(); return; }
        pagina("Resumen del recorrido"); if(session.demo) texto("DEMOSTRACIÓN DEBUG · Datos locales de prueba",16,true);
        try(RouteManager manager=new RouteManager(this)) {
            long id = getIntent().getLongExtra(MainActivity.EXTRA_ROUTE_ID, 0);
            Route route = id == 0 ? manager.ultimaRuta(session.worker) : manager.ruta(session.worker, id);
            tarjeta("Duración, distancia y ozono",18).setText(StatisticsCalculator.resumen(route,System.currentTimeMillis()));
            if (route != null) boton(getString(com.example.ozora.senda.R.string.main_map), () ->
                    startActivity(new Intent(this, RouteActivity.class).putExtra(MainActivity.EXTRA_ROUTE_ID, route.id)));
        }
        boton("Volver",this::finish);
    }
}
