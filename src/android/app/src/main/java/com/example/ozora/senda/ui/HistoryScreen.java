/*
 * Nombre: HistoryScreen.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
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
    private int page, generation, chartGeneration, period;
    private boolean remote, closed, busy, temperature;
    private String next;

    /** --------------------
     * Diseño lógico: activity: Activity, root: Vista, session: SessionManager --> HistoryScreen() -->
     * HistoryScreen <--
     * Descripción: Construye HistoryScreen. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public HistoryScreen(Activity activity, View root, SessionManager session) {
        this.activity=activity; this.root=root; this.session=session;
        cursors.add(null);
        ((MaterialButtonToggleGroup)root.findViewById(R.id.history_source)).addOnButtonCheckedListener((g,id,checked) -> {
            if (!checked) return;
            remote = id == R.id.history_account;
            reset();
        });
        root.findViewById(R.id.button_refresh_history).setOnClickListener(v -> { reset(); chart(); });
        ((MaterialButtonToggleGroup)root.findViewById(R.id.history_magnitude)).addOnButtonCheckedListener((g,id,checked) -> {
            if (checked) { temperature = id == R.id.history_temperature; chart(); }
        });
        ((MaterialButtonToggleGroup)root.findViewById(R.id.history_period)).addOnButtonCheckedListener((g,id,checked) -> {
            if (!checked) return;
            period = id == R.id.history_week ? 1 : id == R.id.history_month ? 2 : id == R.id.history_year ? 3 : 0;
            chart();
        });
        chart();
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

    /** --------------------
     * Diseño lógico: close() -->
     * Descripción: Close. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public void close() { closed=true; generation++; chartGeneration++; }

    /** Agrupa las lecturas locales por hora, día, semana o mes; sin datos inventados. */

    /** --------------------
     * Diseño lógico: chart() -->
     * Descripción: Chart. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void chart() {
        int request=++chartGeneration;
        boolean temp=temperature; int range=period;
        TextView mean=text(R.id.history_mean);
        BarChartView view=root.findViewById(R.id.history_chart);
        if (temp) {
            mean.setText("Sin mediciones de temperatura");
            view.setData(new float[0],new String[0],false,"El llavero actual no mide temperatura");
            return;
        }
        android.content.Context app=activity.getApplicationContext();
        IO.execute(() -> {
            java.util.Calendar now=java.util.Calendar.getInstance();
            java.util.Calendar start=(java.util.Calendar)now.clone();
            start.set(java.util.Calendar.MINUTE,0); start.set(java.util.Calendar.SECOND,0); start.set(java.util.Calendar.MILLISECOND,0);
            int buckets;
            if (range==0) { start.set(java.util.Calendar.HOUR_OF_DAY,0); buckets=24; }
            else if (range==1) { start.set(java.util.Calendar.HOUR_OF_DAY,0); start.add(java.util.Calendar.DAY_OF_MONTH,-6); buckets=7; }
            else if (range==2) { start.set(java.util.Calendar.HOUR_OF_DAY,0); start.add(java.util.Calendar.DAY_OF_MONTH,-27); buckets=4; }
            else { start.set(java.util.Calendar.HOUR_OF_DAY,0); start.set(java.util.Calendar.DAY_OF_MONTH,1); start.add(java.util.Calendar.MONTH,-11); buckets=12; }
            double[] sum=new double[buckets]; int[] count=new int[buckets];
            double total=0; int all=0;
            java.text.SimpleDateFormat utc=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",java.util.Locale.ROOT);
            utc.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            try(RouteManager db=new RouteManager(app); Cursor c=db.lecturasDesde(session.worker,utc.format(start.getTime()))) {
                java.util.Calendar when=java.util.Calendar.getInstance();
                while(c.moveToNext()) {
                    long instant;
                    try { instant=SessionManager.instante(c.getString(1)); } catch(java.text.ParseException e) { continue; }
                    if (instant<start.getTimeInMillis()) continue;
                    when.setTimeInMillis(instant);
                    int index;
                    if (range==0) index=when.get(java.util.Calendar.HOUR_OF_DAY);
                    else if (range==3) index=(when.get(java.util.Calendar.YEAR)-start.get(java.util.Calendar.YEAR))*12
                            +when.get(java.util.Calendar.MONTH)-start.get(java.util.Calendar.MONTH);
                    else { long days=(instant-start.getTimeInMillis())/86_400_000L; index=(int)(range==1?days:days/7); }
                    if (index<0||index>=buckets) continue;
                    sum[index]+=c.getInt(0); count[index]++; total+=c.getInt(0); all++;
                }
            } catch(Exception error) { all=-1; }
            int first=0, last=buckets-1;
            if (range==0) { // Solo las horas de trabajo con lecturas hasta la hora actual.
                last=now.get(java.util.Calendar.HOUR_OF_DAY);
                first=last;
                for(int i=0;i<=last;i++) if(count[i]>0) { first=i; break; }
                first=Math.max(0,Math.min(first,last-8));
            }
            int size=last-first+1;
            float[] values=new float[size]; String[] labels=new String[size];
            String[] months={"E","F","M","A","M","J","J","A","S","O","N","D"};
            String[] days={"D","L","M","X","J","V","S"};
            for(int i=0;i<size;i++) {
                int b=first+i;
                values[i]=count[b]==0?Float.NaN:(float)(sum[b]/count[b]);
                java.util.Calendar label=(java.util.Calendar)start.clone();
                if (range==0) labels[i]=(size<=5||i%2==0)?String.format(java.util.Locale.ROOT,"%02d:00",b):null;
                else if (range==1) { label.add(java.util.Calendar.DAY_OF_MONTH,b); labels[i]=days[label.get(java.util.Calendar.DAY_OF_WEEK)-1]; }
                else if (range==2) { label.add(java.util.Calendar.DAY_OF_MONTH,b*7); labels[i]=new java.text.SimpleDateFormat("dd/MM",java.util.Locale.getDefault()).format(label.getTime()); }
                else { label.add(java.util.Calendar.MONTH,b); labels[i]=months[label.get(java.util.Calendar.MONTH)]; }
            }
            if (range==0) for(int i=0;i<size;i++) if(labels[i]!=null && size>5 && i%2!=0) labels[i]=null;
            String[] names={"Media de hoy","Media de la semana","Media del mes","Media del año"};
            int finalAll=all; double finalTotal=total;
            activity.runOnUiThread(() -> {
                if (closed || request!=chartGeneration || activity.isDestroyed()) return;
                if (finalAll<=0) {
                    mean.setText(finalAll<0?"No se pudieron leer las mediciones":names[range]+"  —");
                } else {
                    android.text.SpannableStringBuilder s=new android.text.SpannableStringBuilder(names[range]+"  ");
                    int a=s.length(); s.append(String.valueOf(Math.round(finalTotal/finalAll)));
                    s.setSpan(new android.text.style.AbsoluteSizeSpan(30,true),a,s.length(),0);
                    s.setSpan(new android.text.style.StyleSpan(Typeface.BOLD),a,s.length(),0);
                    s.setSpan(new android.text.style.ForegroundColorSpan(0xff1f3d2f),a,s.length(),0);
                    s.append("  ppb");
                    mean.setText(s);
                }
                view.setData(values,labels,true,"Sin lecturas en este periodo");
            });
        });
    }

    /** --------------------
     * Diseño lógico: reset() -->
     * Descripción: Reset. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void reset() { cursors.clear(); cursors.add(null); load(0); }

    /** --------------------
     * Diseño lógico: id: Z --> text() -->
     * Text <--
     * Descripción: Text. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private TextView text(int id) { return root.findViewById(id); }

    /** --------------------
     * Diseño lógico: request: Z --> valid() -->
     * B <--
     * Descripción: Valid. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private boolean valid(int request) {
        return !closed && request==generation && !activity.isDestroyed() && SessionManager.current()==session;
    }

    /** --------------------
     * Diseño lógico: targetPage: Z --> load() -->
     * Descripción: Load. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
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
        root.findViewById(R.id.list_history).setVisibility(View.GONE);
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
                    list.setVisibility(rows.isEmpty()?View.GONE:View.VISIBLE);
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
                    int status = error instanceof ApiClient.Error ? ((ApiClient.Error)error).status : 0;
                    android.util.Log.w("SendaHistorial", "Historial de cuenta: " + status + " " + error.getMessage());
                    text(R.id.text_list_empty).setText(status==401
                            ? "Tu sesión ha caducado. Vuelve a iniciar sesión para consultar tu cuenta."
                            : fromServer && status==404 ? "El servidor todavía no ofrece el historial de cuenta (ruta no disponible). Las lecturas siguen guardadas: consulta Este móvil."
                            : fromServer && status>0 ? "No se pudo cargar el historial de tu cuenta: " + error.getMessage()
                            : fromServer ? "Sin conexión con el servidor. Pulsa Actualizar para reintentarlo o consulta las lecturas de Este móvil."
                            : "No se pudieron leer las mediciones del móvil. Pulsa Actualizar para reintentarlo.");
                    root.findViewById(R.id.text_list_empty).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.history_pages).setVisibility(page>0?View.VISIBLE:View.GONE);
                });
            }
        });
    }

    /** --------------------
     * Diseño lógico: finishLoading() -->
     * Descripción: Finish Loading. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void finishLoading() {
        busy=false;
        root.findViewById(R.id.history_loading).setVisibility(View.GONE);
        root.findViewById(R.id.button_refresh_history).setEnabled(true);
        root.findViewById(R.id.button_previous).setEnabled(page>0);
        root.findViewById(R.id.button_next).setEnabled(next!=null);
    }

    /** --------------------
     * Diseño lógico: value: Z --> dp() -->
     * Z <--
     * Descripción: Dp. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private int dp(int value) { return Math.round(value*activity.getResources().getDisplayMetrics().density); }

    /** --------------------
     * Diseño lógico: list: LinearLayout, reading: Reading --> card() -->
     * Descripción: Card. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void card(LinearLayout list, Reading reading) {
        if (list.getChildCount()>0) {
            View divider=new View(activity); divider.setBackgroundColor(0xffece9e2);
            LinearLayout.LayoutParams line=new LinearLayout.LayoutParams(-1,dp(1)); line.setMargins(dp(16),0,dp(16),0);
            list.addView(divider,line);
        }
        LinearLayout row=new LinearLayout(activity); row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16),dp(12),dp(16),dp(12));
        android.widget.ImageView icon=new android.widget.ImageView(activity);
        icon.setImageResource(R.drawable.ic_s_pin); icon.setBackgroundResource(R.drawable.s_tile_large);
        icon.setPadding(dp(10),dp(10),dp(10),dp(10)); icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        row.addView(icon,new LinearLayout.LayoutParams(dp(46),dp(46)));
        LinearLayout body=new LinearLayout(activity); body.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams bodySize=new LinearLayout.LayoutParams(0,-2,1); bodySize.setMarginStart(dp(14));
        row.addView(body,bodySize);
        TextView value=label(body,reading.ppb+" ppb",18,true); value.setPadding(0,0,0,0);
        String detail=reading.status+" · "+(reading.sensor==null?"Llavero sin identificar":reading.sensor);
        TextView sub=label(body,detail,14,false); sub.setPadding(0,dp(2),0,0); sub.setTextColor(activity.getColor(
                reading.status.contains("Rechazada") ? R.color.status_error : R.color.senda_muted));
        sub.setSingleLine(true); sub.setEllipsize(android.text.TextUtils.TruncateAt.END);
        if(reading.error!=null) { TextView error=label(body,reading.error,13,false); error.setPadding(0,dp(2),0,0); error.setTextColor(activity.getColor(R.color.status_error)); }
        String when=reading.date;
        try {
            long instant=SessionManager.instante(when);
            java.util.Calendar a=java.util.Calendar.getInstance(), b=java.util.Calendar.getInstance(); b.setTimeInMillis(instant);
            boolean today=a.get(java.util.Calendar.YEAR)==b.get(java.util.Calendar.YEAR) && a.get(java.util.Calendar.DAY_OF_YEAR)==b.get(java.util.Calendar.DAY_OF_YEAR);
            when=new java.text.SimpleDateFormat(today?"HH:mm":"dd/MM HH:mm",java.util.Locale.getDefault()).format(new Date(instant));
        } catch(java.text.ParseException ignored) { }
        TextView time=new TextView(activity); time.setText(when); time.setTextSize(17); time.setTextColor(0xff5e6861);
        LinearLayout.LayoutParams timeSize=new LinearLayout.LayoutParams(-2,-2); timeSize.setMarginStart(dp(8));
        row.addView(time,timeSize);
        row.setContentDescription(reading.ppb+" ppb, "+when+", "+detail);
        list.addView(row);
    }

    /** --------------------
     * Diseño lógico: card: LinearLayout, value: Text, size: Z, bold: B --> label() -->
     * Text <--
     * Descripción: Label. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private TextView label(LinearLayout card,String value,int size,boolean bold) {
        TextView text=new TextView(activity); text.setText(value); text.setTextColor(0xff1d4032); text.setTextSize(size);
        text.setPadding(0,dp(4),0,dp(4)); if(bold) text.setTypeface(FieldScreen.inter(activity,true)); card.addView(text); return text;
    }
    private static final class Reading {
        final int ppb; final String date,sensor,status,error;

        /** --------------------
         * Diseño lógico: ppb: Z, date: Text, sensor: Text, status: Text, error: Text --> Reading() -->
         * Reading <--
         * Descripción: Construye Reading. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        Reading(int ppb,String date,String sensor,String status,String error) {
            this.ppb=ppb;this.date=date;this.sensor=sensor;this.status=status;this.error=error;
        }
    }
}
