package com.privacity.cliente.rest.restcalls.serverconf;

import android.app.Activity;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class GetTime {

    public static void getTime(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {

        if (Singletons.serverTime().getServerTime() != null){
            GetGralConf.getGralConf(context, callbackRest, innerCallbackRest);
            return;
        };





        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.SERVER_CONF_UNSECURE);
        p.setAction(ProtocoloActionsEnum.SERVER_CONF_UNSECURE_GET_TIME);
        RestExecute.doit(context, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {


                        LocalDateTime l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), LocalDateTime.class);
                        Singletons.serverTime().setServerTime(l);

                        GetGralConf.getGralConf(context, callbackRest, innerCallbackRest);

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        if (callbackRest!= null) callbackRest.onError(response);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        if (callbackRest!= null) callbackRest.beforeShowErrorMessage(msg);
                    }

                },false);
    }
}
