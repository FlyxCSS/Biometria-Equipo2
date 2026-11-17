package com.example.ozora.senda.ui;

/** Un llavero seleccionado no implica estar recibiendo sus anuncios BLE. */
public final class KeyringStatus {
    private KeyringStatus() {}
    public static boolean receiving(boolean active, long route, long captureRoute,
                                    long lastAdvertisement, long now, int timeoutSeconds) {
        long age = now - lastAdvertisement;
        return active && route > 0 && route == captureRoute && lastAdvertisement > 0
                && age >= 0 && age < timeoutSeconds * 1000L;
    }
}
