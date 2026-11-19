package com.example.ozora.senda.ui;

import android.content.Context;
import android.os.SystemClock;
import com.example.ozora.senda.bluetooth.EstadoLlavero;
import java.util.Locale;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.routes.RouteManager;
import com.google.android.material.button.MaterialButton;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** Presenta solo información real: no simula batería, firmware o recepción. */
public final class KeyringScreen {
    private final View root;
    private final Context context;
    public KeyringScreen(View root) { this.root = root; context = root.getContext(); }
    private void text(int id, String value) {
        TextView view = root.findViewById(id);
        if (!android.text.TextUtils.equals(view.getText(), value)) view.setText(value);
    }
    private int color(int id) { return context.getColor(id); }

    public void refresh(SessionManager session, RouteManager routes, SharedPreferences prefs,
                        String sensor, boolean active, boolean receiving, String state) {
        boolean selected = sensor != null;
        // Telemetría real del llavero (batería y temperatura) cuando ha llegado por BLE.
        long ahora = SystemClock.elapsedRealtime();
        Integer bateria = EstadoLlavero.COMPARTIDO.bateria(sensor, ahora);
        Double temperatura = EstadoLlavero.COMPARTIDO.temperatura(sensor, ahora);
        text(R.id.keyring_battery_value, bateria == null ? "—" : bateria + "%");
        root.findViewById(R.id.keyring_battery_value).setContentDescription(bateria == null
                ? context.getString(R.string.keyring_battery_unknown) : context.getString(R.string.keyring_battery_percent, bateria));
        View bar = root.findViewById(R.id.keyring_battery_bar);
        if (bateria == null) bar.setVisibility(View.INVISIBLE);
        else {
            bar.setVisibility(View.VISIBLE);
            bar.setBackgroundTintList(ColorStateList.valueOf(bateria <= 20 ? color(R.color.status_error_strong) : color(R.color.status_success)));
            View track = (View) bar.getParent();
            int level = Math.max(0, Math.min(100, bateria));
            track.post(() -> {
                android.view.ViewGroup.LayoutParams p = bar.getLayoutParams();
                p.width = Math.round(track.getWidth() * level / 100f);
                bar.setLayoutParams(p);
            });
        }
        text(R.id.keyring_temperature_value, temperatura == null ? "No disponible"
                : String.format(Locale.getDefault(), "%.1f °C", temperatura));
        text(R.id.keyring_id, selected ? sensor : "Sin llavero vinculado");
        TextView id = root.findViewById(R.id.keyring_id);
        id.setTextSize(selected ? 30 : 24);
        id.setLetterSpacing(selected ? 0.04f : -0.01f);
        text(R.id.keyring_park, context.getString(R.string.home_park, session.worker.parkId));

        // Chip de estado: azul si vinculado, rojo si no hay llavero.
        TextView status = root.findViewById(R.id.keyring_status);
        text(R.id.keyring_status, context.getString(!selected ? R.string.keyring_none
                : receiving ? R.string.keyring_receiving : R.string.keyring_silent));
        int tone = selected ? color(R.color.status_connected) : color(R.color.status_error);
        int surface = selected ? color(R.color.status_connected_surface) : color(R.color.status_error_surface);
        status.setTextColor(tone);
        status.setBackgroundTintList(ColorStateList.valueOf(surface));
        status.setCompoundDrawableTintList(ColorStateList.valueOf(selected ? tone : color(R.color.status_error_strong)));

        ImageView image = root.findViewById(R.id.keyring_image);
        image.setImageTintList(selected ? null : ColorStateList.valueOf(0xff9aa59e));
        root.findViewById(R.id.keyring_halo).setBackgroundTintList(ColorStateList.valueOf(
                selected ? color(R.color.senda_tile) : color(R.color.senda_tile_neutral)));

        // Tarjeta de señal BLE.
        text(R.id.keyring_reception, receiving ? "Recibiendo" : "Sin recibir");
        int bleTone = receiving ? color(R.color.status_connected) : selected ? color(R.color.status_neutral) : color(R.color.status_error);
        ((TextView) root.findViewById(R.id.keyring_reception)).setTextColor(bleTone);
        ImageView ble = root.findViewById(R.id.keyring_ble_icon);
        ble.setImageTintList(ColorStateList.valueOf(receiving ? color(R.color.status_connected)
                : selected ? color(R.color.status_neutral) : color(R.color.status_error_strong)));
        ble.setBackgroundTintList(ColorStateList.valueOf(receiving ? color(R.color.status_connected_surface)
                : selected ? color(R.color.senda_tile_neutral) : color(R.color.status_error_surface)));
        root.findViewById(R.id.keyring_ble_dot).setBackgroundTintList(ColorStateList.valueOf(receiving
                ? color(R.color.status_connected) : selected ? 0xffb8bdb9 : color(R.color.status_error_strong)));

        root.findViewById(R.id.keyring_details).setVisibility(selected ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.keyring_link_card).setVisibility(selected ? View.GONE : View.VISIBLE);
        TextView device = root.findViewById(R.id.text_device);
        text(R.id.text_device, !active ? "Sin recorrido activo" : receiving ? "● Recibiendo BLE" : "Esperando BLE");
        device.setTextColor(receiving ? color(R.color.status_connected) : color(R.color.senda_muted));
        text(R.id.text_last_o3, "Sin mediciones");
        if (selected) {
            try (Cursor c = routes.historial(session.worker, sensor, 1, 0)) {
                if (c.moveToFirst()) {
                    String date = c.getString(1);
                    try { date = cuando(SessionManager.instante(date)); }
                    catch (java.text.ParseException ignored) { }
                    text(R.id.text_last_o3, c.getInt(0) + " ppb · " + date);
                }
            }
        }
        long linked = prefs.getLong("sensor_linked_at", 0);
        text(R.id.keyring_link_date, linked == 0 ? "No disponible"
                : new SimpleDateFormat("d MMM yyyy", new Locale("es", "ES")).format(new Date(linked)));

        MaterialButton action = root.findViewById(R.id.button_select_sensor);
        text(R.id.button_select_sensor, context.getString(selected ? R.string.keyring_unlink : R.string.keyring_scan));
        action.setIconResource(selected ? R.drawable.ic_s_logout : R.drawable.ic_s_qr);
        ColorStateList background = new ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_enabled}, new int[]{}},
                new int[]{0xffe7e3da, selected ? color(R.color.status_error_surface) : color(R.color.senda_ink)});
        ColorStateList foreground = new ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_enabled}, new int[]{}},
                new int[]{color(R.color.status_neutral), selected ? color(R.color.status_error) : 0xffffffff});
        action.setBackgroundTintList(background);
        action.setTextColor(foreground);
        action.setIconTint(foreground);
        action.setEnabled(!active);
        text(R.id.keyring_hint, context.getString(active ? R.string.keyring_active_hint
                : selected ? R.string.keyring_local_hint : R.string.keyring_hint));
    }

    private static String cuando(long instant) {
        Calendar today = Calendar.getInstance();
        Calendar when = Calendar.getInstance(); when.setTimeInMillis(instant);
        String hour = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(instant));
        if (today.get(Calendar.YEAR) == when.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == when.get(Calendar.DAY_OF_YEAR))
            return "Hoy, " + hour;
        return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(instant)) + ", " + hour;
    }
}
