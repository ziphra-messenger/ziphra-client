package com.privacity.cliente.rest.restcalls;

import android.app.Activity;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.restcalls.serverconf.GetGralConf;
import com.privacity.cliente.rest.restcalls.serverconf.GetServerPublicKey;
import com.privacity.cliente.rest.restcalls.serverconf.GetTime;

public class ServerConfRest {

    public static void getTime(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {
        GetTime.getTime(context, callbackRest, innerCallbackRest);
    }

    public static void getGralConf(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {
        GetGralConf.getGralConf(context, callbackRest, innerCallbackRest);
    }

    public static void getServerPublicKey(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {
        GetServerPublicKey.getServerPublicKey(context, callbackRest, innerCallbackRest);
    }
}
