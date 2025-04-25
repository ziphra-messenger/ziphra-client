package com.privacity.cliente.singleton.toast;

import android.app.Activity;
import android.widget.Toast;

import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;

public class SingletonToastManager implements SingletonReset {

    static private SingletonToastManager instance;


    private SingletonToastManager(){

    }

    public static SingletonToastManager getInstance() {
        if (instance == null){
            instance= new SingletonToastManager();
        }
        return instance;
    }

    private Toast t;
    private String toastString="";
    private boolean toastShowing=false;

    public void showToadShort(Activity activity, String txt) {
        showToad(activity,txt,Toast.LENGTH_SHORT);
    }
    public void showToadLong(Activity activity, String txt) {
        showToad(activity,txt,Toast.LENGTH_LONG);
    }
    public void showToad(Activity activity, String txt,int duration) {
        if (toastShowing){
            if (txt.equals(toastString)) return;
            t.cancel();
            toastShowing=false;
        }

        t = Toast.makeText(activity, txt, duration);
        t.addCallback(new Toast.Callback() {
            @Override
            public void onToastShown() {
                super.onToastShown();
                toastShowing=true;
            }

            @Override
            public void onToastHidden() {
                super.onToastHidden();
                toastShowing=false;
            }
        });
        if (!toastShowing){
            toastString=txt;
            t.show();
        }
    }
    @Override
    public void reset() {

        ToolsUtil.forceGarbageCollector(instance);
    }
}
