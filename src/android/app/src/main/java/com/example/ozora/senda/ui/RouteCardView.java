package com.example.ozora.senda.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.model.RoutePoint;
import com.example.ozora.senda.routes.RoutePeriod;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class RouteCardView extends MaterialCardView {
    private long routeId,start;
    public RouteCardView(Context context) {
        super(context);
        float density=getResources().getDisplayMetrics().density;
        setRadius(26*density); setCardElevation(density);
        setStrokeColor(0x00ffffff); setCardBackgroundColor(0xffffffff);
        setClickable(true); setFocusable(true);
        View.inflate(context,R.layout.item_route,this);
    }
    public void bind(long id,long start,Long end,double meters,int count,List<RoutePoint> points) {
        this.routeId=id; this.start=start;
        ((RoutePreviewView)findViewById(R.id.route_preview)).setSeed(id);
        android.text.SpannableStringBuilder name=new android.text.SpannableStringBuilder("Ruta "+id);
        name.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),0,name.length(),0);
        int from=name.length();
        name.append(" · ").append(new SimpleDateFormat("dd/MM/yy",Locale.getDefault()).format(new Date(start)));
        name.setSpan(new android.text.style.ForegroundColorSpan(0xff5e6861),from,name.length(),0);
        ((TextView)findViewById(R.id.route_name)).setText(name);
        long now=System.currentTimeMillis();
        long[] today=RoutePeriod.TODAY.bounds(now,TimeZone.getDefault());
        long[] yesterday=RoutePeriod.YESTERDAY.bounds(now,TimeZone.getDefault());
        String date=start>=today[0]&&start<today[1] ? getContext().getString(R.string.routes_today)
                : start>=yesterday[0]&&start<yesterday[1] ? getContext().getString(R.string.routes_yesterday)
                : new SimpleDateFormat("dd/MM/yy",Locale.getDefault()).format(new Date(start));
        ((TextView)findViewById(R.id.route_date)).setText(date+", "+
                new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date(start)));
        setPoints(points);
        update(end,meters,count,now);
    }
    public void setPoints(List<RoutePoint> points) { ((RoutePreviewView)findViewById(R.id.route_preview)).setPoints(points); }
    public void update(Route route,long now) { update(route.end,route.meters,route.count,now); }
    private void update(Long end,double meters,int count,long now) {
        boolean active=end==null;
        findViewById(R.id.route_status).setVisibility(active?VISIBLE:GONE);
        findViewById(R.id.route_chevron).setVisibility(active?GONE:VISIBLE);
        long seconds=Math.max(0,(end==null?now:end)-start)/1000;
        String duration=String.format(Locale.getDefault(),"%02d:%02d:%02d",seconds/3600,seconds/60%60,seconds%60);
        String distance=String.format(Locale.getDefault(),meters>=10000?"%.0f km":"%.1f km",meters/1000);
        ((TextView)findViewById(R.id.route_duration)).setText(duration);
        ((TextView)findViewById(R.id.route_distance)).setText(distance);
        ((TextView)findViewById(R.id.route_count)).setText(String.valueOf(count));
        setContentDescription("Ruta "+routeId+", "+(active?"en curso":"finalizada")+", "+duration+
                ", "+distance+", "+count+" mediciones. Abrir resumen.");
    }
}
