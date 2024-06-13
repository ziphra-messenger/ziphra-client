package com.privacity.cliente.singleton.impl;


import android.view.View;

import com.privacity.cliente.singleton.interfaces.SingletonReset;

import lombok.Data;

@Data
public class SingletonServer implements SingletonReset {

    private String appServer;
    private String wsServer;
    private String helpServer;
    private boolean developerMode;

    public void isShowDeveloperModeView(View v){
        if (SingletonServer.getInstance().isDeveloperMode()) {
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
    }

    private static SingletonServer instance;

        public static SingletonServer getInstance() {

            if (instance == null){
                instance = new SingletonServer();
            }
            return instance;
        }

        private SingletonServer() { }


    @Override
    public void reset() {
        instance=null;
    }
}
