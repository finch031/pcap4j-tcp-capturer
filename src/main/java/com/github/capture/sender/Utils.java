package com.github.capture.sender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2021-09-07 10:02
 * @description utils.
 */
public final class Utils {
    private static final Logger LOG = LogManager.getLogger(Utils.class);

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

    /**
     * get the stack trace from an exception as a string
     */
    public static String stackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
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

    public static void closeSocket(Socket socket){
        if(socket != null){
            try{
                socket.close();
                socket = null;
            }catch (IOException ioe){
                LOG.error("close socket error: " + stackTrace(ioe));
            }
        }
    }

    public static void closeOutputStream(OutputStream os){
       if(os != null){
           try{
               os.close();
               os = null;
           }catch (IOException ioe){
               LOG.error("close output stream error: " + stackTrace(ioe));
           }
       }
    }
}
