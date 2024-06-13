package com.privacity.cliente.singleton.observers;

import android.app.Activity;

import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonSessionFinish;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.interfaces.SingletonReset;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.notificacion.Notificacion;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.WrittingDTO;
import com.privacity.common.enumeration.GrupoUserConfEnum;
import com.privacity.common.enumeration.MessageState;

import org.springframework.http.ResponseEntity;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.Getter;
import lombok.Setter;


public class ObserverMessage implements SingletonReset {
    @Getter
    private Set<ItemListMessage> messageSelected = new CopyOnWriteArraySet<ItemListMessage>();
    private List<ObservadoresMensajes> o = new CopyOnWriteArrayList<ObservadoresMensajes>();

    private Map<String, CopyOnWriteArrayList<MessageDetailDTO>> mensajesDetailsPorGrupo = new HashMap<String, CopyOnWriteArrayList<MessageDetailDTO>>();
    private Map<String,CopyOnWriteArrayList<MessageDTO>> todosLosMensajesPorGrupo = new HashMap<String,CopyOnWriteArrayList<MessageDTO>>();
    private Map<String,Message> todosLosMensajesPorId = new HashMap<String, Message>();

    private HashMap<String, Integer> estadoMensaje = new HashMap<String, Integer>();

    private static ObserverMessage instance;
    @Getter @Setter
    private boolean messageOnTop=false;
    public static ObserverMessage getInstance() {

        if (instance == null){
            instance = new ObserverMessage();
        }
        return instance;

    }

