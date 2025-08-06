package com.privacity.cliente.rest.restcalls.message;

import android.app.Activity;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.observers.ObserverConnection;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class MessageChangeState {
    public static void mensajeChangeState(MessageDetail mdDto, Activity context) {

        MessageDetail md = (MessageDetail)mdDto;

        if ((SingletonValues.getInstance().getMyAccountConfDTO().isHideMyMessageState())
                || ObserverGrupo.getInstance().getGrupoById(md.getIdGrupo()).getGralConfDTO().isHideMessageReadState()){
            md.setHideRead(true);
        }

        md.setSendChangeState(true);



        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_CHANGE_STATE);
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(md));


        Observers.message().mensajeDetailChangeState(p);
        if (!ObserverConnection.getInstance().isOnline()){
            return;
        }

        RestExecute.doit(context,
                p, new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        //MessageDetail l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MessageDetail.class);
                        md.setSendChangeState(false);
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        md.setSendChangeState(false);
                        SimpleErrorDialog.errorDialog( context, context.getString(R.string.general__error_message) , response.getBody().getCodigoRespuesta() );

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        SimpleErrorDialog.errorDialog( context, context.getString(R.string.general__error_message) , msg );

                    }

                });


    }
}
