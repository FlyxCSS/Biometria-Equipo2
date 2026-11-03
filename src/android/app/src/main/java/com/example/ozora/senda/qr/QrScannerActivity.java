package com.example.ozora.senda.qr;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import com.example.ozora.senda.ui.FieldScreen;

/** Devuelve una selección local. El resultado no autoriza un llavero en el servidor. */
public final class QrScannerActivity extends FieldScreen {
    private EditText codigo;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pagina("Llavero de campo");
        texto("Escanea la etiqueta o introduce el código del llavero.",16,false);
        codigo=campo("Ej. S-001", InputType.TYPE_CLASS_TEXT);
        codigo.setText(getIntent().getStringExtra("sensor"));
        contenido.addView(codigo);
        boton("Escanear QR",()->{
            Intent scanner=new Intent("com.google.zxing.client.android.SCAN").putExtra("SCAN_MODE","QR_CODE_MODE");
            try { startActivityForResult(scanner,1); }
            catch(ActivityNotFoundException e) { mensaje("No hay un lector QR compatible instalado. Introduce el código de la etiqueta."); }
        });
        boton("Seleccionar",()->seleccionar(codigo.getText().toString()));
        boton("Solo GPS",()->devolver(null));
        mensajes();
    }
    @Override protected void onActivityResult(int request,int result,Intent data) {
        super.onActivityResult(request,result,data);
        if(request==1 && result==Activity.RESULT_OK && data!=null) seleccionar(data.getStringExtra("SCAN_RESULT"));
    }
    private void seleccionar(String valor) {
        String sensor=QrLlaveroParser.extraerIdentificador(valor);
        if(sensor==null) mensaje("Código inválido. Usa S- seguido de 3 a 6 dígitos.");
        else devolver(sensor);
    }
    private void devolver(String sensor) {
        setResult(RESULT_OK,new Intent().putExtra("sensor",sensor));
        finish();
    }
}
