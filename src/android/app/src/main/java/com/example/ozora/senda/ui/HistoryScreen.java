package com.example.ozora.senda.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.network.LogicaFake;
import com.example.ozora.senda.routes.RouteManager;
import com.google.android.material.button.MaterialButtonToggleGroup;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Dos fuentes explícitas, sin duplicar lecturas locales y remotas ni inventar recorridos. */
public final class HistoryScreen {
    private static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private final Activity activity;
    private final View root;
    private final SessionManager session;
    private final List<String> cursors = new ArrayList<>();
    private int page, generation;
    private boolean remote, closed, busy;
    private String next;
    public HistoryScreen(Activity activity, View root, SessionManager session) {
        this.activity=activity; this.root=root; this.session=session;
        cursors.add(null);
        ((MaterialButtonToggleGroup)root.findViewById(R.id.history_source)).addOnButtonCheckedListener((g,id,checked) -> {
            if (!checked) return;
            remote = id == R.id.history_account;
            reset();
        });
        root.findViewById(R.id.button_refresh_history).setOnClickListener(v -> reset());
        root.findViewById(R.id.button_previous).setOnClickListener(v -> { if (!busy && page>0) load(page-1); });
        root.findViewById(R.id.button_next).setOnClickListener(v -> {
            if (busy || next==null) return;
            if (remote) {
                while(cursors.size()>page+1) cursors.remove(cursors.size()-1);
                cursors.add(next);
            }
            load(page+1);
        });
        reset();
    }
    public void close() { closed=true; generation++; }
    private void reset() { cursors.clear(); cursors.add(null); load(0); }
    private TextView text(int id) { return root.findViewById(id); }
    private boolean valid(int request) {
        return !closed && request==generation && !activity.isDestroyed() && SessionManager.current()==session;
    }
    private void load(int targetPage) {
        int request=++generation;
        boolean fromServer=remote;
        String cursor=remote?cursors.get(targetPage):null;
        busy=true;
        root.findViewById(R.id.history_loading).setVisibility(View.VISIBLE);
        root.findViewById(R.id.button_previous).setEnabled(false);
        root.findViewById(R.id.button_next).setEnabled(false);
        root.findViewById(R.id.button_refresh_history).setEnabled(false);
        ((LinearLayout)root.findViewById(R.id.list_history)).removeAllViews();
        root.findViewById(R.id.text_list_empty).setVisibility(View.GONE);
        text(R.id.page_number).setText("");
        text(R.id.history_hint).setText(remote ? "Lecturas confirmadas de tu cuenta en el servidor, también desde otros móviles."
                : "Lecturas guardadas en este móvil, incluidas las pendientes de envío.");
        android.content.Context app=activity.getApplicationContext();
        IO.execute(() -> {
            List<Reading> rows=new ArrayList<>();
            String following=null;
            try {
                if (SessionManager.current()!=session) return;
                if (fromServer) {
                    JSONObject data=new LogicaFake(app).historialMediciones(session.token,cursor);
                    JSONArray items=data.getJSONArray("mediciones");
                    for(int i=0;i<items.length();i++) {
                        JSONObject item=items.getJSONObject(i);
                        rows.add(new Reading(item.getInt("valor"),item.getString("fecha_hora"),item.getString("nombre_micro"),
                                item.optBoolean("simulada")?"Simulada · En el servidor":"En el servidor",null));
                    }
                    following=data.isNull("siguiente")?null:data.getString("siguiente");
                } else {
                    try(RouteManager db=new RouteManager(app); Cursor c=db.historial(session.worker,null,21,targetPage*20)) {
                        while(rows.size()<20 && c.moveToNext()) rows.add(new Reading(c.getInt(0),c.getString(1),c.getString(2),
                                c.getInt(4)==1?"Enviada":c.getInt(4)==2?"Rechazada · Conservada en el móvil":"Pendiente de envío",c.getString(5)));
                        if(c.moveToNext()) following="local";
                    }
                }
                String finalNext=following;
                activity.runOnUiThread(() -> {
                    if(!valid(request)) return;
                    page=targetPage; next=finalNext; finishLoading();
                    LinearLayout list=root.findViewById(R.id.list_history);
                    for(Reading row:rows) card(list,row);
                    text(R.id.text_list_empty).setText(fromServer ? "Todavía no hay lecturas guardadas en tu cuenta. Consulta Este móvil para ver los envíos pendientes."
                            : "Todavía no hay lecturas en este móvil. Vincula tu llavero y comienza un recorrido.");
                    root.findViewById(R.id.text_list_empty).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
                    text(R.id.page_number).setText("Página "+(page+1));
                    root.findViewById(R.id.history_pages).setVisibility(page>0||next!=null?View.VISIBLE:View.GONE);
                });
            } catch(Exception error) {
                activity.runOnUiThread(() -> {
                    if(!valid(request)) return;
                    next=null; page=targetPage; finishLoading();
                    text(R.id.text_list_empty).setText(error instanceof ApiClient.Error && ((ApiClient.Error)error).status==401
                            ? "Tu sesión ha caducado. Vuelve a iniciar sesión para consultar tu cuenta."
                            : fromServer ? "No se pudo cargar el historial de tu cuenta. Pulsa Actualizar para reintentarlo o consulta las lecturas de Este móvil."
                            : "No se pudieron leer las mediciones del móvil. Pulsa Actualizar para reintentarlo.");
                    root.findViewById(R.id.text_list_empty).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.history_pages).setVisibility(page>0?View.VISIBLE:View.GONE);
                });
            }
        });
    }
    private void finishLoading() {
        busy=false;
        root.findViewById(R.id.history_loading).setVisibility(View.GONE);
        root.findViewById(R.id.button_refresh_history).setEnabled(true);
        root.findViewById(R.id.button_previous).setEnabled(page>0);
        root.findViewById(R.id.button_next).setEnabled(next!=null);
    }
    private int dp(int value) { return Math.round(value*activity.getResources().getDisplayMetrics().density); }
    private void card(LinearLayout list, Reading reading) {
        LinearLayout card=new LinearLayout(activity); card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18),dp(16),dp(18),dp(16));
        GradientDrawable background=new GradientDrawable(); background.setColor(0xffffffff); background.setCornerRadius(dp(20));
        card.setBackground(background); card.setElevation(dp(2));
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-1,-2); params.setMargins(dp(2),dp(10),dp(2),dp(6)); list.addView(card,params);
        String when=reading.date;
        try { when=DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(new Date(SessionManager.instante(when))); }
        catch(java.text.ParseException ignored) { }
        label(card,when,14,false);
        label(card,reading.ppb+" ppb · O₃",27,true);
        label(card,"Llavero "+(reading.sensor==null?"sin identificar":reading.sensor),15,false);
        label(card,reading.status,14,true);
        if(reading.error!=null) label(card,reading.error,14,false);
    }
    private void label(LinearLayout card,String value,int size,boolean bold) {
        TextView text=new TextView(activity); text.setText(value); text.setTextColor(0xff1d4032); text.setTextSize(size);
        text.setPadding(0,dp(4),0,dp(4)); if(bold) text.setTypeface(Typeface.DEFAULT,Typeface.BOLD); card.addView(text);
    }
    private static final class Reading {
        final int ppb; final String date,sensor,status,error;
        Reading(int ppb,String date,String sensor,String status,String error) {
            this.ppb=ppb;this.date=date;this.sensor=sensor;this.status=status;this.error=error;
        }
    }
}
