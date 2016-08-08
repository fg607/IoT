package com.hardwork.fg607.iot.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by fg607 on 16-7-28.
 */
public class IoTUtils {

    public static final String[] WEEKDAYS = {"日","一","二","三","四","五","六"};

    public static final int SUNDAY = 1 << 6;
    public static final int MONDAY = 1 << 5;
    public static final int TUESDAY = 1 << 4;
    public static final int WEDNESDAY = 1 << 3;
    public static final int THURSDAY = 1 << 2;
    public static final int FRIDAY = 1 << 1;
    public static final int SATURDAY = 1 << 0;

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static int getDayOfWeek(){

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.get(Calendar.DAY_OF_WEEK);
    }


    public static long getIntervalStartWeek(){

        Date date = new Date(System.currentTimeMillis());

        long now = date.getHours()*60*60*1000+date.getMinutes()*60*1000;

        long startOfDay = 24*60*60*1000;

        return Math.abs(startOfDay-now);
    }


}
