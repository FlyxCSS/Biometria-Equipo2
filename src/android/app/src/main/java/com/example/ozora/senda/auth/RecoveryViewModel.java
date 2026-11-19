/*
 * Nombre: RecoveryViewModel.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.auth;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ozora.senda.R;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.network.LogicaFake;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Conserva el estado de la petición al girar la pantalla sin guardar secretos en Bundle. */
public final class RecoveryViewModel extends AndroidViewModel {
    enum Step { EMAIL, LINK, DONE }
    static final class State {
        final Step step;
        final boolean busy;
        final String message;

        /** --------------------
         * Diseño lógico: step: Step, busy: B, message: Text --> State() -->
         * State <--
         * Descripción: Construye State. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        State(Step step, boolean busy, String message) {
            this.step = step; this.busy = busy; this.message = message;
        }
    }
    final MutableLiveData<State> state = new MutableLiveData<>(new State(Step.EMAIL, false, ""));
    private final ExecutorService network = Executors.newSingleThreadExecutor();

    /** --------------------
     * Diseño lógico: application: Application --> RecoveryViewModel() -->
     * RecoveryViewModel <--
     * Descripción: Construye RecoveryViewModel. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public RecoveryViewModel(Application application) { super(application); }

    /** --------------------
     * Diseño lógico: observe() -->
     * LiveDataState <--
     * Descripción: Observe. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    LiveData<State> observe() { return state; }

    /** --------------------
     * Diseño lógico: showLink() -->
     * Descripción: Show Link. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    void showLink() { if (!state.getValue().busy) state.setValue(new State(Step.LINK, false, "")); }

    /** --------------------
     * Diseño lógico: showEmail() -->
     * Descripción: Show Email. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    void showEmail() { if (!state.getValue().busy) state.setValue(new State(Step.EMAIL, false, "")); }

    /** --------------------
     * Diseño lógico: email: Text --> request() -->
     * Descripción: Request. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    void request(String email) {
        if (state.getValue().busy) return;
        state.setValue(new State(Step.EMAIL, true, getApplication().getString(R.string.auth_sending)));
        network.execute(() -> {
            try {
                new LogicaFake(getApplication()).solicitarRecuperacion(
                        new JSONObject().put("correo", email));
                state.postValue(new State(Step.LINK, false,
                        getApplication().getString(R.string.auth_recovery_received)));
            } catch (Exception error) {
                state.postValue(new State(Step.EMAIL, false, error instanceof ApiClient.Error
                        ? error.getMessage() : getApplication().getString(R.string.auth_recovery_error)));
            }
        });
    }

    /** --------------------
     * Diseño lógico: token: Text, password: Text --> reset() -->
     * Descripción: Reset. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    void reset(String token, String password) {
        if (state.getValue().busy) return;
        state.setValue(new State(Step.LINK, true, "Actualizando contraseña…"));
        network.execute(() -> {
            try {
                new LogicaFake(getApplication()).restablecerContrasena(
                        new JSONObject().put("token", token).put("contrasena_nueva", password));
                state.postValue(new State(Step.DONE, false, ""));
            } catch (Exception error) {
                state.postValue(new State(Step.LINK, false, error instanceof ApiClient.Error
                        ? error.getMessage() : getApplication().getString(R.string.recovery_reset_error)));
            }
        });
    }

    /** --------------------
     * Diseño lógico: onCleared() -->
     * Descripción: On Cleared. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onCleared() { network.shutdown(); }
}
