package com.example.ozora.senda.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Gráfica de línea de las mediciones de una ruta (ozono o temperatura) a lo largo del tiempo.
 * Sustituye a la lista larga de puntos: muestra la evolución completa de un vistazo, con la banda
 * del umbral de ozono, y las etiquetas de hora de inicio y fin.
 */
public final class LineChartView extends View {
    private final Paint linea = new Paint(Paint.ANTI_ALIAS_FLAG), area = new Paint(Paint.ANTI_ALIAS_FLAG),
            rejilla = new Paint(Paint.ANTI_ALIAS_FLAG), texto = new Paint(Paint.ANTI_ALIAS_FLAG),
            punto = new Paint(Paint.ANTI_ALIAS_FLAG), umbralPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Float> valores = new ArrayList<>();
    private String inicio = "", fin = "", unidad = "ppb", vacio = "Sin mediciones";
    private float umbral = Float.NaN;
    private boolean decimales;

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float d = getResources().getDisplayMetrics().density;
        linea.setStyle(Paint.Style.STROKE); linea.setStrokeWidth(2.5f * d); linea.setStrokeJoin(Paint.Join.ROUND);
        linea.setStrokeCap(Paint.Cap.ROUND); linea.setColor(0xff1f3d2f);
        rejilla.setStyle(Paint.Style.STROKE); rejilla.setStrokeWidth(d); rejilla.setColor(0xffe6e2d9);
        rejilla.setPathEffect(new DashPathEffect(new float[]{4 * d, 4 * d}, 0));
        umbralPaint.setStyle(Paint.Style.STROKE); umbralPaint.setStrokeWidth(1.5f * d); umbralPaint.setColor(0xffd2493a);
        umbralPaint.setPathEffect(new DashPathEffect(new float[]{6 * d, 5 * d}, 0));
        texto.setColor(0xff5e6861); texto.setTextSize(12 * getResources().getDisplayMetrics().scaledDensity);
        texto.setTypeface(FieldScreen.inter(context, false));
        punto.setColor(0xff1f3d2f);
    }

    /**
     * @param datos valores en orden temporal; @param umbralOzono línea de aviso (NaN para no dibujarla);
     * @param conDecimales temperatura con un decimal.
     */
    public void setDatos(List<Float> datos, String horaInicio, String horaFin, String unidadDatos,
                         float umbralOzono, boolean conDecimales, String mensajeVacio) {
        valores.clear(); valores.addAll(datos);
        inicio = horaInicio; fin = horaFin; unidad = unidadDatos; umbral = umbralOzono;
        decimales = conDecimales; vacio = mensajeVacio;
        setContentDescription(datos.isEmpty() ? mensajeVacio : "Gráfica de " + datos.size() + " mediciones entre "
                + horaInicio + " y " + horaFin);
        invalidate();
    }

    private String formato(float v) {
        return decimales ? String.format(Locale.getDefault(), "%.1f", v) : String.valueOf(Math.round(v));
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float d = getResources().getDisplayMetrics().density;
        float izquierda = 4 * d, derecha = getWidth() - 40 * d, arriba = 10 * d, abajo = getHeight() - 24 * d;
        if (valores.isEmpty()) {
            texto.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(vacio, getWidth() / 2f, getHeight() / 2f, texto);
            return;
        }
        float min = Float.MAX_VALUE, max = -Float.MAX_VALUE;
        for (float v : valores) { min = Math.min(min, v); max = Math.max(max, v); }
        if (!Float.isNaN(umbral)) { min = Math.min(min, umbral); max = Math.max(max, umbral); }
        float margen = Math.max(1f, (max - min) * 0.15f);
        min -= margen; max += margen;
        if (!decimales) min = Math.max(0, min);
        float rango = max - min;

        // Rejilla horizontal y valores del eje a la derecha.
        texto.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i <= 3; i++) {
            float y = abajo - (abajo - arriba) * i / 3f;
            canvas.drawLine(izquierda, y, derecha, y, rejilla);
            canvas.drawText(formato(min + rango * i / 3f), derecha + 6 * d, y + 4 * d, texto);
        }
        if (!Float.isNaN(umbral)) {
            float y = abajo - (abajo - arriba) * (umbral - min) / rango;
            canvas.drawLine(izquierda, y, derecha, y, umbralPaint);
        }

        Path trazo = new Path(), relleno = new Path();
        int n = valores.size();
        for (int i = 0; i < n; i++) {
            float x = n == 1 ? (izquierda + derecha) / 2f : izquierda + (derecha - izquierda) * i / (n - 1f);
            float y = abajo - (abajo - arriba) * (valores.get(i) - min) / rango;
            if (i == 0) { trazo.moveTo(x, y); relleno.moveTo(x, abajo); }
            trazo.lineTo(x, y); relleno.lineTo(x, y);
            if (i == n - 1) relleno.lineTo(x, abajo);
        }
        relleno.close();
        area.setShader(new LinearGradient(0, arriba, 0, abajo, 0x551f3d2f, 0x001f3d2f, Shader.TileMode.CLAMP));
        canvas.drawPath(relleno, area);
        canvas.drawPath(trazo, linea);
        // Último valor resaltado.
        float ultimoX = n == 1 ? (izquierda + derecha) / 2f : derecha;
        float ultimoY = abajo - (abajo - arriba) * (valores.get(n - 1) - min) / rango;
        punto.setColor(0xffffffff); canvas.drawCircle(ultimoX, ultimoY, 5 * d, punto);
        punto.setColor(0xff1f3d2f); canvas.drawCircle(ultimoX, ultimoY, 3.5f * d, punto);

        texto.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(inicio, izquierda, getHeight() - 4 * d, texto);
        texto.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(fin, derecha, getHeight() - 4 * d, texto);
    }
}
