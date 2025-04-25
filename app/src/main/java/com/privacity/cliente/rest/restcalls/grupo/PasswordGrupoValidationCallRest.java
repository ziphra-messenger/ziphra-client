package com.privacity.cliente.rest.restcalls.grupo;

import android.app.Activity;
import android.content.Intent;
import android.widget.ProgressBar;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class PasswordGrupoValidationCallRest {


    public static void call(Activity activity, ProgressBar progressBar, Grupo grupo, GrupoDTO dto) throws Exception {

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_LOGIN);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));

        ProgressBarUtil.show(activity, progressBar);

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        boolean l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), boolean.class);

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
                            SimpleErrorDialog.errorDialog(activity,activity.getString(R.string.general__error_message), activity.getString(R.string.general__alert__validation__password_incorrect));
                        }


                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                        ProgressBarUtil.hide(activity, progressBar);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                        ProgressBarUtil.hide(activity, progressBar);
                    }
                });

    }

}
