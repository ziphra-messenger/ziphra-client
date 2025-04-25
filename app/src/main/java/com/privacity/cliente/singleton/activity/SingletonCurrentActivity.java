package com.privacity.cliente.singleton.activity;

import android.app.Activity;

import com.privacity.cliente.activity.main.MainActivi2ty;
import com.privacity.cliente.activity.message.MessageActivity;

import lombok.Getter;
import lombok.Setter;

public class SingletonCurrentActivity {
    @Getter
    @Setter
    private boolean reLoad;
    private Activity lastActivity;
    private Activity activity;
    static private SingletonCurrentActivity instance;
    @Getter
    private MessageActivity messageActivity;
    @Getter
    private MainActivi2ty mainActivity;
    private SingletonCurrentActivity(){

    }

    public static SingletonCurrentActivity getInstance() {
        if (instance == null){
            instance= new SingletonCurrentActivity();
        }
        return instance;
    }

    public Activity get() {
        return activity;
    }

    public void set(Activity activity) {

        if (this.activity != null && activity.equals(this.activity)) return;

        if (activity instanceof MessageActivity){
            this.messageActivity = (MessageActivity) activity;
        }
        if (activity instanceof MainActivi2ty){
            this.mainActivity = (MainActivi2ty) activity;
        }
        lastActivity=activity;
        this.activity = activity;

    }
    public void finish(){
        activity=lastActivity;
    }

    //public boolean isLastActivityEqualsCurrectActivity(){
    //    return activity == lastActivity;
    //}
}
