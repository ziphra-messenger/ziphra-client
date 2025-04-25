package com.privacity.cliente.activity.messageresend;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.ConfType;
import com.privacity.cliente.common.component.selectview.SelectView;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.toast.SingletonToastManager;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MediaDTO;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.enumeration.RulesConfEnum;
import com.privacity.common.enumeration.MediaTypeEnum;
import com.privacity.common.enumeration.MessageState;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.util.List;

public class MessageUtil {

    private static final String CONSTANT__STRING_FORMAT__TIME = "%1$2s";

    public static GrupoUserConfDTO getDefaultGrupoUserConf(String idGrupo, String idUsuario) {
        GrupoUserConfDTO conf = new GrupoUserConfDTO();

        conf.setIdUsuario(idGrupo );
        conf.setIdUsuario( idUsuario);

        conf.setExtraAesAlways(RulesConfEnum.OFF);
        conf.setBlackMessageAttachMandatory(RulesConfEnum.OFF);

        conf.setTimeMessageAlways(RulesConfEnum.OFF);
        conf.setAnonimoAlways(RulesConfEnum.OFF);
        conf.setBlockResend(RulesConfEnum.OFF);
        conf.setBlockMediaDownload(RulesConfEnum.OFF);

        conf.setTimeMessageSeconds(300);

        conf.setBlackMessageAttachMandatoryReceived(RulesConfEnum.OFF);
        conf.setAnonimoRecived(false);

        //conf.setChangeNicknameToNumber(RulesConfEnum.ON);

        return conf;
    }
    public static int segundosCalculados(TextView tv, List list) {
        if (list != null){
            return segundosCalculados(tv, list.size());
        }
        return 0;
    }
    public static int segundosCalculados(TextView tv, int lenght) {
        if (tv != null){
            int seg=0;
            seg = new Double(Math.floor(lenght/44100.00)).intValue();

            tv.setText(CalcularTiempoFormater(seg));
            return seg;
        }
        return 0;
    }

    public static String CalcularTiempoFormater(long tsegundos){
        return CalcularTiempoFormater(Integer.parseInt(tsegundos+""));
    }
    public static String CalcularTiempoFormater(int tsegundos)
    {
        int horas = (tsegundos / 3600);
        int minutos = ((tsegundos-horas*3600)/60);
        int segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format(CONSTANT__STRING_FORMAT__TIME, segundos+"").replace(' ', '0');
        String minutosStr=String.format(CONSTANT__STRING_FORMAT__TIME, minutos+"").replace(' ', '0');

        return  minutos + ":" + segundosStr;
    }
    public static String CalcularTiempoFormaterSinHora(long tsegundos)
    {
        long horas = (tsegundos / 3600);
        long minutos = ((tsegundos-horas*3600)/60);
        long segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format(CONSTANT__STRING_FORMAT__TIME, segundos+"").replace(' ', '0');
        long horasMin = horas*60;
        long horasMasMinutos = horasMin+minutos;
        String minutosStr=horasMasMinutos+"";

        return  minutosStr + ":" + segundosStr;
    }

    public static int CalcularTiempoFormaterSoloMinutos(int tsegundos)
    {
        int horas = (tsegundos / 3600);
        int minutos = ((tsegundos-horas*3600)/60);
        int segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format(CONSTANT__STRING_FORMAT__TIME, segundos+"").replace(' ', '0');
        String minutosStr=String.format(CONSTANT__STRING_FORMAT__TIME, minutos+"").replace(' ', '0');

        return  minutos;
    }
    public static int CalcularTiempoFormaterSoloHora(int tsegundos)
    {
        int horas = (tsegundos / 3600);
        int minutos = ((tsegundos-horas*3600)/60);
        int segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format(CONSTANT__STRING_FORMAT__TIME, segundos+"").replace(' ', '0');
        String minutosStr=String.format(CONSTANT__STRING_FORMAT__TIME, minutos+"").replace(' ', '0');

        return  horas;
    }

    public static int CalcularTiempoFormaterSoloSegundos(int tsegundos)
    {
        int horas = (tsegundos / 3600);
        int minutos = ((tsegundos-horas*3600)/60);
        int segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format(CONSTANT__STRING_FORMAT__TIME, segundos+"").replace(' ', '0');
        String minutosStr=String.format(CONSTANT__STRING_FORMAT__TIME, minutos+"").replace(' ', '0');

        return  segundos;
    }
    public static int getSpinnerItem(ConfType c, boolean configurable){
        if (c.isGanaGrupo()) return 0;

        if (c.isSuperiorConf()) return 0;

        if (c.isValue()){
            if (!configurable) return 0;
            return 1;
        }else if (!c.isValue()){
            if (!configurable) return 1;
            return 2;
        }

        return -1;
    }

