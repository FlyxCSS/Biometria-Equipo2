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
    public RoutePreviewView(Context context,AttributeSet attrs) { super(context,attrs); }
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
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale=getResources().getDisplayMetrics().density;
        RectF bounds=new RectF(0,0,getWidth(),getHeight());
        Path clip=new Path(); clip.addRoundRect(bounds,16*scale,16*scale,Path.Direction.CW);
        canvas.save(); canvas.clipPath(clip);
        canvas.drawColor(0xffd6e0ce);
        paint.setStyle(Paint.Style.FILL); paint.setColor(0xffb3cdae);
        canvas.drawOval(new RectF(-getWidth()*.4f,getHeight()*.35f,getWidth()*1.4f,getHeight()*1.4f),paint);
        if(points.isEmpty()) {
            paint.setColor(0xff466650); paint.setTextSize(12*scale); paint.setTextAlign(Paint.Align.CENTER);
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
        paint.setStrokeWidth(8*scale); paint.setColor(0xfff7f3e8); canvas.drawPath(path,paint);
        paint.setStrokeWidth(4*scale); paint.setColor(0xfffad99e); canvas.drawPath(path,paint);
        paint.setStyle(Paint.Style.FILL); paint.setColor(0xffffffff);
        canvas.drawCircle(firstX,firstY,4*scale,paint); canvas.drawCircle(lastX,lastY,4*scale,paint);
        paint.setColor(0xff1d4032);
        canvas.drawCircle(firstX,firstY,2.5f*scale,paint); canvas.drawCircle(lastX,lastY,2.5f*scale,paint);
        canvas.restore();
    }
}
