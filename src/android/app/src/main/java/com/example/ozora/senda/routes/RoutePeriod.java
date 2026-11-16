package com.example.ozora.senda.routes;

import java.util.Calendar;
import java.util.TimeZone;

/** Intervalos locales, con fin exclusivo para evitar duplicar las rutas de medianoche. */
public enum RoutePeriod {
    TODAY, YESTERDAY, THIS_WEEK, LAST_SEVEN_DAYS;

    public long[] bounds(long now, TimeZone zone) {
        Calendar start=Calendar.getInstance(zone);
        start.setTimeInMillis(now);
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.MINUTE,0);
        start.set(Calendar.SECOND,0);
        start.set(Calendar.MILLISECOND,0);
        Calendar end=(Calendar)start.clone();
        end.add(Calendar.DAY_OF_MONTH,1);
        if(this==YESTERDAY) {
            end=(Calendar)start.clone();
            start.add(Calendar.DAY_OF_MONTH,-1);
        } else if(this==THIS_WEEK) {
            int days=(start.get(Calendar.DAY_OF_WEEK)+5)%7;
            start.add(Calendar.DAY_OF_MONTH,-days);
            end=(Calendar)start.clone();
            end.add(Calendar.DAY_OF_MONTH,7);
        } else if(this==LAST_SEVEN_DAYS) start.add(Calendar.DAY_OF_MONTH,-6);
        return new long[]{start.getTimeInMillis(),end.getTimeInMillis()};
    }
}
