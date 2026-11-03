package com.example.ozora.senda.routes;

import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.model.RoutePoint;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.ui.FieldScreen;
import android.os.Bundle;
import java.util.ArrayList;

import android.webkit.WebView;
import java.util.List;

/** Proyección Web Mercator compartida por las teselas y el trazado GPS. */
public final class RouteActivity extends FieldScreen {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        SessionManager session=SessionManager.current();
        if(session==null) { finish(); return; }
        pagina("Mapa del recorrido");
        List<double[]> puntos=new ArrayList<>();
        try(RouteManager manager=new RouteManager(this)) {
            Route route=manager.ultimaRuta(session.worker);
            if(route!=null) for(RoutePoint point:manager.puntos(route.id)) {
                puntos.add(new double[]{point.latitude,point.longitude,point.segment});
            }
        }

        if(puntos.isEmpty()) {
            texto("El mapa estará disponible al registrar el primer punto GPS.",17,false);
            boton("Volver",this::finish);
            return;
        }
        double xmin=1,xmax=0,ymin=1,ymax=0;
        for(double[] p:puntos) {
            double lat=Math.toRadians(Math.max(-85.05,Math.min(85.05,p[0])));
            double x=(p[1]+180)/360, y=(1-Math.log(Math.tan(lat)+1/Math.cos(lat))/Math.PI)/2;
            xmin=Math.min(xmin,x); xmax=Math.max(xmax,x); ymin=Math.min(ymin,y); ymax=Math.max(ymax,y);
            p[0]=x; p[1]=y;
        }
        int z=17;
        while(z>0 && Math.max(xmax-xmin,ymax-ymin)*(256L<<z)>440) z--;
        int count=1<<z;
        double world=count*256., left=(xmin+xmax)*world/2-256,top=(ymin+ymax)*world/2-256;
        StringBuilder svg=new StringBuilder("<meta name='viewport' content='width=device-width,initial-scale=1'><body style='margin:0;background:#f5f3e8;color:#244e3d;font:16px sans-serif'><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 512 512' width='100%'>");
        for(int x=(int)Math.floor(left/256);x*256.<left+512;x++) {
            for(int y=(int)Math.floor(top/256);y*256.<top+512;y++) {
                if(y<0 || y>=count) continue;
                svg.append("<image width='256' height='256' x='").append(x*256.-left).append("' y='").append(y*256.-top)
                    .append("' href='https://tile.openstreetmap.org/").append(z).append('/').append(Math.floorMod(x,count)).append('/').append(y).append(".png'/>");
            }
        }
        svg.append("<path fill='none' stroke='#126546' stroke-width='5' stroke-linejoin='round' d='");
        double segmento=-1;
        for(double[] p:puntos) {
            svg.append(p[2]==segmento ? " L" : " M").append(Math.round(p[0]*world-left)).append(' ').append(Math.round(p[1]*world-top));
            segmento=p[2];
        }
        svg.append("'/>");
        for(int idx:new int[]{0,puntos.size()-1}) {
            double[] p=puntos.get(idx);
            svg.append("<circle r='7' stroke='white' fill='").append(idx==0?"#27784c":"#aa6315").append("' cx='")
                .append(p[0]*world-left).append("' cy='").append(p[1]*world-top).append("'/>");
        }
        svg.append("</svg><p>Inicio en verde · Último punto en ocre<br>© OpenStreetMap contributors</p><p>El trazado está guardado en el móvil. El mapa base necesita conexión.</p>");
        WebView web=new WebView(this); web.getSettings().setAllowFileAccess(false);
        web.getSettings().setUserAgentString("SENDA-Equipo2/1.0 (Android; Biometria-Equipo2)");
        web.loadDataWithBaseURL("https://senda.invalid/",svg.toString(),"text/html","utf-8",null);
        contenido.addView(web);
        boton("Volver",this::finish);
    }
}
