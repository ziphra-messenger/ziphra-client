package com.privacity.cliente.singleton.usuario;

import android.util.Log;

import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.util.ToolsUtil;

import lombok.Getter;
import lombok.Setter;

public class SingletonSessionClosing {
    private static final String TAG = "SingletonSessionClosing";

    private boolean closeApp=false;


    public boolean isCloseApp() {
        Log.d(TAG, "isCloseApp " + closing);
        return closeApp;
    }

    public void setCloseApp(boolean closeApp) {
        Log.d(TAG, "setCloseApp " + closeApp);
        this.closeApp = closeApp;
    }

    public boolean isClosing() {
        Log.d(TAG, "isClosing " + closing);
        return closing;
    }

    public void setClosing(boolean closing) {
        Log.d(TAG, "setClosing " + closing);
        this.closing = closing;
    }

    public boolean isClose() {
        Log.d(TAG, "isClose " + close);
        return close;
    }

    public void setClose(boolean close) {
        Log.d(TAG, "setClose " + close);
        this.close = close;
    }

    private boolean closing=false;

    private boolean close=false;
    private static SingletonSessionClosing instance;

    public static SingletonSessionClosing getInstance() {

        if (instance == null){
            instance = new SingletonSessionClosing();
        }
        return instance;
    }

    private SingletonSessionClosing() { }





}
