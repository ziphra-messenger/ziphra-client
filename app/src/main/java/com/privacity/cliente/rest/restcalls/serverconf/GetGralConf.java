package com.privacity.cliente.rest.restcalls.serverconf;

import android.app.Activity;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.servergralconf.SystemGralConf;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class GetGralConf {

    public static void getGralConf(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {


        if (SingletonServerConfiguration.getInstance().getSystemGralConf() != null){

            GetServerPublicKey.getServerPublicKey(context, callbackRest, innerCallbackRest);

            return;
        }
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.SERVER_CONF_UNSECURE);
        p.setAction(ProtocoloActionsEnum.SERVER_CONF_UNSECURE_GET_GRAL_CONF);
        RestExecute.doit(context, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        if (callbackRest!= null) callbackRest.response(response);

                        SystemGralConf r = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), SystemGralConf.class);
                        SingletonServerConfiguration.getInstance().setSystemGralConf(r);

                        GetServerPublicKey.getServerPublicKey(context, callbackRest, innerCallbackRest);
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        if (callbackRest!= null) callbackRest.onError(response);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                },false);
    }
}
