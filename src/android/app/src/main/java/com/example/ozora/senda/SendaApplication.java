package com.example.ozora.senda;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.ozora.senda.auth.SessionManager;

/** Bloquea la interfaz al salir, sin revocar la sesión ni detener los recorridos. */
public final class SendaApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int visibles;
    private final Runnable bloquear = SessionManager::bloquearAcceso;

    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }
    @Override public void onActivityStarted(Activity activity) {
        visibles++;
        handler.removeCallbacks(bloquear);
    }
    @Override public void onActivityStopped(Activity activity) {
        visibles--;
        // Evita bloquear por transiciones entre pantallas o cambios de configuración.
        if (visibles == 0 && !activity.isChangingConfigurations()) handler.postDelayed(bloquear, 700);
    }
    @Override public void onActivityCreated(Activity activity, Bundle state) { }
    @Override public void onActivityResumed(Activity activity) { }
    @Override public void onActivityPaused(Activity activity) { }
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle state) { }
    @Override public void onActivityDestroyed(Activity activity) { }
}
