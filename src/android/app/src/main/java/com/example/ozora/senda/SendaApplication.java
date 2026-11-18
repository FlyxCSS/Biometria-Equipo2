package com.example.ozora.senda;

import android.app.Application;
import com.example.ozora.senda.auth.SessionManager;

/** Restaura la sesión antes de abrir actividades o servicios. */
public final class SendaApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        SessionManager.initialize(this);
    }
}