    private  void sortById(List<MessageDTO> items)
    {

        items.sort(new Comparator<MessageDTO>() {
            @Override
            public int compare(MessageDTO o1, MessageDTO o2) {
                try {
                    if ( o1.getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING.name()) &&
                            o2.getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING.name())){

                        int value =  o1.getIdMessage().compareTo(o2.getIdMessage());
                        return value;

                    }
                    if (o1.getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING.name())){
                        return 1;
                    }
                    ;
                    if (o2.getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING.name())){
                        return -1;
                    }
                    ;

                    int value =  o1.getIdMessage().compareTo(o2.getIdMessage());
                    return value;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                return -1;
            }
        });
    }



    public int contarEstadoMensajePorGrupo(String idGrupo) {
        if ( estadoMensaje.get(idGrupo) == null){
            estadoMensaje.put(idGrupo, 0);
        }
        return estadoMensaje.get(idGrupo);
    }

    private synchronized void estadoMensajeCalculatelement(String idGrupo, int value) {
        if ( estadoMensaje.get(idGrupo) == null){
            estadoMensaje.put(idGrupo, 0);
        }

        if ( value < 1 && estadoMensaje.get(idGrupo) == 0){
            return;
        }
        estadoMensaje.put(idGrupo, estadoMensaje.get(idGrupo) + value);
    }

    public void estadoMensajeAddElement(String idGrupo) {
        estadoMensajeCalculatelement(idGrupo, 1);

    }

    public void estadoMensajeRestarElement(String idGrupo) {

        estadoMensajeCalculatelement(idGrupo, -1);

    }

    public Map<String,CopyOnWriteArrayList<MessageDTO>> getTodosLosMensajesPorGrupo() {
        return this.todosLosMensajesPorGrupo;
    }
    public CopyOnWriteArrayList<MessageDTO> getTodosLosMensajesPorGrupo(String idGrupo) {
        return this.todosLosMensajesPorGrupo.get(idGrupo);
    }

    @Override
    public void reset() {
        instance = null;
    }

    private ObserverMessage() { }


    public void dessuscribirse( ObservadoresMensajes n) {
        o.remove(n);
    }
    public void suscribirse( ObservadoresMensajes n) {
        o.add(0,n);
    }

    public Message getMensajesPorId(String idMensajeToMap){
        return this.todosLosMensajesPorId.get(idMensajeToMap);
    }

    public int getMensajesDetailsPorGrupoUnread(String idGrupo){
        if (this.mensajesDetailsPorGrupo.get(idGrupo) == null) return 0;
        CopyOnWriteArrayList<MessageDetailDTO> l = this.mensajesDetailsPorGrupo.get(idGrupo);
        int r=0;
        for ( MessageDetailDTO md : l){
            if (md.getUsuarioDestino().getIdUsuario().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())){
                if (md.getEstado().equals(MessageState.DESTINY_DELIVERED.toString())
                        || md.getEstado().equals(MessageState.DESTINY_SERVER.toString())){
                    r++;
                }
            }
        }
        return r;
    }

    public List<MessageDetailDTO> getMensajesDetailsPorGrupo(String idGrupo){
        if ( !mensajesDetailsPorGrupo.containsKey(idGrupo)){
            mensajesDetailsPorGrupo.put((idGrupo), new CopyOnWriteArrayList<MessageDetailDTO>());
        }

        return this.mensajesDetailsPorGrupo.get(idGrupo);

    }
    /*
    public void mensajes(MessageDTO[] body) {

        for ( int i = 0 ; i < body.length ; i++){
            if (!todosLosMensajesPorGrupo.containsKey(body[i].getIdGrupo())){
                todosLosMensajesPorGrupo.put(body[i].getIdGrupo(), new CopyOnWriteArrayList<MessageDTO>());
                mensajesDetailsPorGrupo.put(body[i].getIdGrupo(), new CopyOnWriteArrayList<MessageDetailDTO>());
            }
            todosLosMensajesPorGrupo.get(body[i].getIdGrupo()).add(body[i]);
            todosLosMensajesPorId.put(body[i].getIdMessageToMap(),body[i]);

            for ( int j = 0 ; j < body[i].getMessagesDetailDTO().length ; j++){
                mensajesDetailsPorGrupo.get(body[i].getIdGrupo()).add(body[i].getMessagesDetailDTO()[j]);
            }

        }

    }
*/
    public void emptyList(String idGrupo) {
        SingletonSessionFinish.getInstance().restart();

        if (todosLosMensajesPorGrupo.get(idGrupo) != null){
            for ( MessageDTO m : todosLosMensajesPorGrupo.get(idGrupo)){

                todosLosMensajesPorId.remove(m.getIdMessageToMap());

            }
        }

        todosLosMensajesPorGrupo.put(idGrupo, new CopyOnWriteArrayList<MessageDTO>());
        mensajesDetailsPorGrupo.put(idGrupo, new CopyOnWriteArrayList<MessageDetailDTO>());
        avisarEmptyList();
    }

    public void mensaje(ProtocoloDTO protocoloDTO,Activity context, MessageDTO finalMedia) {
        mensaje(protocoloDTO,true,context);
    }

    HashMap<String,MessageDTO> peticiones = new HashMap<String,MessageDTO>();

    public void mensajeAddItem(ProtocoloDTO p , Message mensaje, String asyncId){
        SingletonSessionFinish.getInstance().restart();
        if (!todosLosMensajesPorGrupo.containsKey(mensaje.getIdGrupo())){
            todosLosMensajesPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<MessageDTO>());
            mensajesDetailsPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<MessageDetailDTO>());
        }

        todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()).add(mensaje);
        sortById(todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()));
        mensaje.setIdMessage(asyncId);

        mensaje.getMessagesDetailDTO()[0].setIdMessage(asyncId);
        mensajesDetailsPorGrupo.get(mensaje.getIdGrupo()).add(mensaje.getMessagesDetailDTO()[0]);
        if (mensaje.getMediaDTO() != null){
            mensaje.getMediaDTO().setIdMessage(asyncId);
        }
        /*
        if(!mensaje.isAnonimo()){
            mensaje.setUsuarioCreacion(SingletonValues.getInstance().getUsuario());
        }
        */
        todosLosMensajesPorId.put(mensaje.getIdMessageToMap(), mensaje);
        peticiones.put(asyncId, mensaje);

        //p.setMessageDTO(mensaje);
