package com.privacity.cliente.activity.message.validations;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.imagefull.ImageFullActivity;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.CallbackRestDownload;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;

public class MessageImage {
    private static final String TAG = "MessageImage";
    public static void processMessageImage(ItemListMessage item, RecyclerHolderGeneric rch, boolean isReply, MessageActivity messageActivity) {
        boolean tieneImagen;
        if (rch.isHasMediaImage()) {
            hasImage(item, rch, isReply, messageActivity);
        }else{
            rch.getStateIcons().isBlockDownload().setVisibility(View.GONE);
        }
    }

    private static void hasImage(ItemListMessage item, RecyclerHolderGeneric rch, boolean isReply, MessageActivity messageActivity) {
        boolean tieneImagen;
        if (item.getMessage().getMedia().isDownloadable()){
            rch.getStateIcons().isBlockDownload().setVisibility(View.GONE);
        }else{
            rch.getStateIcons().isBlockDownload().setVisibility(View.VISIBLE);
        }


        Bitmap bit = null;
        tieneImagen = true;
        try {

            byte[] imagenEncr = item.getMessage().getMedia().getMiniatura();

            byte[] imagen = imagenEncr; //ObservatorGrupos.getInstance().getGrupoAESToUseById().get(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).getAESDecrypt(imagenEncr);

            if (item.getMessage().isSecretKeyPersonal()) {
                try {
                    Log.d(TAG, "Convertir imagen CON extra aes");
                    messageActivity.extraAesToUse.getAESDecryptData(imagen);
                    imagen = messageActivity.extraAesToUse.getAESDecryptData(imagen);

                    bit = convert(imagen);

                    rch.getIvItemListImageMedia().setImageBitmap(bit);

                    rch.setPersonalEncryptLockOpen(isReply);
                } catch (Exception e) {
                    item.setOcularDetails(true);
                    bit = showCandadoErrorMedia(messageActivity, rch, e);
                    rch.setPersonalEncryptLockClose(isReply);
                }
            } else {
                Log.d(TAG, "Convertir imagen sin extra aes");
                bit = convert(imagen);
                rch.getIvItemListImageMedia().setImageBitmap(bit);
            }

            if (item.getMessage().getMedia().getData() == null) {
                rch.getIvItemListImageMedia().setVisibility(View.GONE);
                rch.getLayoutItemListImage().setBackground(rch.getIvItemListImageMedia().getDrawable());

                if (item.getMessage().canDownloadMyMedia()) {
                    rch.getIbMessageItemlistMediaDownload().setVisibility(View.GONE);
                    rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.VISIBLE);
                } else {
                    rch.getIbMessageItemlistMediaDownload().setVisibility(View.VISIBLE);
                    rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);

                    rch.getIbMessageItemlistMediaDownload().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rch.getIbMessageItemlistMediaDownload().setVisibility(View.GONE);
                            rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.VISIBLE);

                            restDownloadImage(messageActivity, rch, item.getMessage());
                        }
                    });
                    //restDownloadImage(rch, item.getMessage());
                }
            } else {
                Log.d(TAG, "Mostrando miniatura");
                rch.getIvItemListImageMedia().setVisibility(View.VISIBLE);
                rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);
                rch.getIbMessageItemlistMediaDownload().setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error a convertir imagen showCandadoErrorMedia", e);
            bit = showCandadoErrorMedia(messageActivity, rch, e);
        }
        final Bitmap bit2 = bit;
        //((ImageView)findViewById(R.id.imagen)).setImageBitmap(bitmap);
        rch.getIvItemListImageMedia().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Bundle b = new Bundle();
                //b.putParcelable("bitmap", bit);

                if (!item.isOcularDetails()) {
                    SingletonValues.getInstance().setImagenFull(bit2);
                    Intent intent = new Intent(v.getContext(), ImageFullActivity.class);
                    intent.putExtra("idMessageToMap", item.getMessage().buildIdMessageToMap());
                    v.getContext().startActivity(intent);
                }

            }
        });
    }

    private static Bitmap showCandadoErrorMedia(MessageActivity activity,  RecyclerHolderGeneric rch, Exception e) {
        Bitmap bit;

        rch.getTvMessageListText().setText("Secret Key Invalida " + e.getMessage());
        //rch.getTvMessageListText().setError("Secret Key Invalida");
        e.printStackTrace();

        bit = BitmapFactory.decodeResource(activity.getResources(), R.drawable.candadocerrado);
        //bit.setWidth(50);

        rch.getIvItemListImageMedia().setImageBitmap(bit);
        return bit;
    }

    private static Bitmap convert(byte[] base64Str) throws IllegalArgumentException {
        Log.d(TAG, "convert imagen: " + Arrays.toString(base64Str));
//        byte[] decodedBytes = Base64.decode(
//                base64Str.substring(base64Str.indexOf(",")  + 1),
//                Base64.DEFAULT
//        );

        //byte[] decodedBytes = UtilsStringSingleton.getInstance().gson().fromJson(base64Str, byte[].class);

        return BitmapFactory.decodeByteArray(base64Str, 0, base64Str.length);
    }

    private static void restDownloadImage(Activity activity, RecyclerHolderGeneric rch, Message message) {
        message.setDownloadingMedia(true);
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_MEDIA_2);

        Message o = new Message();
        o.setIdGrupo(message.getIdGrupo());
        o.setIdMessage(message.getIdMessage());

        p.setMessage(o);

        RestExecute.doitDownload(activity, p,
                new CallbackRestDownload() {

                    @Override
                    public void response(ResponseEntity<byte[]> response) {
                        //MessageDTO m = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MessageDTO.class);

                        byte[] completo;
                        try {
                            completo = response.getBody(); //Observers.grupo().getGrupoById(message.getIdGrupo()).getAESToUse().getAESDecrypt(response.getBody());
                            message.getMedia().setData(completo);
                            message.setDownloadingMedia(false);
                            rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);
                            rch.getIvItemListImageMedia().setVisibility(View.VISIBLE);
                            rch.getLayoutItemListImage().setBackground(null);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            //loadingConsole.setText(loadingConsole.getText().toString().replaceAll( "Getting Message: " + (num-1) +"/" +  list.length+"\n",""));
                        } catch (Exception e) {
                            rch.addError("* GetData Imagen 7 : " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<byte[]> response) {
                        rch.addError("* GetData Imagen 8 : " + response.getStatusCode());
                        message.setDownloadingMedia(false);

                        //System.out.println("Get Message Error: " + response.getBody().getCodigoRespuesta());

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        message.setDownloadingMedia(false);
                        rch.addError("* GetData Imagen 9 : " + msg);
                        System.out.println("MessagE: " + msg);
                        rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);

                    }

                });

    }


}
