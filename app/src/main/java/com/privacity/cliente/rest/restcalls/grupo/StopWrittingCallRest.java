package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.WrittingDTO;

import org.springframework.http.ResponseEntity;

public class StopWrittingCallRest {


    public static void call(Activity activity, WrittingDTO dto) throws Exception {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_GRUPO);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_STOP_WRITTING);

        p.setObjectDTO(GsonFormated.get().toJson(dto));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                    }
                });

    }

}