// esto se podra borrar?

            for (ObservadoresMensajes e : o) {

                e.mensajeAddItem(mensaje, asyncId);
            }


        //avisar(p);

    }

    public void mensajeNuevoWSReply(ProtocoloDTO protocoloDTO, boolean avisar, Activity context) {
        mensaje(protocoloDTO,avisar,context,true);
    }

    public void mensajeNuevoWS(ProtocoloDTO protocoloDTO, boolean avisar, Activity context) {

        mensaje(protocoloDTO,avisar,context);
    }
    public void mensaje(ProtocoloDTO protocoloDTO, boolean avisar, Activity context) {
        mensaje(protocoloDTO,avisar,context,false);
    }
    public synchronized void mensaje(ProtocoloDTO protocoloDTO, boolean avisar, Activity context, boolean isReply) {
        Message messageMapped = new Message(protocoloDTO.getMessageDTO());
        messageMapped.setReply(isReply);
        protocoloDTO.setMessageDTO(messageMapped);

        if (protocoloDTO.getAsyncId() != null) {
            MessageDTO mpt = peticiones.get(protocoloDTO.getAsyncId());

            borrarMensaje2(mpt.getMessagesDetailDTO()[0], true);
            //ObservatorGrupos.getInstance().avisarCambioUnread(mpt.idGrupo, +1);
            avisar=true;
            String mptId = mpt.getIdMessageToMap();
            String mptIpDetails = mpt.getMessagesDetailDTO()[0].getIdMessageDetailToMap();

            mpt.setIdMessage(protocoloDTO.getMessageDTO().getIdMessage());
            //mpt.setUsuarioCreacion(protocoloDTO.getMessageDTO().getUsuarioCreacion());
            if (protocoloDTO.getMessageDTO().getMediaDTO() != null) {
                mpt.getMediaDTO().setIdMessage(protocoloDTO.getMessageDTO().getIdMessage());
            }
            mpt.setMessagesDetailDTO(protocoloDTO.getMessageDTO().getMessagesDetailDTO());
            peticiones.remove(protocoloDTO.getAsyncId());

            protocoloDTO.setMessageDTO(mpt);

        } else if (protocoloDTO.getMessageDTO().isSystemMessage()){
            estadoMensajeAddElement(protocoloDTO.getMessageDTO().getIdGrupo());

            if ( this.todosLosMensajesPorId.containsKey(protocoloDTO.getMessageDTO().getIdMessageToMap())){
                return;
            }

        }else{

            if ( this.todosLosMensajesPorId.containsKey(protocoloDTO.getMessageDTO().getIdMessageToMap())){
                //actualizar mensaje data
                return;
            }
            estadoMensajeAddElement(protocoloDTO.getMessageDTO().getIdGrupo());

            if (protocoloDTO.getMessageDTO().getText() != null && !protocoloDTO.getMessageDTO().getText().trim().equals("") ){
                String text;
                try {
                    text = Observers.grupo().getGrupoById(protocoloDTO.getMessageDTO().getIdGrupo()).getAESToUse().getAESDecrypt(protocoloDTO.getMessageDTO().getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    text = e.getMessage();
                }

                protocoloDTO.getMessageDTO().setText(text);

            }

            if (protocoloDTO.getMessageDTO().getMediaDTO() != null &&
                    (
                    protocoloDTO.getMessageDTO().getMediaDTO().getData() != null ||
                    protocoloDTO.getMessageDTO().getMediaDTO().getMiniatura() != null
                    )
            ){


                try {
                    byte[] data;
                    data = Observers.grupo().getGrupoById(protocoloDTO.getMessageDTO().getIdGrupo()).getAESToUse().getAESDecrypt(protocoloDTO.getMessageDTO().getMediaDTO().getData());
                    protocoloDTO.getMessageDTO().getMediaDTO().setData(data);

                    if ( protocoloDTO.getMessageDTO().getMediaDTO().getMiniatura() != null){
                        byte[] miniatura;
                        miniatura = Observers.grupo().getGrupoById(protocoloDTO.getMessageDTO().getIdGrupo()).getAESToUse().getAESDecrypt(protocoloDTO.getMessageDTO().getMediaDTO().getMiniatura());
                        protocoloDTO.getMessageDTO().getMediaDTO().setMiniatura(miniatura);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    protocoloDTO.getMessageDTO().setMediaDTO(null);
                    protocoloDTO.getMessageDTO().setText("Error desencriptandomedia e="+ e.getMessage());
                }

            }
        }

        Message mensaje = (Message) protocoloDTO.getMessageDTO();


        /*
        if (finalMedia != null){
            mensaje.setText(finalMedia.getText());
            if (mensaje.getMediaDTO() != null){
                mensaje.getMediaDTO().setData(finalMedia.getMediaDTO().getData());
            }

        }*/
        MessageDetailDTO miMensaje=null;

        if (!todosLosMensajesPorGrupo.containsKey(mensaje.getIdGrupo())){
            todosLosMensajesPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<MessageDTO>());
            mensajesDetailsPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<MessageDetailDTO>());
        }

       // if ( todosLosMensajesPorId.get(mensaje.getIdMessage() ) != null) {
            todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()).add(mensaje);
            sortById(todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()));
            todosLosMensajesPorId.put(mensaje.getIdMessageToMap(), mensaje);

            for (int j = 0; j < mensaje.getMessagesDetailDTO().length; j++) {
                MessageDetailDTO nuevo = mensaje.getMessagesDetailDTO()[j];
                if (mensajesDetailsPorGrupo.get(mensaje.getIdGrupo()) == null) {
                    mensajesDetailsPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<>());
                }
                mensajesDetailsPorGrupo.get(mensaje.getIdGrupo()).add(nuevo);

                if (nuevo.getUsuarioDestino().getIdUsuario().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())) {
                    miMensaje = nuevo;
                }
            }

            // cambio el estado del mensaje







           cambiarEstadoUso(miMensaje, false, context, mensaje );
        if (avisar) avisar(protocoloDTO);
        //}
    }
