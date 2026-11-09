package com.example.ozora.senda.ui;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public abstract class FieldScreen extends AppCompatActivity {
    private static final int BOSQUE=0xff234d39, TEXTO=0xff20372b;
    protected LinearLayout contenido;
    private TextView error;
    protected int dp(int n) { return Math.round(n*getResources().getDisplayMetrics().density); }
    /** Una sola aplicación de insets por ventana, también para recortes laterales e IME. */
    protected void protegerBarras(View root) {
        int superficie = getColor(com.example.ozora.senda.R.color.senda_surface);
        EdgeToEdge.enable(this, SystemBarStyle.light(superficie, 0xff20372b),
                SystemBarStyle.light(superficie, 0xff20372b));
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets safe = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.ime());
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }

    protected void pagina(String titulo) {
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(getColor(com.example.ozora.senda.R.color.senda_surface));
        contenido=new LinearLayout(this); contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.setPadding(dp(22),dp(24),dp(22),dp(24)); scroll.addView(contenido); setContentView(scroll); protegerBarras(scroll);
        texto("S E N D A    /    OZORA",13,true); texto(titulo,29,true);
    }
    protected TextView texto(String valor,int tam,boolean negrita) {
        TextView t=new TextView(this); t.setText(valor); t.setTextSize(tam); t.setTextColor(TEXTO);
        t.setPadding(0,dp(6),0,dp(6)); if(negrita)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        contenido.addView(t); return t;
    }
    protected TextView tarjeta(String titulo,int tam) {
        texto(titulo,14,true); TextView t=texto("",tam,false);
        GradientDrawable fondo=new GradientDrawable(); fondo.setColor(0xffe4ecde); fondo.setCornerRadius(dp(12));
        t.setBackground(fondo); t.setPadding(dp(16),dp(12),dp(16),dp(12)); return t;
    }
    protected Button boton(String label,Runnable click) {
        Button b=new Button(this); b.setText(label); b.setAllCaps(false); b.setTextColor(-1);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(BOSQUE));
        b.setOnClickListener(v->click.run()); contenido.addView(b); return b;
    }
    protected EditText campo(String hint,int tipo) {
        EditText e=new EditText(this); e.setSingleLine(true); e.setInputType(tipo); e.setHint(hint);
        e.setTextColor(TEXTO); e.setHintTextColor(0xff536855); e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(BOSQUE));
        return e;
    }
    protected void mensajes() {
        error=texto("",15,false); error.setTextColor(0xff953e22);
        error.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
    }
    protected void mensaje(String s) { if(!isDestroyed() && error!=null) error.setText(s); }
}
