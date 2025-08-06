package com.privacity.cliente.singleton.localconfiguration;

import android.app.Activity;

import com.privacity.cliente.common.enumerators.ButtonSetupEnum;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

public class SingletonButtonSetup {

    private ButtonSetupEnum buttonSetup;
    static private SingletonButtonSetup instance;
    
    private SingletonButtonSetup(){

    }

    public static SingletonButtonSetup getInstance() {
        if (instance == null){
            instance= new SingletonButtonSetup();
        }
        return instance;
    }



    public boolean isHideText(Activity activity){
        if(buttonSetup == null){
            buttonSetup = ButtonSetupEnum.valueOf(SharedPreferencesUtil.getButtonSetup(activity));
        }
        return (buttonSetup.equals(ButtonSetupEnum.HIDE_TEXT));

    }
    public boolean isHideIcon(Activity activity){
        if(buttonSetup == null){
            buttonSetup = ButtonSetupEnum.valueOf(SharedPreferencesUtil.getButtonSetup(activity));
        }
        return (buttonSetup.equals(ButtonSetupEnum.HIDE_ICON));
    }
    public ButtonSetupEnum get(Activity activity){
        if(buttonSetup == null){
            buttonSetup = ButtonSetupEnum.valueOf(SharedPreferencesUtil.getButtonSetup(activity));
        }
        return buttonSetup;
    }
    public void save(Activity activity, String newButtonSetup){
        save(activity,ButtonSetupEnum.valueOf(newButtonSetup) );
    }

    public void save(Activity activity, ButtonSetupEnum newButtonSetup){
        buttonSetup=newButtonSetup;
        SharedPreferencesUtil.saveButtonSetup(activity,buttonSetup.name() );
    }  
}
