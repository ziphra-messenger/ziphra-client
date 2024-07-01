package com.privacity.cliente.activity.message;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.imagefull.ImageFullActivity;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.CallbackRestDownload;
import com.privacity.cliente.rest.restcalls.message.GetMessageById;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.enumeration.MessageState;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerMessageAdapter extends RecyclerView.Adapter<RecyclerHolder> implements StopMedia {

    public static final String SECRET_KEY_INVALIDA = "Secret Key Invalida";
    private List<ItemListMessage> items;
    private List<ItemListMessage> originalItems;
    private RecyclerItemClick itemClick;
    private MessageActivity messageActivity;

    public RecyclerMessageAdapter(MessageActivity messageActivity, List<ItemListMessage> items, RecyclerItemClick itemClick) {
        this.items = items;
        this.itemClick = itemClick;
        this.originalItems = new ArrayList<>();
        this.messageActivity = messageActivity;
        originalItems.addAll(items);
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view_message, parent, false);
        return new RecyclerHolder(view);
    }

    public static Bitmap convert(byte[] base64Str) throws IllegalArgumentException {
//        byte[] decodedBytes = Base64.decode(
//                base64Str.substring(base64Str.indexOf(",")  + 1),
//                Base64.DEFAULT
//        );

        //byte[] decodedBytes = GsonFormated.get().fromJson(base64Str, byte[].class);

        return BitmapFactory.decodeByteArray(base64Str, 0, base64Str.length);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {

        try {
            final ItemListMessage item = items.get(position);



            holder.setItemListMessage(item);





            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ( item.getRch().getLayoutSelected().getVisibility() == View.VISIBLE ){
                        Observers.message().getMessageSelected().remove(item);
                        messageActivity.getMessageCustomActionBar().changeSelectItems();
                        item.getRch().getLayoutSelected().setVisibility(View.INVISIBLE);

                        if ( Observers.message().getMessageSelected().size() == 0){
                            messageActivity.getMessageCustomActionBar().setOnActionBarMain();
                        }
                    }else if (Observers.message().getMessageSelected().size() > 0) {
                        Observers.message().getMessageSelected().add(item);
                        messageActivity.getMessageCustomActionBar().changeSelectItems();
                        item.getRch().getLayoutSelected().setVisibility(View.VISIBLE);
                    }else {


                        itemClick.itemClick(item);
                    }

                }
            });

            holder.itemView.setLongClickable(true);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if ( Observers.message().getMessageSelected().contains(item)) {
                        return false;
                    }
                    messageActivity.getMessageCustomActionBar().setOnActionBarActions();

                    //ItemListMessage item = SingletonValues.getInstance().getMessageDetailSeleccionado();
                    Observers.message().getMessageSelected().add(item);

                    messageActivity.getMessageCustomActionBar().changeSelectItems();

                    item.getRch().getLayoutSelected().setVisibility(View.VISIBLE);

                    return true;
                }
            });

            if (item.getMessage().isSystemMessage()) {

                buildSystemMessage(item, holder, holder.getRCH(MessageSenderEnum.SYSTEM_MESSAGE),false);

            } else if (!item.getMessage().isAnonimo()
                    && !MasterGeneralConfiguration.buildSiempreAnonimoReceptionConfigurationByGrupo(item.getMessage().getIdGrupo()).isValue()
                    && getIdUsuario(item.getMessage().getUsuarioCreacion()).equals(item.getMessageDetailDTO().getUsuarioDestino().getIdUsuario())) {

                buildMensaje(item, holder, holder.getRCH(MessageSenderEnum.MY_MESSAGE),false);

            } else {
                buildMensaje(item, holder, holder.getRCH(MessageSenderEnum.MESSAGE_RECEIVED),false);
            }

            if (item.getRch() != null && item.getRch().getLayoutSelected() != null) {
                if (!Observers.message().getMessageSelected().contains(item)) {
                    item.getRch().getLayoutSelected().setVisibility(View.INVISIBLE);
                } else {
                    item.getRch().getLayoutSelected().setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void buildSystemMessage(ItemListMessage item, RecyclerHolder holder, RecyclerHolderGeneric rch,boolean isReply) {
        item.setRch(rch);

        holder.getLayoutAllMessage().setVisibility(View.GONE);
        holder.getLayoutAllMessageRec().setVisibility(View.GONE);
        holder.getLayoutSystem().setVisibility(View.VISIBLE);
        holder.getTvMessageListTextSystem().setVisibility(View.VISIBLE);
        holder.getTvMessageListTextSystem().setText(item.getMessage().getText());
    }

    private void buildMensaje(ItemListMessage item, RecyclerHolder holder, RecyclerHolderGeneric rch,boolean isReply) {
        holder.initViewShow(rch, item,isReply, messageActivity);

        if (rch.getReply() != null && rch.getReply().getLayoutAllMessage() != null){
            rch.getReply().getLayoutAllMessage().setVisibility(View.GONE);
        }

        if ( item.getMessage().getParentReply() != null && item.getMessage().getParentReply().getIdMessage() != null && !isReply){
            final String idMessageToMap = item.getMessage().getParentReply().getIdMessageToMap();
            final IdMessageDTO idParentReply = item.getMessage().getParentReply();
            Message messageReply = Observers.message().getMensajesPorId(idMessageToMap);

            if (messageReply == null){

                holder.getRch().getReply().getLayoutEmptyMessage().setVisibility(View.VISIBLE);
                rch.getReply().getLayoutAllMessage().setVisibility(View.GONE);
                holder.getRch().getReply().getEmptyMessageDownload().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GetMessageById.getMessageParentReplyById(messageActivity,idParentReply
                                );
                    }
                });
            }else{
                buildMensaje(

                        new ItemListMessage(messageReply, messageReply.getMessagesDetailDTO()[0]),
                        holder,
                        holder.getRch().getReply(),true);
                rch.getReply().getLayoutAllMessage().setVisibility(View.VISIBLE);
                holder.getRch().getReply().getLayoutEmptyMessage().setVisibility(View.GONE);
            }
        }else{
            holder.getRch().getReply().getLayoutEmptyMessage().setVisibility(View.GONE);
        }


        item.setRch(rch);
        boolean grupoDeDos = false;
