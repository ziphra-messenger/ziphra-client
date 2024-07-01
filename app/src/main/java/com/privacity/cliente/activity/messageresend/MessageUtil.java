package com.privacity.cliente.activity.messageresend;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.ConfType;
import com.privacity.cliente.activity.message.MasterGeneralConfiguration;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MediaDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.enumeration.GrupoUserConfEnum;
import com.privacity.common.enumeration.MediaTypeEnum;
import com.privacity.common.enumeration.MessageState;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.util.List;

public class MessageUtil {
    public static GrupoUserConfDTO getDefaultGrupoUserConf(String idGrupo, String idUsuario) {
        GrupoUserConfDTO conf = new GrupoUserConfDTO();

        conf.setIdUsuario(idGrupo );
        conf.setIdUsuario( idUsuario);

        conf.setExtraAesAlways(GrupoUserConfEnum.GRUPO_FALSE);
        conf.setBlackMessageAlways(GrupoUserConfEnum.GRUPO_FALSE);
        conf.setTimeMessageAlways(GrupoUserConfEnum.GRUPO_FALSE);
        conf.setAnonimoAlways(GrupoUserConfEnum.GRUPO_FALSE);
        conf.setPermitirReenvio(GrupoUserConfEnum.GRUPO_TRUE);
        conf.setDownloadAllowImage(GrupoUserConfEnum.GRUPO_TRUE);
        conf.setDownloadAllowAudio(GrupoUserConfEnum.GRUPO_TRUE);
        conf.setDownloadAllowVideo(GrupoUserConfEnum.GRUPO_TRUE);
        conf.setTimeMessageSeconds(300);

        conf.setBlackMessageRecived(GrupoUserConfEnum.GRUPO_FALSE);
        conf.setAnonimoRecived(GrupoUserConfEnum.GRUPO_FALSE);

        //conf.setChangeNicknameToNumber(GrupoUserConfEnum.GRUPO_TRUE);

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

    public static String CalcularTiempoFormater(int tsegundos)
    {
        int horas = (tsegundos / 3600);
        int minutos = ((tsegundos-horas*3600)/60);
        int segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format("%1$2s", segundos+"").replace(' ', '0');
        String minutosStr=String.format("%1$2s", minutos+"").replace(' ', '0');

        return  minutos + ":" + segundosStr;
    }
    public static String CalcularTiempoFormaterSinHora(long tsegundos)
    {
        long horas = (tsegundos / 3600);
        long minutos = ((tsegundos-horas*3600)/60);
        long segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format("%1$2s", segundos+"").replace(' ', '0');
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
        String segundosStr=String.format("%1$2s", segundos+"").replace(' ', '0');
        String minutosStr=String.format("%1$2s", minutos+"").replace(' ', '0');

        return  minutos;
    }
    public static int CalcularTiempoFormaterSoloHora(int tsegundos)
    {
        int horas = (tsegundos / 3600);
        int minutos = ((tsegundos-horas*3600)/60);
        int segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format("%1$2s", segundos+"").replace(' ', '0');
        String minutosStr=String.format("%1$2s", minutos+"").replace(' ', '0');

        return  horas;
    }

    public static int CalcularTiempoFormaterSoloSegundos(int tsegundos)
    {
        int horas = (tsegundos / 3600);
        int minutos = ((tsegundos-horas*3600)/60);
        int segundos = tsegundos-(horas*3600+minutos*60);
        String segundosStr=String.format("%1$2s", segundos+"").replace(' ', '0');
        String minutosStr=String.format("%1$2s", minutos+"").replace(' ', '0');

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

    public static Integer getGrupoUserConfMessageTimeGetSeconds(Activity activity, Spinner spMessageAvanzadoTimeValues) {
        Resources res = activity.getResources();
        String[] a = res.getStringArray(R.array.time_messages_values_in_seconds);
        int t = Integer.parseInt(a[spMessageAvanzadoTimeValues.getSelectedItemPosition()]);
        return t;
    }
    public static Integer getGrupoUserConfMessageTimeGetSecondsIndex(Activity activity, Integer value) {
        Resources res = activity.getResources();
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
                            boolean isPermitirReenvio, boolean isRetry, Integer timeSeconds) throws Exception {

        if (ObserverGrupo.getInstance().getGrupoById(idGrupo).getGralConfDTO().isBlackMessageAttachMandatory()){
            if (mediaDTO != null && ( mediaDTO.getMediaType().equals(MediaTypeEnum.IMAGE)
                    || mediaDTO.getMediaType().equals(MediaTypeEnum.VIDEO) )){
                isBlack=true;
            }


        }
        if (mediaDTO != null && ( mediaDTO.getMediaType().equals(MediaTypeEnum.IMAGE))){
            ConfType conf = MasterGeneralConfiguration.buildImageDownload(idGrupo);
            mediaDTO.setDownloadable(conf.value);
        }

        if ((txt == null || txt.equals("")) && mediaDTO == null) return;

        //if (isTime) isBlack=false;
        String asyncId = SingletonValues.getInstance().getCounterNextValue();

        String txtStr=new String(txt);

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

        usuario.setIdUsuario(SingletonValues.getInstance().getUsuario().getIdUsuario());

//        if (MasterGeneralConfiguration.buildHideNicknameConfigurationByGrupo(idGrupo).isValue()){
//            usuario.setNickname(Observers.grupo().getGrupoById(idGrupo).getAlias());
//        }else{
//            usuario.setNickname(Observers.grupo().getGrupoById(idGrupo).getNicknameForGrupo());
//        }
//
//        if ( usuario.getNickname() == null){
//            usuario.setNickname(SingletonValues.getInstance().getUsuario().getNickname());
//        }

        Message miMensaje = new Message();
        miMensaje.setIdGrupo(idGrupo);
        miMensaje.setUsuarioCreacion(usuario);
        miMensaje.setText(txtStr);
        miMensaje.setBlackMessage(isBlack);
        miMensaje.setTimeMessage(timeSeconds);
        miMensaje.setAnonimo(isAnonimo);
        miMensaje.setSecretKeyPersonal(isSecret);
        miMensaje.setPermitirReenvio(isPermitirReenvio);
        miMensaje.setMessagesDetailDTO(new MessageDetailDTO[1]);

        miMensaje.getMessagesDetailDTO()[0] = new MessageDetailDTO();
        miMensaje.getMessagesDetailDTO()[0].setEstado(MessageState.MY_MESSAGE_SENDING);
        miMensaje.getMessagesDetailDTO()[0].setUsuarioDestino(miMensaje.getUsuarioCreacion());
        miMensaje.getMessagesDetailDTO()[0].setIdGrupo(miMensaje.getIdGrupo());

        if (reply != null){
            miMensaje.setParentReply(new IdMessageDTO());
            miMensaje.getParentReply().setIdGrupo(reply.getIdGrupo());
            miMensaje.getParentReply().setIdMessage(reply.getIdMessage());
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
                        encoded = secretKeyPersonal.getAES(encoded);
                    }
                    media.setData(encoded);
                }

                if (mediaDTO.getMiniatura() != null ) {
                    byte[] encoded = mediaDTO.getMiniatura();

                    if (isSecret) {
                        encoded = secretKeyPersonal.getAES(encoded);
                    }
                    media.setMiniatura(encoded);
                }
                //media.setData(ObservatorGrupos.getInstance().getGrupoAESToUseById().get(idGrupo).getAES(encoded));


                //encoded = ZipUtil.compress(encoded);


                miMensaje.setMediaDTO(media);
            }else{
                miMensaje.setMediaDTO(mediaDTO);
            }
        }



