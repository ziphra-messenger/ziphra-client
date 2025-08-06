package com.privacity.cliente.singleton.activity;

import android.app.Activity;

import com.privacity.cliente.activity.main.MainActi22vity;
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
    private MainActi22vity mainActivity;
    private SingletonCurrentActivity(){

    }

    public static SingletonCurrentActivity getInstance() {
        if (instance == null){

            synchronized (SingletonCurrentActivity.class) {
                if (instance == null) {      // Segundo chequeo (con bloqueo)
                    instance = new SingletonCurrentActivity();
                }
            }

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
        if (activity instanceof MainActi22vity){
            this.mainActivity = (MainActi22vity) activity;
        }
        lastActivity=activity;
        this.activity = activity;

    }
    public void finish(){
        activity=lastActivity;
    }

    //public boolean isLastActivityEqualsCurrectActivity(){
    //    return activity == lastActivity;

/*

    public class Singleton {
        private Singleton() {}

        private static class Holder {
            private static final Singleton INSTANCE = new Singleton();
        }

        public static Singleton getInstance() {
            return Holder.INSTANCE;
        }
    }*/

    //}
}
