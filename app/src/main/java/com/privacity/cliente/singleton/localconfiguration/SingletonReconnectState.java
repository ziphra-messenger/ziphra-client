package com.privacity.cliente.singleton.localconfiguration;

import android.app.Activity;

import com.neovisionaries.i18n.LanguageCode;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

public class SingletonReconnectState {

    private Boolean isOpen=null;
    private Boolean isLogOpen=null;
    static private SingletonReconnectState instance;
    private SingletonReconnectState(){

    }

    public static SingletonReconnectState getInstance() {
        if (instance == null){
            instance= new SingletonReconnectState();
        }
        return instance;
    }

    public void save(Activity activity, boolean isOpenm){
        isOpen=isOpenm;
        SharedPreferencesUtil.saveReconnectState(activity,isOpen+"" );
    }
    public void saveLog(Activity activity, boolean isLogOpenm){
        isLogOpen=isLogOpenm;
        SharedPreferencesUtil.saveReconnectLogState(activity,isLogOpen+"" );
    }


    public boolean get(Activity activity){
        if(isOpen == null){
            isOpen = Boolean.valueOf( SharedPreferencesUtil.getReconnectLogState(activity));
        }
        return isOpen;
    }
    public boolean getLog(Activity activity){
        if(isLogOpen == null){
            isLogOpen = Boolean.valueOf( SharedPreferencesUtil.getReconnectState(activity));
        }
        return isLogOpen;
    }
}
