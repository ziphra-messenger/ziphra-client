package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;
import android.content.Intent;
import android.widget.ProgressBar;

import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class PasswordGrupoValidationCallRest {


    public static void call(Activity activity, ProgressBar progressBar, Grupo grupo, GrupoDTO dto) throws Exception {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_LOGIN);

        p.setObjectDTO(GsonFormated.get().toJson(dto));

        ProgressBarUtil.show(activity, progressBar);

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        boolean l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), boolean.class);

                        ProgressBarUtil.hide(activity, progressBar);
                        if (l){
                            grupo.reintentosPasswordReset();


                            if (grupo.getPassword().isExtraEncryptDefaultEnabled() ){
                                grupo.getPassword().setPasswordExtraEncrypt(dto.getPassword().getPassword());
                            }

                            Observers.passwordGrupo().passwordSet(grupo);
                            ObserverGrupo.getInstance().getGrupoById(grupo.getIdGrupo()).setGrupoLocked(false)
                            ;
                            SingletonValues.getInstance().setGrupoSeleccionado(grupo);
                            Intent intent = new Intent(activity, MessageActivity.class);
                            activity.startActivity(intent);
                        }else{
                            grupo.reintentosPasswordSumar();
                            SimpleErrorDialog.errorDialog(activity,"Error", "Password Incorrecto");
                        }


                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                        ProgressBarUtil.hide(activity, progressBar);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                        ProgressBarUtil.hide(activity, progressBar);
                    }
                });

    }

}
