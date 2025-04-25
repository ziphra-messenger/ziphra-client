package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.WrittingDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class WrittingCallRest {

    public static void callRestWritting(Activity activity , Grupo grupo) throws Exception {
        WrittingDTO dto = new WrittingDTO();
        dto.setNickname(Singletons.usuario().getUsuario().getNickname());
        dto.setIdGrupo(grupo.getIdGrupo());
        WrittingCallRest.call(activity, dto);


    }
    public static void call(Activity activity, WrittingDTO dto) throws Exception {

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_WRITTING);

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
