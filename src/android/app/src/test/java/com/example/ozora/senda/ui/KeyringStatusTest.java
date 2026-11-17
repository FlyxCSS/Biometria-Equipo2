package com.example.ozora.senda.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class KeyringStatusTest {
    @Test public void requiereRecorridoYAnuncioDelServicioActual() {
        assertTrue(KeyringStatus.receiving(true, 7, 7, 1000, 1500, 20));
        assertFalse(KeyringStatus.receiving(false, 7, 7, 1000, 1500, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 8, 1000, 1500, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 7, 0, 1500, 20));
        assertFalse(KeyringStatus.receiving(true, 0, 0, 1000, 1500, 20));
    }
    @Test public void elSilencioCaducaEnElLimiteYDescartaRelojesIncompatibles() {
        assertTrue(KeyringStatus.receiving(true, 7, 7, 1000, 20999, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 7, 1000, 21000, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 7, 2000, 1000, 20));
    }
}