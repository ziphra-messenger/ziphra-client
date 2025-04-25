package com.privacity.cliente.rest.restcalls.message;

import android.app.Activity;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class GetMessageHistorialById {
    private static final String TAG = "GetMessageHistorialById";
    public static  void loadMessagesContador(Activity activity, String idGrupo, String idMessage) {

//        tvLoadingGetNewMessages.setVisibility(View.VISIBLE);
//        tvLoadingGetNewMessagesCount.setVisibility(View.VISIBLE);

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_ID_HISTORIAL
        );

        p.setObjectDTO( UtilsStringSingleton.getInstance().gsonToSend(new IdMessageDTO(idGrupo, idMessage)));

        RestExecute.doit(activity, p,
 new CallbackRest(){

     @Override
     public void response(ResponseEntity<Protocolo> response) {
         MessageDTO[] l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MessageDTO[].class);

         //tvLoadingGetNewMessagesCount.setText("Obteniendo" + contadorMensajes + " de " + l.length);
         //messageTotal=l.length;
         Thread t = new Thread() {
             public void run() {
  loadMessages(activity, l);
             }


         };
         t.start();
/*         Intent i = new Intent(LoadingActivity.this, GrupoActivity.class);
         startActivity(i);*/
     }

     @Override
     public void onError(ResponseEntity<Protocolo> response) {
         SimpleErrorDialog.errorDialog( activity, activity.getString(R.string.general__error_message_ph1, TAG) , response.getBody().getCodigoRespuesta() );

     }

     @Override
     public void beforeShowErrorMessage(String msg) {

     }

 });



    }

    private static void loadMessages(Activity activity, MessageDTO[] list) {
        // init valores
        MessageDTO[] listNotNull = list;
        if ( list == null){
            listNotNull = new MessageDTO[0];
        }

        for ( int i = 0 ; i < listNotNull.length ; i++){
            Protocolo p = new Protocolo();
            p.setComponent(ProtocoloComponentsEnum.MESSAGE);
            p.setAction(ProtocoloActionsEnum.MESSAGE_GET_MESSAGE);
            final int num = i+1;
            MessageDTO o = new MessageDTO();
            o.setIdGrupo(list[i].getIdGrupo());
            o.setIdMessage(list[i].getIdMessage());

            p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

            RestExecute.doit(activity, p,
     new CallbackRest(){

         @Override
         public void response(ResponseEntity<Protocolo> response) {
             //MessageDTO m = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MessageDTO.class);
             response.getBody().getMessage().setHistorial(true);
             Observers.message().mensajeNuevoWS(response.getBody(),true, activity);
//             try {
//  Thread.sleep(1000);
//             } catch (InterruptedException e) {
//  e.printStackTrace();
//             }
             //loadingConsole.setText(loadingConsole.getText().toString().replaceAll( "Getting Message: " + (num-1) +"/" +  list.length+"\n",""));

             System.out.println("Getting Message: " + num +"/" +  list.length);




         }

         @Override
         public void onError(ResponseEntity<Protocolo> response) {


             System.out.println("Get Message Error: " + response.getBody().getCodigoRespuesta() );

         }

         @Override
         public void beforeShowErrorMessage(String msg) {

             System.out.println("Message: " + msg);

         }

     });

        }

    }

}
