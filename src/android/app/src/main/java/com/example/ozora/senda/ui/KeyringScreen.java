package com.example.ozora.senda.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.routes.RouteManager;
import java.text.DateFormat;
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
    public void refresh(SessionManager session, RouteManager routes, SharedPreferences prefs,
                        String sensor, boolean active, boolean receiving, String state) {
        boolean selected = sensor != null;
        text(R.id.keyring_id, selected ? sensor : "—");
        text(R.id.keyring_park, context.getString(R.string.home_park, session.worker.parkId));
        text(R.id.keyring_status, context.getString(!selected ? R.string.keyring_none
                : receiving ? R.string.keyring_receiving : R.string.keyring_silent));
        TextView status = root.findViewById(R.id.keyring_status);
        GradientDrawable pill = new GradientDrawable();
        pill.setCornerRadius(48 * context.getResources().getDisplayMetrics().density);
        pill.setColor(receiving ? 0xffd5dfce : 0xffebe5db);
        status.setBackground(pill);
        text(R.id.keyring_reception, receiving ? "● Recibiendo" : "● Sin recibir");
        ((TextView)root.findViewById(R.id.keyring_reception)).setTextColor(receiving ? 0xff315d3c : 0xff954432);
        root.findViewById(R.id.keyring_details).setVisibility(selected ? View.VISIBLE : View.GONE);
        text(R.id.text_device, context.getString(R.string.keyring_state,
                active ? state : context.getString(R.string.keyring_wait)));
        text(R.id.text_last_o3, context.getString(R.string.keyring_last));
        if (selected) {
            try (Cursor c = routes.historial(session.worker, sensor, 1, 0)) {
                if (c.moveToFirst()) {
                    String date = c.getString(1);
                    try { date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                            .format(new Date(SessionManager.instante(date))); }
                    catch (java.text.ParseException ignored) { }
                    text(R.id.text_last_o3, context.getString(R.string.keyring_reading, c.getInt(0), date));
                }
            }
        }
        long linked = prefs.getLong("sensor_linked_at", 0);
        text(R.id.keyring_link_date, linked == 0 ? context.getString(R.string.keyring_link_unknown)
                : context.getString(R.string.keyring_link_date,
                DateFormat.getDateInstance(DateFormat.LONG).format(new Date(linked))));
        text(R.id.button_select_sensor, context.getString(selected ? R.string.keyring_unlink : R.string.keyring_scan));
        root.findViewById(R.id.button_select_sensor).setEnabled(!active);
        text(R.id.keyring_hint, context.getString(active ? R.string.keyring_active_hint
                : selected ? R.string.keyring_local_hint : R.string.keyring_hint));
    }
}
