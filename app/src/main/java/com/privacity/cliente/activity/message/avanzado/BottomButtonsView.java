package com.privacity.cliente.activity.message.avanzado;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class BottomButtonsView {
    private Button saveclose;
    private Button save;
    private Button close;


    private MessageAvanzado messageAvanzado;

    public BottomButtonsView(MessageAvanzado messageAvanzado) {
        this.messageAvanzado = messageAvanzado;

        initView();
        initListeners();

    }

    private void initView() {

        saveclose =  GetButtonReady.get(SingletonCurrentActivity.getInstance().getMessageActivity(), R.id.bt_message_avanzado_save_close, getSave(true));
        close =  GetButtonReady.get(SingletonCurrentActivity.getInstance().getMessageActivity(), R.id.bt_message_avanzado_clse);
        save =  GetButtonReady.get(SingletonCurrentActivity.getInstance().getMessageActivity(), R.id.bt_message_avanzado_save, getSave(false));
/*
        saveclose = (Button) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.bt_message_avanzado_save_close);
        close = (Button) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.bt_message_avanzado_clse);
        save = (Button) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.bt_message_avanzado_save);
*/


    }

    private void initListeners() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SingletonCurrentActivity.getInstance().getMessageActivity().messageAvanzadoClose();

            }
        });


    }

    private ViewCallbackActionInterface getSave(boolean closep) {
        return new ViewCallbackActionInterface() {

            final boolean closeA=closep;
            @Override
            public void action(View v) {

                SimpleErrorDialog.passwordValidation(SingletonCurrentActivity.getInstance().getMessageActivity(), new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        Protocolo p = new Protocolo();
                        p.setComponent(ProtocoloComponentsEnum.GRUPO);
                        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_GRUPO_USER_CONF);

                        GrupoUserConfDTO c =messageAvanzado.getGrupoUserConfDTO();


                        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(c));

                        SingletonValues.getInstance().getGrupoSeleccionado().setUserConfDTO(c);
                        RestExecute.doit(SingletonCurrentActivity.getInstance().getMessageActivity(), p,
                                new CallbackRest() {

                                    @Override
                                    public void response(ResponseEntity<Protocolo> response) {

//                                        if (etMessageAvanzadoNicknameForGrupo.getText().equals("")){
//                                            ObservatorGrupos.getInstance().getGrupoById(c.getIdGrupo()).setNicknameForGrupo(null);
//                                        }else{
//                                            ObservatorGrupos.getInstance().getGrupoById(c.getIdGrupo()).setNicknameForGrupo(c.getNicknameForGrupo());
//                                        }
                                        Toast.makeText(SingletonCurrentActivity.getInstance().getMessageActivity(), "Guardada", Toast.LENGTH_SHORT).show();
                                        if (closeA)SingletonCurrentActivity.getInstance().getMessageActivity().messageAvanzadoClose();
                                        //SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().setBlackMessageAttachMandatory(blackMessageAttachMandatoryGrupoUserConf.isChecked());

                                    }

                                    @Override
                                    public void onError(ResponseEntity<Protocolo> response) {

                                    }

                                    @Override
                                    public void beforeShowErrorMessage(String msg) {

                                    }
                                });


                    }


                });

            }
        };
    }
}

