package com.privacity.cliente.rest.restcalls.grupo;

import android.view.View;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.addgrupo.AddGrupoActivity;
import com.privacity.cliente.activity.addgrupo.AddGrupoView;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AESEncrypt;
import com.privacity.cliente.encrypt.AESFactory;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.GrupoNewRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class AddGrupo {

    public static void addGrupo(AddGrupoActivity activity, AddGrupoView v){
        //GrupoNewRequestDTO
        GrupoNewRequestDTO request = new GrupoNewRequestDTO();

        GrupoDTO newGrupo = new GrupoDTO(v.getTvAddGrupoName().getText().toString());
        Protocolo p = new Protocolo();
        try {
            AESDTO aesdtoEncript = AESEncrypt.messaging(AESFactory.getAESMessaging());

            request.setAesDTO(aesdtoEncript);

            request.setGrupoDTO(newGrupo);

            p.setComponent(ProtocoloComponentsEnum.GRUPO);
            p.setAction(ProtocoloActionsEnum.GRUPO_NEW_GRUPO);

            p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(request));
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.addgrupo_activity__alert__error_encrypt_title) , e.getMessage());
            return;
        }





        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        GrupoDTO l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), GrupoDTO.class);
                        Observers.grupo().addGrupo(response.getBody());
                        SingletonValues.getInstance().setGrupoSeleccionado(new Grupo(l));


//                                ObservatorGrupos.getInstance().getGrupoUserConfPorId().put(
//                                        SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),
//                                        MessageUtil.getDefaultGrupoUserConf(
//                                                SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),
//                                                Singletons.usuario().getUsuario().getIdUsuario()
//                                        )
//                                );

                        //Toast.makeText(getApplicationContext(), "Grupo " + l.getName() + " Agregado", Toast.LENGTH_SHORT).show();
                        //finish();
                        v.getLayoutGrupoCreado().setVisibility(View.VISIBLE);
                        v.getTvMensajeGrupoCreado().setVisibility(View.VISIBLE);
                        v.getBtnAddGrupoOk().setVisibility(View.GONE);
                        v.getTvAddGrupoName().setEnabled(false);
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
