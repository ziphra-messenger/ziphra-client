package com.privacity.cliente.singleton.localconfiguration;

import android.app.Activity;

import com.neovisionaries.i18n.LanguageCode;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

public class SingletonLang {

    private String currentLang= LanguageCode.en.toString();

    static private SingletonLang instance;
    private SingletonLang(){

    }

    public static SingletonLang getInstance() {
        if (instance == null){
            instance= new SingletonLang();
        }
        return instance;
    }

    public void save(Activity activity, String newLang){
        currentLang=newLang;
        SharedPreferencesUtil.saveLanguage(activity,newLang );
    }

    public String get(){

        return currentLang;
    }

    public String get(Activity activity){
        if(currentLang == null){
            currentLang = SharedPreferencesUtil.getLanguage(activity);
        }
        return currentLang;
    }
}
