package com.privacity.cliente.rest.restcalls.message;

import android.app.Activity;
import android.util.Log;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class GetMessageById {
    private static final String TAG = "GetMessageById";


    public static  void loadMessagesContador(Activity activity) {

//        tvLoadingGetNewMessages.setVisibility(View.VISIBLE);
//        tvLoadingGetNewMessagesCount.setVisibility(View.VISIBLE);

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_ALL_ID_MESSAGE_UNREAD
        );
        RestExecute.doit(activity, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        if (SingletonSessionClosing.getInstance().isClosing())return;
                        if (response.getBody().getCodigoRespuesta() == null && response.getBody().getObjectDTO() != null) {
                            MessageDTO[] l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MessageDTO[].class);
                                Log.i(TAG,"Obteniendo " + l.length + " id de mensajes pendientes de lectura");
                                //messageTotal=l.length;
                                Thread t = new Thread() {
                                    public void run() {
                                        loadMessages(activity, l);
                                        loadMessages(activity, Singletons.observerMessage().getTodosLosIdMensajes());

                                    }


                                };
                            t.start();
/*                        Intent i = new Intent(LoadingActivity.this, GrupoActivity.class);
                        startActivity(i);*/
                        }else {

                            Log.i(TAG, "No hay mensajes pendientes de lectura");
                            Thread t = new Thread() {
                                public void run() {
                                                          loadMessages(activity, Singletons.observerMessage().getTodosLosIdMensajes());

                                }


                            };
                            t.start();
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        SimpleErrorDialog.errorDialog( activity, activity.getString(R.string.general__error_message_ph1,TAG) , response.getBody().getCodigoRespuesta() );

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                });



    }

    private static void loadMessages(Activity activity, MessageDTO[] list) {
        // init valores


        if (SingletonSessionClosing.getInstance().isClosing())return;
        for ( int i = 0 ; i < list.length ; i++){
            if (SingletonSessionClosing.getInstance().isClosing())return;
            Log.i(TAG, "buscando mensajes: " + i + " de " + list.length);
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
                            if (SingletonSessionClosing.getInstance().isClosing())return;
                            Observers.message().mensajeNuevoWS(response.getBody(),true, activity);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            //loadingConsole.setText(loadingConsole.getText().toString().replaceAll( "Getting Message: " + (num-1) +"/" +  list.length+"\n",""));

                            System.out.println("Getting Message: " + num +"/" +  list.length);




                        }

                        @Override
                        public void onError(ResponseEntity<Protocolo> response) {


                            System.out.println("Get Message Error: " + response.getBody().getCodigoRespuesta() );

                        }

                        @Override
                        public void beforeShowErrorMessage(String msg) {

                            System.out.println("MessagE: " + msg);

                        }

                    });

        }

    }

    public static void getMessageParentReplyById(Activity activity, IdMessageDTO id) {
        // init valores



            Protocolo p = new Protocolo();
            p.setComponent(ProtocoloComponentsEnum.MESSAGE);
            p.setAction(ProtocoloActionsEnum.MESSAGE_GET_MESSAGE);

            MessageDTO o = new MessageDTO();
            o.setIdGrupo(id.getIdGrupo());
            o.setIdMessage(id.getIdMessage());

            p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

            RestExecute.doit(activity, p,
                    new CallbackRest(){

                        @Override
                        public void response(ResponseEntity<Protocolo> response) {
                            //MessageDTO m = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MessageDTO.class);
                            Observers.message().mensajeNuevoWSReply(response.getBody(),true, activity);
                            //Observers.message().mensajeNuevoWS(response.getBody(),true, activity);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            //loadingConsole.setText(loadingConsole.getText().toString().replaceAll( "Getting Message: " + (num-1) +"/" +  list.length+"\n",""));

        //                    System.out.println("Getting Message: " + num +"/" +  list.length);




                        }

                        @Override
                        public void onError(ResponseEntity<Protocolo> response) {


                            System.out.println("Get Message Error: " + response.getBody().getCodigoRespuesta() );

                        }

                        @Override
                        public void beforeShowErrorMessage(String msg) {

                            System.out.println("MessagE: " + msg);

                        }

                    });



    }

    public static void  get(Activity activity, String idGrupo, String idMessage) {
        if (SingletonSessionClosing.getInstance().isClosing())return;
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_MESSAGE);

        MessageDTO o = new MessageDTO();
        o.setIdGrupo(idGrupo);
        o.setIdMessage(idMessage);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        if (SingletonSessionClosing.getInstance().isClosing())return;
                        Observers.message().mensajeNuevoWS(response.getBody(), true, activity);

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
