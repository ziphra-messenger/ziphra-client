package com.privacity.cliente.activity.grupo.delegate;

import android.app.Activity;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class UsuarioCloseSessionRest {

    public static void  doIt(Activity activity) {


        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_CLOSE_SESSION
        );
        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        activity.finish();
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        activity.finish();
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                    }
                });


    }


}
