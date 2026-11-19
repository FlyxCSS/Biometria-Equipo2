/*
 * Nombre: RoutePreviewView.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.example.ozora.senda.R;
import com.example.ozora.senda.model.RoutePoint;
import java.util.ArrayList;
import java.util.List;

/** Miniatura del trazado GPS real. Los cambios de segmento conservan las interrupciones. */
public final class RoutePreviewView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<double[]> points=new ArrayList<>();
    private final Paint bitmapPaint=new Paint(Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG);
    private static Bitmap texture;
    private int seed;

    /** --------------------
     * Diseño lógico: context: Entorno, attrs: AttributeSet --> RoutePreviewView() -->
     * RoutePreviewView <--
     * Descripción: Construye RoutePreviewView. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public RoutePreviewView(Context context,AttributeSet attrs) { super(context,attrs); }

    /** --------------------
     * Diseño lógico: value: Z --> setSeed() -->
     * Descripción: Set Seed. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public void setSeed(long value) { seed=(int)Math.abs(value%97); invalidate(); }

    /** --------------------
     * Diseño lógico: route: [Dato] --> setPoints() -->
     * Descripción: Set Points. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public void setPoints(List<RoutePoint> route) {
        points.clear();
        double previous=0;
        for(RoutePoint p:route) {
            if(!Double.isFinite(p.latitude)||!Double.isFinite(p.longitude)) continue;
            double x=(p.longitude+180)/360;
            if(!points.isEmpty()) x+=Math.rint(previous-x);
            previous=x;
            double lat=Math.toRadians(Math.max(-85,Math.min(85,p.latitude)));
            double y=(1-Math.log(Math.tan(lat)+1/Math.cos(lat))/Math.PI)/2;
            points.add(new double[]{x,y,p.segment});
        }
        invalidate();
    }

    /** --------------------
     * Diseño lógico: canvas: Canvas --> onDraw() -->
     * Descripción: On Draw. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale=getResources().getDisplayMetrics().density;
        RectF bounds=new RectF(0,0,getWidth(),getHeight());
        Path clip=new Path(); clip.addRoundRect(bounds,20*scale,20*scale,Path.Direction.CW);
        canvas.save(); canvas.clipPath(clip);
        canvas.drawColor(0xffb7cda6);
        if(texture==null) texture=BitmapFactory.decodeResource(getResources(),R.drawable.senda_map_texture);
        if(texture!=null) {
            float s=Math.max(getWidth()/(float)texture.getWidth(),getHeight()/(float)texture.getHeight())*1.6f;
            float w=texture.getWidth()*s,h=texture.getHeight()*s;
            // Recorte distinto por ruta para que las miniaturas no sean idénticas.
            float ox=(float)(((seed*37)%100)/100.0*(w-getWidth())), oy=(float)(((seed*61)%100)/100.0*(h-getHeight()));
            canvas.drawBitmap(texture,null,new RectF(-ox,-oy,w-ox,h-oy),bitmapPaint);
        }
        paint.setStyle(Paint.Style.FILL);
        if(points.isEmpty()) {
            paint.setColor(0xccffffff); paint.setTextSize(12*scale); paint.setTextAlign(Paint.Align.CENTER);
            paint.setFakeBoldText(true);
            canvas.drawText(getContext().getString(R.string.routes_no_gps),getWidth()/2f,getHeight()/2f+4*scale,paint);
            canvas.restore(); return;
        }
        double xmin=Double.MAX_VALUE,xmax=-Double.MAX_VALUE,ymin=Double.MAX_VALUE,ymax=-Double.MAX_VALUE;
        for(double[] p:points) { xmin=Math.min(xmin,p[0]); xmax=Math.max(xmax,p[0]); ymin=Math.min(ymin,p[1]); ymax=Math.max(ymax,p[1]); }
        double span=Math.max(Math.max(xmax-xmin,ymax-ymin),.000001);
        float padding=12*scale;
        double fit=Math.max(1,Math.min(getWidth()-2*padding,getHeight()-2*padding))/span;
        Path path=new Path();
        double segment=Double.NaN;
        float firstX=0,firstY=0,lastX=0,lastY=0;
        for(int index=0;index<points.size();index++) {
            double[] p=points.get(index);
            float x=(float)((p[0]-(xmin+xmax)/2)*fit+getWidth()/2);
            float y=(float)((p[1]-(ymin+ymax)/2)*fit+getHeight()/2);
            if(index==0) { firstX=x; firstY=y; }
            if(p[2]!=segment) path.moveTo(x,y); else path.lineTo(x,y);
            segment=p[2]; lastX=x; lastY=y;
        }
        paint.setStyle(Paint.Style.STROKE); paint.setStrokeJoin(Paint.Join.ROUND); paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(7*scale); paint.setColor(0x33000000); canvas.drawPath(path,paint);
        paint.setStrokeWidth(4.5f*scale); paint.setColor(0xffffffff); canvas.drawPath(path,paint);
        paint.setStyle(Paint.Style.FILL); paint.setColor(0xffffffff);
        canvas.drawCircle(lastX,lastY,7*scale,paint);
        paint.setColor(0xff1f3d2f);
        canvas.drawCircle(lastX,lastY,4.5f*scale,paint);
        canvas.restore();
    }
}
