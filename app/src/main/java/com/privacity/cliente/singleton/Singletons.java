package com.privacity.cliente.singleton;


import com.privacity.cliente.singleton.reconnect.SingletonReconnect;

public class Singletons {

    public static SingletonReconnect reconnect(){
        return SingletonReconnect.getInstance();
    }

    public static void reset(){
        reconnect().reset();
    }

}
