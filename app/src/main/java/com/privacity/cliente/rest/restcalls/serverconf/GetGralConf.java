package com.privacity.cliente.rest.restcalls.serverconf;

import android.app.Activity;

import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.servergralconf.SystemGralConf;

import org.springframework.http.ResponseEntity;

public class GetGralConf {

    public static void getGralConf(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_SERVER_CONF_UNSECURE);
        p.setAction("/serverConf/getGralConf");
        RestExecute.doit(context, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {


                        SystemGralConf r = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), SystemGralConf.class);
                        SingletonValues.getInstance().setSystemGralConf(r);

                        GetServerPublicKey.getServerPublicKey(context, callbackRest, innerCallbackRest);
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                        if (callbackRest!= null) callbackRest.onError(response);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                },false);
    }
}
