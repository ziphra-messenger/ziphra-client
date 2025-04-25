package com.privacity.cliente.singleton;


import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.countdown.SingletonPasswordInMemoryLifeTime;
import com.privacity.cliente.singleton.localconfiguration.SingletonTimeMessage;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.cliente.singleton.observers.ObserverPassword;
import com.privacity.cliente.singleton.observers.ObserverPasswordGrupo;
import com.privacity.cliente.singleton.observers.ObserverUnread;
import com.privacity.cliente.singleton.reconnect.SingletonReconnect;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerTime;
import com.privacity.cliente.singleton.toast.SingletonToastManager;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.cliente.singleton.usuario.SingletonUsuario;
import com.privacity.common.SingletonReset;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Singletons {

    public static ObserverUnread unread() {
        return ObserverUnread.getInstance();
    }

    public static SingletonReconnect reconnect() {
        return SingletonReconnect.getInstance();
    }

    public static SingletonUsuario usuario() {
        return SingletonUsuario.getInstance();
    }

    public static SingletonServerTime serverTime() {
        return SingletonServerTime.getInstance();
    }

    public static SingletonLoginValues loginValues() {
        return SingletonLoginValues.getInstance();
    }

    public static SingletonUsuarioNickname usuarioNickname() {
        return SingletonUsuarioNickname.getInstance();
    }

    public static SingletonSessionFinish sessionFinish() {
        return SingletonSessionFinish.getInstance();
    }

    public static SingletonValues values() {
        return SingletonValues.getInstance();
    }

    public static UtilsStringSingleton utilsString() {
        return UtilsStringSingleton.getInstance();
    }


    public static SingletonMyAccountConfLockDownTimer myAccountConfLockDownTimer() {
        return SingletonMyAccountConfLockDownTimer.getInstance();
    }
    public static SingletonTimeMessage timeMessage() {
        return SingletonTimeMessage.getInstance();
    }
    public static SingletonPasswordInMemoryLifeTime passwordInMemoryLifeTime() {
        return SingletonPasswordInMemoryLifeTime.getInstance();
    }

    public static ObserverGrupo observerGrupo() {
        return ObserverGrupo.getInstance();
    }

    public static ObserverMessage observerMessage() {
        return ObserverMessage.getInstance();
    }

    public static ObserverPassword observerPassword() {
        return ObserverPassword.getInstance();
    }

    public static ObserverPasswordGrupo observerPasswordGrupo() {
        return ObserverPasswordGrupo.getInstance();
    }

    public static SingletonServerConfiguration serverConfiguration() {
        return SingletonServerConfiguration.getInstance();
    }

    public static SingletonToastManager toastManager() {
        return SingletonToastManager.getInstance();
    }

    public static void resetAll() {
        SingletonSessionClosing.getInstance().setClosing(true);
        Arrays.stream(Singletons.class.getDeclaredMethods()).forEach(str -> resetSingleton(str));
        System.gc ();
        System.runFinalization ();

        SingletonSessionClosing.getInstance().setClose(true);
    }

    private static void resetSingleton(Method str) {


        if ( Arrays.stream(str.getReturnType().getInterfaces()).anyMatch(s -> s.equals(SingletonReset.class))){
            try {
                SingletonReset r = (SingletonReset)str.invoke(null);
                System.out.println(str.getName().equals("values"));
                if (!str.getName().equals("values")) {
                    r.reset();
                }
                System.out.println(str.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
       // resetAll();
    }
}
