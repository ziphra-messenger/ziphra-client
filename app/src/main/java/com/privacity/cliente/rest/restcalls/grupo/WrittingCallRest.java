package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.WrittingDTO;

import org.springframework.http.ResponseEntity;

public class WrittingCallRest {

    public static void callRestWritting(Activity activity , Grupo grupo) throws Exception {
        WrittingDTO dto = new WrittingDTO();
        dto.setNickname(SingletonValues.getInstance().getUsuario().getNickname());
        dto.setIdGrupo(grupo.getIdGrupo());
        WrittingCallRest.call(activity, dto);


    }
    public static void call(Activity activity, WrittingDTO dto) throws Exception {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_GRUPO);
        p.setAction(ProtocoloActionsEnum.PROTOCOLO_ACTION_GRUPO_WRITTING);

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
