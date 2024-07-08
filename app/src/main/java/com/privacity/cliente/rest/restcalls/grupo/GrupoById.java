package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.IdDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

import java.util.List;

public class GrupoById {
    public void getGrupoById(Activity activity, IdDTO g) {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_GET_GRUPO_BY_ID);

        p.setObjectDTO(GsonFormated.get().toJson(g));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        Grupo l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), Grupo.class);


                            Observers.grupo().addGrupo(l, false);
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        //activity.addTextConsole("Group beforeShowErrorMessage: " + msg);
                    }

                });


    }
}
