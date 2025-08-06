package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.WrittingDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class StopWrittingCallRest {


    public static void call(Activity activity, WrittingDTO dto) throws Exception {

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_STOP_WRITTING);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                    }
                });

    }

}
