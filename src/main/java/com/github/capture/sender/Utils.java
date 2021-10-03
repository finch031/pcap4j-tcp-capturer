package com.github.capture.sender;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2021-09-07 10:02
 * @description utils.
 */
public final class Utils {
    private static final SimpleDateFormat DEFAULT_SIMPLE_DATE_FORMAE =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Utils(){
        // no instance.
    }

    public static void sleepQuietly(long millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException ie){
            // ignore.
        }
    }

    public static String formatDateTime(long dateTimeMills,String pattern){
        SimpleDateFormat sdf;
        if(pattern == null){
            sdf = DEFAULT_SIMPLE_DATE_FORMAE;
        }else{
            sdf = new SimpleDateFormat(pattern);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTimeMills);
        return sdf.format(calendar.getTime());
    }
}
