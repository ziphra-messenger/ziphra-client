package com.privacity.cliente.singleton;

import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.cliente.singleton.observers.ObserverPassword;
import com.privacity.cliente.singleton.observers.ObserverPasswordGrupo;

public class Observers {

    public static ObserverGrupo grupo(){
        return ObserverGrupo.getInstance();
    }

    public static ObserverMessage message(){
        return ObserverMessage.getInstance();
    }

    public static ObserverPassword password(){
        return ObserverPassword.getInstance();
    }

    public static ObserverPasswordGrupo passwordGrupo(){
        return ObserverPasswordGrupo.getInstance();
    }

    public static void reset() {
        grupo().reset();
        message().reset();
        password().reset();
        passwordGrupo().reset();
    }

}
