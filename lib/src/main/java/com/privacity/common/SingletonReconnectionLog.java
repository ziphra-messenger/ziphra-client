package com.privacity.common;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SingletonReconnectionLog implements SingletonReset {
    private static SingletonReconnectionLog instance;
    private static final String TAG = "SingletonReconnectionLog";
    private String logtxt="";
    public static SingletonReconnectionLog getInstance() {

        if (instance == null) {
            instance = new SingletonReconnectionLog();
        }
        return instance;
    }

    @Override
    public void reset() {
        logtxt="";
        instance=null;
    }
    String lastLog="";
    public String getLog(){
        return logtxt;
    }
    public void addLog(String txt){
        if (lastLog.equals(txt)) return;

        lastLog=txt;
        String newTxt = getCurrentTimeStamp() + " > " + txt  ;
        Log.i(TAG, newTxt);
        logtxt = logtxt + newTxt + "\n";
    }


    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate ;
    }
}
