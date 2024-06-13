package com.privacity.cliente.rest.restcalls.message;

import android.app.Activity;

import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

public class GetMessageHistorialById {

    public static  void loadMessagesContador(Activity activity, String idGrupo, String idMessage) {

//        tvLoadingGetNewMessages.setVisibility(View.VISIBLE);
//        tvLoadingGetNewMessagesCount.setVisibility(View.VISIBLE);

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MESSAGE);
        p.setAction("/message/get/id/historial"
        );

        p.setObjectDTO( GsonFormated.get().toJson(new IdMessageDTO(idGrupo, idMessage)));

        RestExecute.doit(activity, p,
 new CallbackRest(){

     @Override
     public void response(ResponseEntity<ProtocoloDTO> response) {
         MessageDTO[] l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MessageDTO[].class);

            ;
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
     public void onError(ResponseEntity<ProtocoloDTO> response) {
         SimpleErrorDialog.errorDialog( activity, "Messages Error: " , response.getBody().getCodigoRespuesta() );

     }

     @Override
     public void beforeShowErrorMessage(String msg) {

     }

 });



    }

    private static void loadMessages(Activity activity, MessageDTO[] list) {
        // init valores


        for ( int i = 0 ; i < list.length ; i++){
            ProtocoloDTO p = new ProtocoloDTO();
            p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MESSAGE);
            p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MESSAGE_GET_MESSAGE);
            final int num = i+1;
            MessageDTO o = new MessageDTO();
            o.setIdGrupo(list[i].getIdGrupo());
            o.setIdMessage(list[i].getIdMessage());

            p.setObjectDTO(GsonFormated.get().toJson(o));

            RestExecute.doit(activity, p,
     new CallbackRest(){

         @Override
         public void response(ResponseEntity<ProtocoloDTO> response) {
             //MessageDTO m = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MessageDTO.class);

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
         public void onError(ResponseEntity<ProtocoloDTO> response) {


             System.out.println("Get Message Error: " + response.getBody().getCodigoRespuesta() );

         }

         @Override
         public void beforeShowErrorMessage(String msg) {

             System.out.println("MessagE: " + msg);

         }

     });

        }

    }

}
