package com.privacity.cliente.singleton.localconfiguration;

import android.app.Activity;

import com.neovisionaries.i18n.LanguageCode;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

public class SingletonTimeMessage {

    private int seconds;

    static private SingletonTimeMessage instance;
    private SingletonTimeMessage(){

    }

    public static SingletonTimeMessage getInstance() {
        if (instance == null){
            instance= new SingletonTimeMessage();
        }
        return instance;
    }

    public void save(Activity activity, int seconds){
        if ( this.seconds == seconds) return;

        this.seconds=seconds;
        SharedPreferencesUtil.saveTimeMessage(activity,seconds+"" );
    }

    public int get(Activity activity){
        if(seconds == 0){
            seconds = Integer.parseInt( SharedPreferencesUtil.getTimeMessage(activity));
        }
        return seconds;
    }
}