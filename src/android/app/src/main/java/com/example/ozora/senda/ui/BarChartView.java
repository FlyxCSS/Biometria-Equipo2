/*
 * Nombre: BarChartView.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/** Barras redondeadas con rejilla discontinua y eje a la derecha, como en el diseño del historial. */
public final class BarChartView extends View {
    private final Paint bar = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint grid = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] values = new float[0];
    private String[] labels = new String[0];
    private boolean highlightLast;
    private String empty = "";


    /** --------------------
     * Diseño lógico: context: Entorno, attrs: AttributeSet --> BarChartView() -->
     * BarChartView <--
     * Descripción: Construye BarChartView. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float d = getResources().getDisplayMetrics().density;
        grid.setColor(0xffdcd8cf); grid.setStrokeWidth(d); grid.setStyle(Paint.Style.STROKE);
        grid.setPathEffect(new DashPathEffect(new float[]{4 * d, 4 * d}, 0));
        text.setColor(0xff5e6861); text.setTypeface(FieldScreen.inter(context,false)); text.setTextSize(13 * getResources().getDisplayMetrics().scaledDensity);
    }

    /** NaN indica un intervalo sin lecturas. */

    /** --------------------
     * Diseño lógico: values: [Dato], labels: [Dato], highlightLast: B, emptyMessage: Text --> setData() -->
     * Descripción: Set Data. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public void setData(float[] values, String[] labels, boolean highlightLast, String emptyMessage) {
        this.values = values; this.labels = labels; this.highlightLast = highlightLast;
        this.empty = emptyMessage == null ? "" : emptyMessage;
        StringBuilder description = new StringBuilder("Gráfico de barras. ");
        for (int i = 0; i < values.length; i++) if (!Float.isNaN(values[i]))
            description.append(labels[i] == null ? "" : labels[i]).append(": ").append(Math.round(values[i])).append(" ppb. ");
        setContentDescription(description.toString());
        invalidate();
    }


    /** --------------------
     * Diseño lógico: canvas: Canvas --> onDraw() -->
     * Descripción: On Draw. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float d = getResources().getDisplayMetrics().density;
        float axis = 34 * d, bottom = getHeight() - 28 * d, top = 10 * d;
        float width = getWidth() - axis - 6 * d;
        float max = 100;
        boolean any = false;
        for (float v : values) if (!Float.isNaN(v)) { any = true; max = Math.max(max, v); }
        max = (float) Math.ceil(max / 25f) * 25f;
        text.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i <= 4; i++) {
            float y = bottom - (bottom - top) * i / 4f;
            canvas.drawLine(0, y, width, y, grid);
            canvas.drawText(String.valueOf(Math.round(max * i / 4f)), getWidth(), y + 5 * d, text);
        }
        if (!any) {
            text.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(empty, width / 2f, (top + bottom) / 2f, text);
            return;
        }
        int n = Math.max(1, values.length);
        float slot = width / n, barWidth = Math.min(28 * d, slot * 0.46f);
        text.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < values.length; i++) {
            float cx = slot * i + slot / 2f;
            if (!Float.isNaN(values[i])) {
                float h = Math.max(barWidth, (bottom - top) * values[i] / max);
                bar.setColor(highlightLast && i == values.length - 1 ? 0xffa9c4a6 : 0xff1f3d2f);
                canvas.drawRoundRect(new RectF(cx - barWidth / 2, bottom - h, cx + barWidth / 2, bottom), barWidth / 2, barWidth / 2, bar);
            }
            if (labels[i] != null) canvas.drawText(labels[i], cx, getHeight() - 4 * d, text);
        }
    }
}