    public static Integer getGrupoUserConfMessageTimeGetSeconds( Spinner spMessageAvanzadoTimeValues) {
        Resources res = SingletonCurrentActivity.getInstance().get().getResources();
        String[] a = res.getStringArray(R.array.time_messages_values_in_seconds);
        int t = Integer.parseInt(a[spMessageAvanzadoTimeValues.getSelectedItemPosition()]);
        return t;
    }
    public static Integer getGrupoUserConfMessageTimeGetSecondsIndex(Integer value) {
        Resources res = SingletonCurrentActivity.getInstance().get().getResources();
        String[] a = res.getStringArray(R.array.time_messages_values_in_seconds);

        for (int i=0; i < a.length ; i++){
            if (a[i].equals(value+"")) return i;
        }
        return -1;
    }


    public void sendMessage(Activity activity,
                            IdMessageDTO reply, AEStoUse secretKeyPersonal, String idGrupo,
                            String txt, MediaDTO mediaDTO,
                            boolean isBlack, boolean isTime, boolean isAnonimo, boolean isSecret,
                            boolean isBlockResend, boolean isRetry, Integer timeSeconds, Message resendParent) throws Exception {

        System.out.println("texto mensaje: " +txt);

        if (ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().getAnonimo().equals(RulesConfEnum.MANDATORY)){
            isAnonimo=true;
        }

        if ( isAnonimo  && ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().getAnonimo().equals(RulesConfEnum.BLOCK   )){
            SingletonToastManager.getInstance().showToadShort(activity, "NO ANONIMIO");

            return;
        }

        if ( !ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().getAnonimo().equals(RulesConfEnum.BLOCK   )
        && ObserverGrupo.getInstance().getGrupoById(idGrupo).getUserConfDTO().getAnonimoAlways().equals(RulesConfEnum.ON   )
        ){
            isAnonimo=true;

        }

        boolean mustBeBlack = SelectView.isFull(
                SingletonValues.getInstance().getMyAccountConfDTO().isBlackMessageAttachMandatory(),
                ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().isBlackMessageAttachMandatory(),
                ObserverGrupo.getInstance().getGrupoById(idGrupo).getUserConfDTO().getBlackMessageAttachMandatory()
        );
        if ( mustBeBlack){
            if (mediaDTO != null && ( mediaDTO.getMediaType().equals(MediaTypeEnum.IMAGE)
                    || mediaDTO.getMediaType().equals(MediaTypeEnum.VIDEO) )){
                isBlack=true;
            }


        }



        boolean isBlockMediaDownload = SelectView.isFull(
                SingletonValues.getInstance().getMyAccountConfDTO().isBlockMediaDownload(),
                ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().isBlockMediaDownload(),
                ObserverGrupo.getInstance().getGrupoById(idGrupo).getUserConfDTO().getBlockMediaDownload()
        );

        if (mediaDTO != null && ( mediaDTO.getMediaType().equals(MediaTypeEnum.IMAGE)
                || mediaDTO.getMediaType().equals(MediaTypeEnum.VIDEO) )){

            mediaDTO.setDownloadable(!isBlockMediaDownload);
        }

        if ((txt == null || txt.equals("")) && mediaDTO == null) return;

        //if (isTime) isBlack=false;
        String asyncId = SingletonValues.getInstance().getCounterNextValue();

        String txtStr= txt;

        if (timeSeconds == null) timeSeconds=0;
        if (isSecret && !isRetry){
            try {

                txtStr = secretKeyPersonal.getAES(txt);

            } catch (Exception e) {
                e.printStackTrace();
                SimpleErrorDialog.errorDialog(activity, "Error enviando secret", e.getMessage());
                return;
            }

        }


        UsuarioDTO usuario = new UsuarioDTO();

        usuario.setIdUsuario(Singletons.usuario().getUsuario().getIdUsuario());

//        if (MasterGeneralConfiguration.buildHideNicknameConfigurationByGrupo(idGrupo).isValue()){
//            usuario.setNickname(Observers.grupo().getGrupoById(idGrupo).getAlias());
//        }else{
//            usuario.setNickname(Observers.grupo().getGrupoById(idGrupo).getNicknameForGrupo());
//        }
//
//        if ( usuario.getNickname() == null){
//            usuario.setNickname(Singletons.usuario().getUsuario().getNickname());
//        }

        Message miMensaje = new Message();
        miMensaje.setIdGrupo(idGrupo);
        miMensaje.setUsuarioCreacion(usuario);
        miMensaje.setText(txtStr);
        miMensaje.setBlackMessage(isBlack);
        miMensaje.setTimeMessage(timeSeconds);

        miMensaje.setAnonimo(isAnonimo);
        miMensaje.setSecretKeyPersonal(isSecret);
        miMensaje.setBlockResend(isBlockResend);
        miMensaje.setMessagesDetail(new MessageDetail[1]);
        miMensaje.setHideMessageReadState(ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().isHideMessageReadState());

        miMensaje.getMessagesDetail()[0] = new MessageDetail();
        miMensaje.getMessagesDetail()[0].setEstado(MessageState.MY_MESSAGE_SENDING);
        miMensaje.getMessagesDetail()[0].setUsuarioDestino(miMensaje.getUsuarioCreacion());
        miMensaje.getMessagesDetail()[0].setIdGrupo(miMensaje.getIdGrupo());

        if (reply != null){
            miMensaje.setParentReply(new IdMessageDTO());
            miMensaje.getParentReply().setIdGrupo(reply.getIdGrupo());
            miMensaje.getParentReply().setIdMessage(reply.getIdMessage());
        }
        if (resendParent != null){
            miMensaje.setParentResend(new IdMessageDTO());
            miMensaje.getParentResend().setIdGrupo(resendParent.getIdGrupo());
            miMensaje.getParentResend().setIdMessage(resendParent.getIdMessage());

        }
        MediaDTO media = null;

        if (mediaDTO != null){


            if (!isRetry) {
                media = new MediaDTO();
                media.setMediaType(mediaDTO.getMediaType());
                media.setDownloadable(mediaDTO.isDownloadable());
                {
                    byte[] encoded = mediaDTO.getData();

                    if (isSecret) {
                        encoded = secretKeyPersonal.getAESData(encoded);
                    }
                    media.setData(encoded);
                }

                if (mediaDTO.getMiniatura() != null ) {
                    byte[] encoded = mediaDTO.getMiniatura();

                    if (isSecret) {
                        encoded = secretKeyPersonal.getAESData(encoded);
                    }
                    media.setMiniatura(encoded);
                }
                //media.setData(ObservatorGrupos.getInstance().getGrupoAESToUseById().get(idGrupo).getAES(encoded));


                //encoded = ZipUtil.compress(encoded);


                miMensaje.setMedia(media);
            }else{
                miMensaje.setMedia(mediaDTO);
            }
        }



        Message mensaje = new Message();
        mensaje.setIdGrupo(idGrupo);
        mensaje.setBlackMessage(isBlack);
        mensaje.setTimeMessage(timeSeconds);
        mensaje.setAnonimo(isAnonimo);
        mensaje.setSecretKeyPersonal(isSecret);
        mensaje.setBlockResend(isBlockResend);
        mensaje.setHideMessageReadState(ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().isHideMessageReadState());


        //mensaje.setMedia(miMensaje.getMedia());

        String txtStrEncr = Observers.grupo().getGrupoById(idGrupo).getAESToUse().getAES(txtStr);
        mensaje.setText(txtStrEncr);

        byte[] data = null;
        if (mediaDTO != null){
            mensaje.setMedia(new MediaDTO());
            mensaje.getMedia().setMediaType(miMensaje.getMedia().getMediaType());
            mensaje.getMedia().setDownloadable(miMensaje.getMedia().isDownloadable());
            mensaje.getMedia().setIdGrupo(miMensaje.getIdGrupo());
            data = Observers.grupo().getGrupoById(idGrupo).getAESToUse().getAES(miMensaje.getMedia().getData()).getBytes();

            if (miMensaje.getMedia().getMiniatura() != null){
                mensaje.getMedia().setMiniatura(Observers.grupo().getGrupoById(idGrupo).getAESToUse().getAESData(miMensaje.getMedia().getMiniatura()));
            }

        }
        if (reply != null){
            mensaje.setParentReply(new IdMessageDTO());
            mensaje.getParentReply().setIdGrupo(reply.getIdGrupo());
            mensaje.getParentReply().setIdMessage(reply.getIdMessage());
        }

        if (resendParent != null){
            mensaje.setParentResend(new IdMessageDTO());
            mensaje.getParentResend().setIdGrupo(resendParent.getIdGrupo());
            mensaje.getParentResend().setIdMessage(resendParent.getIdMessage());

        }
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_SEND);
        p.setAsyncId(asyncId);
        p.setMessage(mensaje);