/*
    public void borrarMensaje(MessageDetailDTO detail){
        //MessageDTO m = this.getMensajesPorId(detail.getIdMessage());
        CopyOnWriteArrayList<MessageDetailDTO> details = mensajesDetailsPorGrupo.get(detail.getIdGrupo());
        for ( int i = details.size()-1 ; i >=0 ; i--){

            if (details.get(i).getIdMessageDetailToMap().equals(detail.getIdMessageDetailToMap())){
                details.remove(i);
            }
        }

        todosLosMensajesPorId.remove(detail.getIdMessageToMap());
        removeMessage(e)

    }*/
public void cambiarEstadoUso(MessageDetailDTO miMensaje, boolean forzar,Activity context){
     cambiarEstadoUso( miMensaje,  forzar, context, null);
}
    public void cambiarEstadoUso(MessageDetailDTO miMensaje, boolean forzar,Activity context,Message message){
        //if (1==1) return;
        String viejoEstado = miMensaje.getEstado();

        if ((!miMensaje.getEstado().equals(MessageState.MY_MESSAGE_SENDING.name())) &&
                (!miMensaje.getEstado().equals(MessageState.MY_MESSAGE_SENT.name())) &&
        (!miMensaje.getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND.name()))) {

            //estadoMensajeAddElement(miMensaje.getIdGrupo());


            boolean reciboBlack = false;
            if (Observers.grupo().getGrupoById(miMensaje.getIdGrupo()).getUserConfDTO() != null) {

                if (Observers.grupo().getGrupoById(miMensaje.getIdGrupo()).getUserConfDTO().getBlackMessageRecived() != null) {
                    if (Observers.grupo().getGrupoById(miMensaje.getIdGrupo()).getUserConfDTO().getBlackMessageRecived().equals(GrupoUserConfEnum.GRUPO_TRUE)) {
                        reciboBlack = true;
                    }

                }
            }

            if ((!this.getMensajesPorId(miMensaje.getIdMessageToMap()).isBlackMessage() &&
                    !this.getMensajesPorId(miMensaje.getIdMessageToMap()).isTimeMessage() &&
                    !reciboBlack

            ) || forzar) {
                if (SingletonValues.getInstance().getGrupoSeleccionado() != null && messageOnTop) {
                    if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(miMensaje.getIdGrupo())) {
                        miMensaje.setEstado(MessageState.DESTINY_READED.name());
                        estadoMensajeRestarElement(miMensaje.getIdGrupo());
                        List<MessageDetailDTO> lista = mensajesDetailsPorGrupo.get(miMensaje.getIdGrupo());
                        for (int i = 0; i < lista.size(); i++) {
                            if (miMensaje.getIdMessageDetailToMap().equals(lista.get(i).getIdMessageDetailToMap())) {
                                lista.get(i).setEstado(MessageState.DESTINY_READED.name());

                            }
                        }
                    } else {
                        cambiarToDestinyDelivered(miMensaje, message);
                    }
                } else {

                    cambiarToDestinyDelivered(miMensaje, message);
                }
            } else {

                cambiarToDestinyDelivered(miMensaje, message);
            }
        }else{

                cambiarToDestinyDelivered(miMensaje, message);


        }
        if (!viejoEstado.equals(miMensaje.getEstado())  ) {
            // llamo al rest y cambio estado
            mensajeChangeState(miMensaje,context);
            avisarCambioEstado(miMensaje);
        }

        if (miMensaje.getUsuarioDestino().getIdUsuario().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())) {
            avisarCambioEstado(miMensaje);
        }
    }

    private void cambiarToDestinyDelivered(MessageDetailDTO miMensaje, Message message) {

        if  (miMensaje.getEstado().equals(MessageState.DESTINY_SERVER.name()
        ) && message != null && !message.getUsuarioCreacion().getIdUsuario()
                .equals( SingletonValues.getInstance().getUsuario().getIdUsuario())

        ){

            miMensaje.setEstado(MessageState.DESTINY_DELIVERED.name());
            //estadoMensajeAddElement(miMensaje.getIdGrupo());
            List<MessageDetailDTO> lista = mensajesDetailsPorGrupo.get(miMensaje.getIdGrupo());
            for ( int i = 0 ; i < lista.size() ; i++){
                if (miMensaje.getIdMessageDetailToMap().equals(lista.get(i).getIdMessageDetailToMap())){
                    lista.get(i).setEstado(MessageState.DESTINY_DELIVERED.name());

                }
            }

            Notificacion.getInstance().notificacion();
        }
    }

    private void avisar(ProtocoloDTO protocoloDTO) {
        for( ObservadoresMensajes e : o) {
            e.nuevoMensaje(protocoloDTO);
        }
    }
    private void avisarBorrado(MessageDetailDTO detail, boolean avisarSoloGrupos) {
        for( ObservadoresMensajes e : o) {

            if (e instanceof GrupoActivity) {
                e.borrarMensaje(detail);
            }else{

                if (!avisarSoloGrupos){
                    e.borrarMensaje(detail);
                }

            }

        }
    }
    private void avisarEmptyList() {
        for( ObservadoresMensajes e : o) {
            e.emptyList();
        }
    }


    public void mensajeChangeState(MessageDetailDTO md, Activity context) {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MESSAGE);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MESSAGE_CHANGE_STATE);
        p.setObjectDTO(GsonFormated.get().toJson(md));
        RestExecute.doit(context,
                p, new CallbackRest(){

                            @Override
                            public void response(ResponseEntity<ProtocoloDTO> response) {
                                MessageDetailDTO l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MessageDetailDTO.class);

                            }

                            @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                                SimpleErrorDialog.errorDialog( context, "Messages Error: " , response.getBody().getCodigoRespuesta() );

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        SimpleErrorDialog.errorDialog( context, "Messages Error: " , msg );

                    }

                        });


    }



    public void mensajeDetailChangeState(ProtocoloDTO protocoloDTO) {
        MessageDetailDTO messageDetailDTO = GsonFormated.get().fromJson(protocoloDTO.getObjectDTO(), MessageDetailDTO.class);
        MessageDTO m = this.getMensajesPorId(messageDetailDTO.getIdMessageToMap());

        if (m != null) {
            if (m.getMessagesDetailDTO() != null) {
                for (int i = 0; i < m.getMessagesDetailDTO().length; i++) {

                    if (messageDetailDTO.getUsuarioDestino() != null  && m.getMessagesDetailDTO()[i].getIdMessageDetailToMap().equals(messageDetailDTO.getIdMessageDetailToMap())) {
                        m.getMessagesDetailDTO()[i].setEstado(messageDetailDTO.getEstado());
                        avisarCambioEstado(messageDetailDTO);
                    }
                }
            }
        }
    }

    private void avisarCambioEstado(MessageDetailDTO messageDetailDTO) {
        for( ObservadoresMensajes e : o) {
            e.cambioEstado(messageDetailDTO);
        }
    }

    public void borrarMensaje2(MessageDetailDTO detail,boolean avisarSoloGrupos) {
        todosLosMensajesPorId.remove(detail.getIdMessageToMap());

        List<MessageDetailDTO> list = mensajesDetailsPorGrupo.get(detail.getIdGrupo());
        for (MessageDetailDTO md : list){
            if (md.getIdMessageDetailToMap().equals(detail.getIdMessageDetailToMap())){
                list.remove(md);
            }
        }

        List<MessageDTO> listM = todosLosMensajesPorGrupo.get(detail.getIdGrupo());
        for (MessageDTO m : listM){
            if (m.getIdMessageToMap().equals(detail.getIdMessageToMap())){
                list.remove(m);
            }
        }

        avisarBorrado(detail,avisarSoloGrupos);
    }

    public void removeAllMessageFromUser(ProtocoloDTO protocoloDTO) {
//        Grupo grupo = GsonFormated.get().fromJson(protocoloDTO.getObjectDTO(), Grupo.class);
//        String username = grupo.getUsersDTO()[0].getUsername();
//        removeAllMessageFromUser(grupo.getIdGrupo(), username);
    }
    public void removeAllMessageFromUser(String idGrupo, String idUsuario) {

        {
            if (this.todosLosMensajesPorGrupo.get(idGrupo) != null) {
                for (MessageDTO m : this.todosLosMensajesPorGrupo.get(idGrupo)) {
                    if (!m.isAnonimo() && m.getUsuarioCreacion().getIdUsuario().equals(idUsuario)) {
                        todosLosMensajesPorId.remove(m.getIdMessageToMap());
                        this.todosLosMensajesPorGrupo.get(idGrupo).remove(m);
                    }
                }

                for (MessageDTO m : this.todosLosMensajesPorGrupo.get(idGrupo)) {
                    if (!m.isAnonimo() && m.getUsuarioCreacion().getIdUsuario().equals(idUsuario)) {
                        this.todosLosMensajesPorGrupo.get(idGrupo).remove(m);
                    }
                }
            }
            if (this.mensajesDetailsPorGrupo.get(idGrupo) != null) {
                for (int i = this.mensajesDetailsPorGrupo.get(idGrupo).size() - 1; i >= 0; i--) {
                    MessageDetailDTO md = this.mensajesDetailsPorGrupo.get(idGrupo).get(i);

                    if (todosLosMensajesPorId.get(md.getIdMessageToMap()) == null) {

                        this.mensajesDetailsPorGrupo.get(idGrupo).remove(i);
                        avisarBorrado(md, false);
                    } else if (md.getUsuarioDestino().getIdUsuario().equals(idUsuario)) {

                        this.mensajesDetailsPorGrupo.get(idGrupo).remove(i);
                        avisarBorrado(md, false);
                    }
                }

            }
        }

    }

    public void removeMessage(String idGrupo, String idMessageToMap) {

        this.getMessageSelected().remove(Observers.message().getMensajesPorId(idMessageToMap));

        {

            todosLosMensajesPorId.remove(idMessageToMap);

            for ( MessageDTO m : this.todosLosMensajesPorGrupo.get(idGrupo) ){
                if (m.getIdMessageToMap().equals(idMessageToMap)){
                    this.todosLosMensajesPorGrupo.get(idGrupo).remove(m);
                }
            }

            for (int i = this.mensajesDetailsPorGrupo.get(idGrupo).size() - 1; i >= 0; i--) {
                MessageDetailDTO md = this.mensajesDetailsPorGrupo.get(idGrupo).get(i);

                if ( md.getIdMessageToMap().equals(idMessageToMap)){

                    this.mensajesDetailsPorGrupo.get(idGrupo).remove(i);
                    avisarBorrado(md, false);
                }
            }

        }
        if (idMessageToMap.equals(SingletonValues.getInstance().getMessageDetailSeleccionado() != null)) {
            if (idMessageToMap.equals(SingletonValues.getInstance().getMessageDetailSeleccionado().getMessage().getIdMessageToMap())) {
                SingletonValues.getInstance().setMessageDetailSeleccionado(null);
            }
        }
    }

    public void removeMessage(ProtocoloDTO protocoloDTO) {
        MessageDTO m = protocoloDTO.getMessageDTO();
        removeMessage(m.getIdGrupo(), m.getIdMessageToMap());
    }

    public void mensajePropio(ProtocoloDTO body) {
    }

    public void writting(ProtocoloDTO p) {

        WrittingDTO w = GsonFormated.get().fromJson(p.getObjectDTO(), WrittingDTO.class);
        Observers.grupo().getGrupoById(w.getIdGrupo()).setOtherAreWritting(true);
        for( ObservadoresMensajes e : o) {
            e.writting(w);
        }
    }

    public void writtingStop(ProtocoloDTO p) {

        WrittingDTO w = GsonFormated.get().fromJson(p.getObjectDTO(), WrittingDTO.class);

        Observers.grupo().getGrupoById(w.getIdGrupo()).setOtherAreWritting(false);
        for( ObservadoresMensajes e : o) {
            e.writtingStop(w);
        }
    }

}
