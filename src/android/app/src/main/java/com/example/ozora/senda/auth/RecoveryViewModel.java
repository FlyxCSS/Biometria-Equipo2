package com.example.ozora.senda.auth;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ozora.senda.R;
import com.example.ozora.senda.network.ApiClient;
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
        State(Step step, boolean busy, String message) {
            this.step = step; this.busy = busy; this.message = message;
        }
    }
    final MutableLiveData<State> state = new MutableLiveData<>(new State(Step.EMAIL, false, ""));
    private final ExecutorService network = Executors.newSingleThreadExecutor();
    public RecoveryViewModel(Application application) { super(application); }
    LiveData<State> observe() { return state; }
    void showLink() { if (!state.getValue().busy) state.setValue(new State(Step.LINK, false, "")); }
    void showEmail() { if (!state.getValue().busy) state.setValue(new State(Step.EMAIL, false, "")); }
    void request(String email) {
        if (state.getValue().busy) return;
        state.setValue(new State(Step.EMAIL, true, getApplication().getString(R.string.auth_sending)));
        network.execute(() -> {
            try {
                new ApiClient(getApplication()).pedir("POST", "/recuperaciones", null,
                        new JSONObject().put("correo", email));
                state.postValue(new State(Step.LINK, false,
                        getApplication().getString(R.string.auth_recovery_received)));
            } catch (Exception error) {
                state.postValue(new State(Step.EMAIL, false, error instanceof ApiClient.Error
                        ? error.getMessage() : getApplication().getString(R.string.auth_recovery_error)));
            }
        });
    }
    void reset(String token, String password) {
        if (state.getValue().busy) return;
        state.setValue(new State(Step.LINK, true, "Actualizando contraseña…"));
        network.execute(() -> {
            try {
                new ApiClient(getApplication()).pedir("POST", "/recuperaciones/confirmacion", null,
                        new JSONObject().put("token", token).put("contrasena_nueva", password));
                state.postValue(new State(Step.DONE, false, ""));
            } catch (Exception error) {
                state.postValue(new State(Step.LINK, false, error instanceof ApiClient.Error
                        ? error.getMessage() : getApplication().getString(R.string.recovery_reset_error)));
            }
        });
    }
    @Override protected void onCleared() { network.shutdown(); }
}