        MessageDTO mensaje = new MessageDTO();
        mensaje.setIdGrupo(idGrupo);
        mensaje.setBlackMessage(isBlack);
        mensaje.setTimeMessage(timeSeconds);
        mensaje.setAnonimo(isAnonimo);
        mensaje.setSecretKeyPersonal(isSecret);
        mensaje.setPermitirReenvio(isPermitirReenvio);

        //mensaje.setMediaDTO(miMensaje.getMediaDTO());

        String txtStrEncr = Observers.grupo().getGrupoById(idGrupo).getAESToUse().getAES(txtStr);
        mensaje.setText(txtStrEncr);

        byte[] data = null;
        if (mediaDTO != null){
            mensaje.setMediaDTO(new MediaDTO());
            mensaje.getMediaDTO().setMediaType(miMensaje.getMediaDTO().getMediaType());
            mensaje.getMediaDTO().setDownloadable(miMensaje.getMediaDTO().isDownloadable());
            mensaje.getMediaDTO().setIdGrupo(miMensaje.getIdGrupo());
            data = Observers.grupo().getGrupoById(idGrupo).getAESToUse().getAES(miMensaje.getMediaDTO().getData());

            if (miMensaje.getMediaDTO().getMiniatura() != null){
                mensaje.getMediaDTO().setMiniatura(Observers.grupo().getGrupoById(idGrupo).getAESToUse().getAES(miMensaje.getMediaDTO().getMiniatura()));
            }

        }
        if (reply != null){
            mensaje.setParentReply(new IdMessageDTO());
            mensaje.getParentReply().setIdGrupo(reply.getIdGrupo());
            mensaje.getParentReply().setIdMessage(reply.getIdMessage());
        }


        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_MESSAGE);
        p.setAction(ProtocoloActionsEnum.PROTOCOLO_ACTION_MESSAGE_SEND);
        p.setAsyncId(asyncId);
        p.setMessageDTO(mensaje);

        Log.i( "************************",">> Mensaje Enviado >>");
        //Log.i( "<< << ",GsonFormated.get().toJson(p));

        Observers.message().mensajeAddItem(p, miMensaje, asyncId);

        RestExecute.doitSend(activity, p, data,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        boolean a=true;

                        Log.i( "*** ","<< Mensaje Enviado Respuesta Servidor <<");
                        //Log.i( "*** << << ",GsonFormated.get().toJson(response.getBody()));
                        Observers.message().mensajePropio(response.getBody());

                        Observers.message().mensaje(response.getBody(),true, activity);
                        Log.i( "*** " , "Fin Envio Success");
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                        miMensaje.getMessagesDetailDTO()[0].setIdMessage(response.getBody().getAsyncId());
                        miMensaje.getMessagesDetailDTO()[0].setEstado(MessageState.MY_MESSAGE_ERROR_NOT_SEND);
                        response.getBody().setMessageDTO(miMensaje);
                        response.getBody().getMessageDTO().setIdMessage(response.getBody().getAsyncId());
                        Observers.message().mensaje(response.getBody(),true,activity);
                        Log.i( "*** " , "Fin Envio OnError");
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }
    public static GrupoUserConfEnum getGrupoUserConfEnum(Spinner spinner, boolean configurable) {

        int conf = -1;
        if (configurable) {
            conf = 0;
        }
        if ( spinner.getSelectedItemPosition() == 1+conf) {
            return GrupoUserConfEnum.GRUPO_TRUE;
        } else if ( spinner.getSelectedItemPosition() == 2+conf){
            return GrupoUserConfEnum.GRUPO_FALSE;
        }else {
            return GrupoUserConfEnum.GENERAL_VALUE;
        }
    }
}
