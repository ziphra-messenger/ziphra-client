package com.privacity.cliente.rest.restcalls.serverconf;

import android.app.Activity;

import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class GetTime {

    public static void getTime(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.SERVER_CONF_UNSECURE);
        p.setAction(ProtocoloActionsEnum.SERVER_CONF_UNSECURE_GET_TIME);
        RestExecute.doit(context, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {


                        LocalDateTime l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), LocalDateTime.class);
                        SingletonValues.getInstance().setServerTime(l);

                        GetGralConf.getGralConf(context, callbackRest, innerCallbackRest);

                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                        if (callbackRest!= null) callbackRest.onError(response);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        if (callbackRest!= null) callbackRest.beforeShowErrorMessage(msg);
                    }

                },false);
    }
}
