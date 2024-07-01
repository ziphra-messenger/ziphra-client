package com.privacity.cliente.activity.message.delegate;

import android.app.Activity;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.IdDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

public class BloqueoRemotoDelegate {

    public void ejecutarGrupoBloqueoRemoto(Activity activity) {


        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_GRUPO);
        p.setAction(ProtocoloActionsEnum.PROTOCOLO_ACTION_GRUPO_BLOCK_REMOTO);

        GrupoDTO o = new GrupoDTO();

        o.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        p.setObjectDTO(GsonFormated.get().toJson(o));

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
