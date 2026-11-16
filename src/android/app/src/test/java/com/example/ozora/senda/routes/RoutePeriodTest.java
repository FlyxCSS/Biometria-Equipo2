package com.example.ozora.senda.routes;

import java.util.TimeZone;
import java.text.SimpleDateFormat;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoutePeriodTest {
    private final TimeZone zone=TimeZone.getTimeZone("Europe/Madrid");
    private long time(String date) throws Exception {
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        f.setTimeZone(zone);
        return f.parse(date).getTime();
    }
    @Test public void semanaEmpiezaElLunesYSieteDiasIncluyeHoy() throws Exception {
        long now=time("2026-11-13 15:30");
        assertArrayEquals(new long[]{time("2026-11-09 00:00"),time("2026-11-16 00:00")},RoutePeriod.THIS_WEEK.bounds(now,zone));
        assertArrayEquals(new long[]{time("2026-11-07 00:00"),time("2026-11-14 00:00")},RoutePeriod.LAST_SEVEN_DAYS.bounds(now,zone));
        assertArrayEquals(new long[]{time("2026-11-12 00:00"),time("2026-11-13 00:00")},RoutePeriod.YESTERDAY.bounds(now,zone));
    }
    @Test public void cambioDeHoraMantieneLosDiasDelCalendario() throws Exception {
        long[] today=RoutePeriod.TODAY.bounds(time("2026-10-25 15:00"),zone);
        assertEquals(25*60*60*1000L,today[1]-today[0]);
        assertArrayEquals(new long[]{time("2026-10-25 00:00"),time("2026-10-26 00:00")},today);
        long[] yesterday=RoutePeriod.YESTERDAY.bounds(time("2026-10-26 10:00"),zone);
        assertArrayEquals(today,yesterday);
    }
}