//        if (ObservatorGrupos.getInstance().getGrupoById((item.getMessage().getIdGrupo())).getUsersForGrupoDTO().length < 3){
//            grupoDeDos=true;
//        }

        if (grupoDeDos || item.getMessageDetailDTO().getEstado().equals(MessageState.MY_MESSAGE_SENT)
                || item.getMessageDetailDTO().getEstado().equals(MessageState.MY_MESSAGE_SENDING)
                || item.getMessageDetailDTO().getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)
        ) {

            //rch.getTvRemitente().setVisibility(View.GONE);
        }


        boolean tieneImagen;
        boolean tieneAudioMessage = false;

        if (rch.isHasMediaAudioChat()) {
            rch.getBtMessageItemlistMediaAudioVelocity().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rch.getBtMessageItemlistMediaAudioVelocity().getText().toString().equals("1x")) {
                        rch.getBtMessageItemlistMediaAudioVelocity().setText("1.5x");
                    } else if (rch.getBtMessageItemlistMediaAudioVelocity().getText().toString().equals("1.5x")) {
                        rch.getBtMessageItemlistMediaAudioVelocity().setText("2x");
                    } else if (rch.getBtMessageItemlistMediaAudioVelocity().getText().toString().equals("2x")) {
                        rch.getBtMessageItemlistMediaAudioVelocity().setText("1x");
                    }
                }
            });
            rch.getSbMessageItemlistMediaAudioProgress().setProgress(0);
            rch.getSbMessageItemlistMediaAudioProgress().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress == 100) {
                        stopMedia(rch);
                        rch.getSbMessageItemlistMediaAudioProgress().setProgress(0);
                    } else if (!item.isPlaying) {
                        if (!item.getMessage().isSecretKeyPersonal()) {
                            rch.setSecondsMediaValue(new Double(Math.floor(item.getMessage().getMediaDTO().getData().length * progress) / 100.00).intValue());
                        }

                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            if (!item.getMessage().isSecretKeyPersonal()) {
                rch.setSecondsMediaValue(item.getMessage().getMediaDTO().getData());
            }



            rch.getIbMessageItemlistMediaAudioPlay().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rch.getIbMessageItemlistMediaAudioStop().setVisibility(View.VISIBLE);
                    rch.getIbMessageItemlistMediaAudioPlay().setVisibility(View.GONE);

                    byte[] audioEncr = item.getMessage().getMediaDTO().getData();
                    byte[] audio = audioEncr;
                    try {
                        //audio = ObservatorGrupos.getInstance().getGrupoAESToUseById().get(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).getAESDecrypt(audioEncr);
                    } catch (Exception e) {
                        rch.addError("* Catch Encriptacion 1 : " + e.getMessage());
                        e.printStackTrace();
                    }
                    if (item.getMessage().isSecretKeyPersonal()) {
                        try {
                            messageActivity.extraAesToUse.getAESDecrypt(audio);
                            audio = messageActivity.extraAesToUse.getAESDecrypt(audio);

                            rch.setPersonalEncryptLockOpen(isReply);
                            rch.setSecondsMediaValue(audio);
                            play(audio, item, rch,messageActivity);

                            return;
                            //rch.getIbMessageItemlistMediaAudioPlay().setText(""+ ( decodedBytes.length / 44100) );
                        } catch (Exception e) {
                            e.printStackTrace();
                            rch.addError("* Catch Encriptacion Extra 2 : " + e.getMessage());
                            rch.setPersonalEncryptLockClose(isReply);
                        }
                    } else {

                        play(audio, item, rch,messageActivity   );
                        return;
                    }
                }
            });

            if (item.getMessage().getMediaDTO().getData() == null){
                restDownloadAudio(rch, item.getMessage());
            }else{
                rch.getIbMessageItemlistMediaAudioPlay().setVisibility(View.VISIBLE);
                rch.getPbMessageItemlistMediaAudioProgressBar().setVisibility(View.GONE);
                rch.getIbMessageItemlistMediaAudioStop().setVisibility(View.GONE);
            }
        }

        if (rch.isHasMediaImage()) {
            Bitmap bit = null;
            tieneImagen = true;
            try {

                byte[] imagenEncr = item.getMessage().getMediaDTO().getMiniatura();

                byte[] imagen = imagenEncr; //ObservatorGrupos.getInstance().getGrupoAESToUseById().get(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).getAESDecrypt(imagenEncr);

                if (item.getMessage().isSecretKeyPersonal()) {
                    try {
                        messageActivity.extraAesToUse.getAESDecrypt(imagen);
                        imagen = messageActivity.extraAesToUse.getAESDecrypt(imagen);

                        bit = convert(imagen);

                        rch.getIvItemListImageMedia().setImageBitmap(bit);

                        rch.setPersonalEncryptLockOpen(isReply);
                    } catch (Exception e) {
                        item.setOcularDetails(true);
                        bit = showCandadoErrorMedia(rch, e);
                        rch.setPersonalEncryptLockClose(isReply);
                    }
                } else {
                    bit = convert(imagen);
                    rch.getIvItemListImageMedia().setImageBitmap(bit);
                }

                if (item.getMessage().getMediaDTO().getData() == null) {
                    rch.getIvItemListImageMedia().setVisibility(View.GONE);
                    rch.getLayoutItemListImage().setBackground(rch.getIvItemListImageMedia().getDrawable());

                    if (item.getMessage().isDownloadingMedia()) {
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

                                restDownloadImage(rch, item.getMessage());
                            }
                        });
                        //restDownloadImage(rch, item.getMessage());
                    }
                } else {
                    rch.getIvItemListImageMedia().setVisibility(View.VISIBLE);
                    rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);
                    rch.getIbMessageItemlistMediaDownload().setVisibility(View.GONE);
                }

            } catch (Exception e) {
                bit = showCandadoErrorMedia(rch, e);
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
                        intent.putExtra("idMessageToMap", item.getMessage().getIdMessageToMap());
                        v.getContext().startActivity(intent);
                    }

                }
            });
        }

        if (!item.getMessage().isSystemMessage()
            && !isReply
        ) {
            if (!item.messageDetailDTO.getEstado().equals(MessageState.MY_MESSAGE_SENDING) &&
                    !item.messageDetailDTO.getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)) {

                String estado = getEstadoString(item, grupoDeDos);
                if (estado.equals("✓✓✓")) {
                    rch.getTvState().setTextColor(Color.BLUE);
                } else {
                    rch.getTvState().setTextColor(Color.BLACK);
                }
                rch.getTvState().setText(estado);
            } else {
                rch.getTvState().setTextColor(Color.BLACK);
                rch.getTvState().setText(item.messageDetailDTO.getEstado().name());
            }
        }else{
            rch.getTvState().setText("");
        }


        if (item.getMessage().isAnonimo()
               /* || MasterGeneralConfiguration.buildSiempreAnonimoReceptionConfigurationByGrupo(item.getMessage().getIdGrupo()).isValue()

                */
        ) {

            String nicknameCreador = "";
            if (item.getMessage().getUsuarioCreacion() != null) {
                if (item.getMessage().getUsuarioCreacion().getNickname() != null) {
                    nicknameCreador = item.getMessage().getUsuarioCreacion().getNickname() + " ";
                }

            }
            rch.getTvRemitente().setVisibility(View.VISIBLE);
            rch.getTvRemitente().setText("[Anonimo]");

        } else {
            // if (!messageActivity.tvMessageShowOther.getText().toString().equals("")){

            //    rch.getTvRemitente().setText(messageActivity.tvMessageShowOther.getText().toString());
            //}else{

                rch.getTvRemitente().setText(item.getMessage().getUsuarioCreacion().getNickname());



            //}

        }



        boolean secretKeyValida = true;


        String txt = "";
        if (item.getMessage().isSecretKeyPersonal()) {

            try {
                //messageActivity.secretKeyPersonal.getAESDecrypt(item.getMessage().getText());

                String txtStr = item.getMessage().getText();
                if (txtStr != null) {

                    messageActivity.extraAesToUse.getAESDecrypt(txtStr);
                    txtStr = messageActivity.extraAesToUse.getAESDecrypt(txtStr);
                    txt = txtStr;
                }else{
                    txt="";
                }

                rch.setPersonalEncryptLockOpen(isReply);
            } catch (Exception e) {
                e.printStackTrace();
                secretKeyValida = false;
                //rch.addError("* SECRET_KEY_INVALIDA 10 : " + e.getMessage());
                item.setOcularDetails(true);
                rch.setPersonalEncryptLockClose(isReply);
                //rch.getTvMessageListText().setError("Secret Key Invalida");
            }
        } else {

            try {
                txt = item.getMessage().getText();//ObservatorGrupos.getInstance().getGrupoAESToUseById().get(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).getAESDecrypt(item.getMessage().getText());
            } catch (Exception e) {
                e.printStackTrace();
                rch.addError("* Catch Encriptacion Grupo 3 : " + e.getMessage());
                txt = "";
                item.setOcularDetails(true);
            }

        }

        if (SingletonServer.getInstance().isDeveloper()) {
            txt = "Texto: " + txt
                    + "\n Nick Creador: " + ((item.getMessage().getUsuarioCreacion() == null) ? "null" : item.getMessage().getUsuarioCreacion().getNickname())
                    + "\n Id Creador: " + ((item.getMessage().getUsuarioCreacion() == null) ? "null" : item.getMessage().getUsuarioCreacion().getIdUsuario())
                    + "\n IdMessage: " + item.getMessage().getIdMessage()
                    + "\n IdGrupo: " + item.getMessage().getIdGrupo()
                    + "\n Cantidad de Usuarios Enviados: " + item.getMessage().getMessagesDetailDTO().length;

        }

        txt = ListListener.setListenerReadMoreLess(item.getMessage(), rch, txt,isReply);
        rch.getTvMessageListText().setText(txt);





        //final boolean secretKeyValidaFinal = secretKeyValida;
        if (rch.isMessageTimeActive()) {
            Message ms = (Message) item.getMessage();
            if (!item.isRunning() || ms.getCountDownTimer().isTimeMessageCountDownTimerRunning()) {


                rch.getBtActivateMessageTime().setText(MessageUtil.CalcularTiempoFormaterSinHora(ms.getCountDownTimer().getSeconds()));
                item.setCounter(new CountDownTimer(
                        ms.getCountDownTimer().getSeconds() * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        rch.getLayoutMessageContentData().setVisibility(View.VISIBLE);

                        rch.getBtActivateMessageTime().setText((MessageUtil.CalcularTiempoFormaterSinHora(ms.getCountDownTimer().getSeconds())) + "");

                        if (!rch.isHasMediaAudioChat() &&
                                (!item.getMessageDetailDTO().getEstado().equals(MessageState.MY_MESSAGE_SENT) &&
                                        !item.getMessageDetailDTO().getEstado().equals(MessageState.DESTINY_READED)
                                )
                        ) {
                            Observers.message().cambiarEstadoUso(item.getMessageDetailDTO(), true, messageActivity);
                        }


                    }

                    public void onFinish() {
                        item.isPlaying = false;
                        rch.getBtActivateMessageTime().setText("End");

                        if (!rch.isOwnMessage()) {
                            rch.getLayoutMessageContentData().setVisibility(View.GONE);
                            items.remove(item);

                        } else {
                            rch.getLayoutAllMessage().setVisibility(View.GONE);
                        }


                        RecyclerMessageAdapter.this.notifyDataSetChanged();
                        Intent intent = new Intent("finish_activity");
                        messageActivity.sendBroadcast(intent);

                    }
                });
                if (ms.getCountDownTimer().isTimeMessageCountDownTimerRunning()) {
                    item.startTimer();
                } else {
                    rch.getBtActivateMessageTime().setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!rch.isHasMediaAudioChat()) {
                                        item.startTimer();
                                        ms.getCountDownTimer().restart();
                                    } else {
                                        rch.getLayoutMessageContentData().setVisibility(View.VISIBLE);
                                    }

                                }
                            });
                }
            }


        }

        if (!isReply) {
            ListListener.setListenerLockClose(this.messageActivity, rch);
            ListListener.setListenerLockOpen(this.messageActivity, rch);
        }
        holder.endViewShow(rch, item,messageActivity,isReply);
    }

    private void restDownloadAudio(RecyclerHolderGeneric rch, Message message) {

        rch.getIbMessageItemlistMediaAudioPlay().setVisibility(View.GONE);
        rch.getPbMessageItemlistMediaAudioProgressBar().setVisibility(View.VISIBLE);
        rch.getIbMessageItemlistMediaAudioStop().setVisibility(View.GONE);

        message.setDownloadingMedia(true);

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_MEDIA_1);

        MessageDTO o = new MessageDTO();
        o.setIdGrupo(message.getIdGrupo());
        o.setIdMessage(message.getIdMessage());

        p.setMessageDTO(o);

        RestExecute.doitDownload(this.messageActivity, p,
                new CallbackRestDownload() {

                    @Override
                    public void response(ResponseEntity<byte[]> response) {
                        //MessageDTO m = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MessageDTO.class);

                        byte[] completo;
                        try {
                            completo = response.getBody(); //Observers.grupo().getGrupoById(message.getIdGrupo()).getAESToUse().getAESDecrypt(response.getBody());
                            message.getMediaDTO().setData(completo);
                            message.setDownloadingMedia(false);

                            rch.getIbMessageItemlistMediaAudioPlay().setVisibility(View.VISIBLE);
                            rch.getPbMessageItemlistMediaAudioProgressBar().setVisibility(View.GONE);
                            rch.getIbMessageItemlistMediaAudioStop().setVisibility(View.GONE);

                            rch.getLayoutItemListImage().setBackground(null);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            //loadingConsole.setText(loadingConsole.getText().toString().replaceAll( "Getting Message: " + (num-1) +"/" +  list.length+"\n",""));
                        } catch (Exception e) {
                            rch.addError("* GetData Audio 4 : " + e.getMessage());
                            message.setDownloadingMedia(false);
                            message.getMediaDTO().setData(null);
                            rch.getPbMessageItemlistMediaAudioProgressBar().setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<byte[]> response) {
                        rch.addError("* GetData Audio 5 : " + response.getStatusCode());
                        message.setDownloadingMedia(false);
                        rch.getPbMessageItemlistMediaAudioProgressBar().setVisibility(View.GONE);
                        //System.out.println("Get Message Error: " + response.getBody().getCodigoRespuesta());

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        rch.addError("* GetData Audio 6 : " + msg);
                        message.setDownloadingMedia(false);
                        rch.getPbMessageItemlistMediaAudioProgressBar().setVisibility(View.GONE);
                        System.out.println("MessagE: " + msg);

                    }

                });

    }

    private void restDownloadImage(RecyclerHolderGeneric rch, Message message) {
        message.setDownloadingMedia(true);
        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_MEDIA_2);

        MessageDTO o = new MessageDTO();
        o.setIdGrupo(message.getIdGrupo());
        o.setIdMessage(message.getIdMessage());

        p.setMessageDTO(o);

        RestExecute.doitDownload(this.messageActivity, p,
                new CallbackRestDownload() {

                    @Override
                    public void response(ResponseEntity<byte[]> response) {
                        //MessageDTO m = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MessageDTO.class);

                        byte[] completo;
                        try {
                            completo = response.getBody(); //Observers.grupo().getGrupoById(message.getIdGrupo()).getAESToUse().getAESDecrypt(response.getBody());
                            message.getMediaDTO().setData(completo);
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


    @NotNull
    private String getEstadoString(ItemListMessage item, boolean grupoDeDos) {
        int enviado = 0;
        int enviando = 0;
        int esperando = 0;
        int recibido = 0;
        int leido = 0;

        for (int i = 0; i < item.getMessage().getMessagesDetailDTO().length; i++) {
            if (item.getMessage().getMessagesDetailDTO()[i].getEstado().equals(MessageState.MY_MESSAGE_SENT)) {
                enviado++;
            } else if (item.getMessage().getMessagesDetailDTO()[i].getEstado().equals(MessageState.MY_MESSAGE_SENDING)) {
                enviando++;
            } else if (item.getMessage().getMessagesDetailDTO()[i].getEstado().equals(MessageState.DESTINY_SERVER)) {
                esperando++;
            } else if (item.getMessage().getMessagesDetailDTO()[i].getEstado().equals(MessageState.DESTINY_DELIVERED)) {
                recibido++;
            } else if (item.getMessage().getMessagesDetailDTO()[i].getEstado().equals(MessageState.DESTINY_READED)) {
                leido++;
            }
        }

        String estado;
        if ( esperando == 0 && recibido == 0 ){
            //estado = EstadoName.getNameToShow(item.getMessageDetailDTO().getEstado()) + " ✓✓✓";
            estado = "✓✓✓";

        }else{


            if ( esperando == 0 && recibido == 0 && leido==0 ){
                estado = EstadoName.getNameToShow(item.getMessageDetailDTO().getEstado().name());
            }else{
                if (grupoDeDos){
                    String esperandoString = (esperando>0) ?"✓":"";
                    String recibidoString = (recibido>0) ?"✓✓":"";
                    String leidoString = (leido>0) ?"✓✓✓":"";
                    //estado = EstadoName.getNameToShow(item.getMessageDetailDTO().getEstado()) + " Estado:"+ esperandoString + recibidoString + leidoString;
                    estado = esperandoString + recibidoString + leidoString;

                }else{
                    String esperandoString = (esperando>0) ?" (" + esperando + " ✓)":"";
                    String recibidoString = (recibido>0) ?" (" + recibido + " ✓✓)":"";
                    String leidoString = (leido>0) ?" (" + leido + " ✓✓✓)":"";
                    //estado = EstadoName.getNameToShow(item.getMessageDetailDTO().getEstado()) + " Estado:"+ esperandoString + recibidoString + leidoString;
                    estado = esperandoString + recibidoString + leidoString;

                }
            }

        }
        if (estado== null) estado = " ";
        return estado;
    }

    private Bitmap showCandadoErrorMedia(RecyclerHolderGeneric rch, Exception e) {
        Bitmap bit;
        
        rch.getTvMessageListText().setText("Secret Key Invalida " + e.getMessage());
        //rch.getTvMessageListText().setError("Secret Key Invalida");
        e.printStackTrace();

        bit = BitmapFactory.decodeResource(messageActivity.getResources(), R.drawable.candadocerrado);
        //bit.setWidth(50);

        rch.getIvItemListImageMedia().setImageBitmap(bit);
        return bit;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void filter(final String strSearch) {
        if (strSearch.length() == 0) {
            items.clear();
            items.addAll(originalItems);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                items.clear();
                List<ItemListMessage> collect = originalItems.stream()
                        .filter(i -> i.getMessage().getText().toLowerCase().contains(strSearch))
                        .collect(Collectors.toList());

                items.addAll(collect);
            }
            else {
                items.clear();
                for (ItemListMessage i : originalItems) {
                    if (i.getMessage().getText().toLowerCase().contains(strSearch)) {
                        items.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }



    public interface RecyclerItemClick {
        void itemClick(ItemListMessage item);
    }
    
    public String getIdUsuario(UsuarioDTO u){
        if (u == null) return null;
        
        return u.getIdUsuario();
    }

    public static final int SAMPLE_RATE = 44100; // supported on all devices
    public static final int CHANNEL_CONFIG_IN = AudioFormat.CHANNEL_IN_MONO;
    public static final int CHANNEL_CONFIG_OUT = AudioFormat.CHANNEL_OUT_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_8BIT; // not supported on all devices
    public static final int BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_IN, AUDIO_FORMAT);
    public static final int BUFFER_SIZE_PLAYING = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_OUT, AUDIO_FORMAT);
    boolean isRecordingAudio=false;
    AudioRecord audioRecord=null;
    File fileAudio=null;
    private Thread recordingThread;
    String fileNameAudio;

    String TAG="DD";


    private static void play(byte[] audiolist, ItemListMessage item, RecyclerHolderGeneric rch, MessageActivity messageActivity){
        Observers.message().cambiarEstadoUso(item.getMessageDetailDTO(), true,messageActivity);

        if (rch.isMessageTimeActive()){
            item.startTimer();
        }
        rch.getSbMessageItemlistMediaAudioProgress().getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

//        MediaPlayer mediaPlayer = MediaPlayer.create(getApplication(), ((Button) findViewById(R.id.play)).getId());

//       MediaPlayer mediaPlayer = MediaPlayer.create(getApplication(), ((Button) findViewById(R.id.play)).getId());

        if ( !item.isPlaying){
            try {
                AudioTrack audioTrack = new AudioTrack(
                        new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).setUsage(AudioAttributes.USAGE_MEDIA).build(),
                        new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                .setSampleRate(SAMPLE_RATE)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        BUFFER_SIZE_PLAYING,
                        AudioTrack.MODE_STREAM,
                        AudioManager.AUDIO_SESSION_ID_GENERATE
                );

//                EnvironmentalReverb eReverb = new EnvironmentalReverb(1, audioTrack.getAudioSessionId());
//                eReverb.setDecayHFRatio((short) 1000);
//                eReverb.setDecayTime(10000);
//                eReverb.setDensity((short) 1000);
//                eReverb.setDiffusion((short) 1000);
//                eReverb.setReverbLevel((short) -1000);
//                eReverb.setEnabled(true);
//                audioTrack.attachAuxEffect(eReverb.getId());
//                audioTrack.setAuxEffectSendLevel(1.0f);

                if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
                    //Toast.makeText(this, "Couldn't initialize AudioTrack, check configuration", Toast.LENGTH_SHORT).show();
                    //Log.e(TAG, "error initializing AudioTrack");
                    return;
                }

                audioTrack.play();

                //Log.d(TAG, "playback started with AudioTrack");

                item.setPlaying(true);

                Thread playingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readAudioDataFromFile(audioTrack,audiolist,item,rch);
                    }
                });
                playingThread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    public void stopMedia(RecyclerHolderGeneric rch){
        //rch.getSbMessageItemlistMediaAudioProgress().setProgress(0);
        rch.getIbMessageItemlistMediaAudioStop().setVisibility(View.GONE);
        rch.getIbMessageItemlistMediaAudioPlay().setVisibility(View.VISIBLE);
    }

    private static void readAudioDataFromFile(AudioTrack audioTrack, byte[] audioList, ItemListMessage item, RecyclerHolderGeneric rch) { // called inside Runnable of playingThread


        try {


            int colorFlagCounter = 0;
            int i = 0;
            int k = 0;

            if (rch.getSbMessageItemlistMediaAudioProgress().getProgress() != 0) {
                k = new Double(Math.floor(rch.getSbMessageItemlistMediaAudioProgress().getProgress() / 100.00 * audioList.length)).intValue();
            }
            while (item.isPlaying() && (i != -1)) { // continue until run out of data or user stops playback
                byte[] data2 = new byte[BUFFER_SIZE_PLAYING / 2];

                int j = 0;
                for (; j < data2.length && k < audioList.length; j++) {
                    data2[j] = ((byte) audioList[k]);
                    k++;
                }

                if (k >= audioList.length) {

                    i = -1;
                }
                colorFlagCounter++;
                if (colorFlagCounter == 18) {
                    int porcentaje = k * 100 / audioList.length;
                    rch.getSbMessageItemlistMediaAudioProgress().setProgress(porcentaje);
                    colorFlagCounter = 0;
                    MessageUtil.segundosCalculados(rch.getTvMessageItemlistMediaAudioSeconds(), k);
                }

                try {
                    audioTrack.write(data2, 0, j);
                } catch (Exception e) {
                    i = -1;
                    e.printStackTrace();
                }


            }

            // clean up resources

            audioTrack.stop();
            audioTrack.release();
            if (rch.getSbMessageItemlistMediaAudioProgress().getProgress() != 100) {
                rch.getSbMessageItemlistMediaAudioProgress().setProgress(100);
            }
            item.setPlaying(false);
            //rch.getSbMessageItemlistMediaAudioProgress().setProgress(100);
            System.out.println("ok audio");
        } catch (Exception e){
            item.setPlaying(false);
            System.out.println("error audio> " + e.getMessage());
                e.printStackTrace();
            }
    }




}