        Log.i( "************************",">> Mensaje Enviado >>");
        //Log.i( "<< << ",UtilsStringSingleton.getInstance().gsonToSend(p));

        Observers.message().mensajeAddItem(p, miMensaje, asyncId);

        RestExecute.doitSend(activity, p, data,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        boolean a=true;

                        Log.i( "*** ","<< Mensaje Enviado Respuesta Servidor <<");
                        //Log.i( "*** << << ",UtilsStringSingleton.getInstance().gsonToSend(response.getBody()));
                        Observers.message().mensajePropio(response.getBody());

                        Observers.message().mensaje(response.getBody(),true, activity);
                        Log.i( "*** " , "Fin Envio Success");
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        miMensaje.getMessagesDetail()[0].setIdMessage(response.getBody().getAsyncId());
                        miMensaje.getMessagesDetail()[0].setEstado(MessageState.MY_MESSAGE_ERROR_NOT_SEND);
                        miMensaje.getMessagesDetail()[0].setError(response.getBody().getCodigoRespuesta());
                        response.getBody().setMessage(miMensaje);
                        response.getBody().getMessage().setIdMessage(response.getBody().getAsyncId());
                        Observers.message().mensaje(response.getBody(),true,activity);
                        Log.i( "*** " , "Fin Envio OnError");
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }
    public static RulesConfEnum getRulesConfEnum(Spinner spinner, boolean configurable) {

        int conf = -1;
        if (configurable) {
            conf = 0;
        }
        if ( spinner.getSelectedItemPosition() == 1+conf) {
            return RulesConfEnum.ON;
        } else if ( spinner.getSelectedItemPosition() == 2+conf){
            return RulesConfEnum.OFF;
        }else {
            return RulesConfEnum.NULL;
        }
    }


}
