package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class GrupoById {
    public void getGrupoById(Activity activity, GrupoDTO g) {

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_GET_GRUPO_BY_ID);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(g));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        Grupo l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), Grupo.class);


                            Observers.grupo().addGrupo(l, false);
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        //activity.addTextConsole("Group beforeShowErrorMessage: " + msg);
                    }

                });


    }
}
