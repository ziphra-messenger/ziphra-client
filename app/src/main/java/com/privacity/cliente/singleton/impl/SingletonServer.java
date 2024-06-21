package com.privacity.cliente.singleton.impl;


import android.view.View;

import com.privacity.cliente.singleton.interfaces.SingletonReset;
import com.privacity.common.enumeration.EnvironmentEnum;

import lombok.Data;

@Data
public class SingletonServer implements SingletonReset {

    private String appServer;
    private String wsServer;
    private String helpServer;
    private boolean developerMode;
    private EnvironmentEnum environment;

    public int getVisibility() {
        return 8;
    }

    public boolean isDeveloper() {
        return (EnvironmentEnum.DEVELOPER.equals(environment));
    }

    private static SingletonServer instance;

    public static SingletonServer getInstance() {

        if (instance == null) {
            instance = new SingletonServer();
        }
        return instance;
    }

    private SingletonServer() {
    }


    @Override
    public void reset() {
        instance = null;
    }

    public View setVisibility(View v) {
        if (environment.equals(EnvironmentEnum.DEVELOPER)) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }

        return v;
    }
}
