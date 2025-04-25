package com.privacity.cliente.singleton.observers;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.rest.restcalls.message.MessageChangeState;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonSessionFinish;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.cliente.util.notificacion.Notificacion;
import com.privacity.common.SingletonReset;
import com.privacity.common.adapters.LocalDateAdapter;
import com.privacity.common.dto.MessageDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.WrittingDTO;
import com.privacity.common.enumeration.RulesConfEnum;
import com.privacity.common.enumeration.MessageState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.Getter;
import lombok.Setter;


public class ObserverMessage implements SingletonReset {
    private static final String TAG = "ObserverMessage";

    @Getter
    private final Set<ItemListMessage> messageSelected = new CopyOnWriteArraySet<ItemListMessage>();
    private final List<ObservadoresMensajes> o = new CopyOnWriteArrayList<ObservadoresMensajes>();

    private final Map<String, CopyOnWriteArrayList<MessageDetail>> mensajesDetailsPorGrupo = new HashMap<String, CopyOnWriteArrayList<MessageDetail>>();
    private final Map<String,CopyOnWriteArrayList<Message>> todosLosMensajesPorGrupo = new HashMap<String,CopyOnWriteArrayList<Message>>();
    private final Map<String,Message> todosLosMensajesPorId = new HashMap<String, Message>();

    private final HashMap<String, Integer> estadoMensaje = new HashMap<String, Integer>();
    private HashMap<String,Message> peticiones = new HashMap<String,Message>();
    private static ObserverMessage instance;
    @Getter @Setter
    private boolean messageOnTop=false;

    public MessageDTO[] getTodosLosIdMensajes(){

        MessageDTO[] r = new MessageDTO[todosLosMensajesPorId.size()];

        int c = 0;
        Iterator<String> itr2 = todosLosMensajesPorId.keySet().iterator();
        while (itr2.hasNext()) {
            String key = itr2.next();
            Message m= todosLosMensajesPorId.get(key);
            MessageDTO x = new MessageDTO();
            x.setIdGrupo(m.getIdGrupo());
            x.setIdMessage(m.getIdMessage());

            r[c] =x;
           c ++;
        }
        return r;
    }
    public static ObserverMessage getInstance() {

        if (instance == null){
            instance = new ObserverMessage();
        }
        return instance;

    }

    private  void sortById(List<Message> items)
    {

        items.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {

                if ( o1.isHistorial() && o2.isHistorial() ) {
                        int value = o1.getIdMessage().compareTo(o2.getIdMessage());
                        return value;
                    }

                    if ( !o1.isHistorial() && !o2.isHistorial() ) {
                        int value =  o1.getOrden().compareTo(o2.getOrden());
                        return value;
                    }

                    if (o1.isHistorial() && !o2.isHistorial()) {
                        return -1;
                    }
                    if (!o1.isHistorial() && o2.isHistorial()) {
                        return 1;
                    }
                return 0;
            }
        });
    }



    public int contarEstadoMensajePorGrupo(String idGrupo) {

        try {
            return estadoMensaje.get(idGrupo);
        }catch (NullPointerException e){
            return 0;
        }
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

    public Map<String,CopyOnWriteArrayList<Message>> getTodosLosMensajesPorGrupo() {
        return this.todosLosMensajesPorGrupo;
    }
    public CopyOnWriteArrayList<Message> getTodosLosMensajesPorGrupo(String idGrupo) {
        return this.todosLosMensajesPorGrupo.get(idGrupo);
    }

    @Override
    public void reset() {
        //eliminar en un for cada map y list
        ToolsUtil.forceGarbageCollector(TAG);
        ToolsUtil.forceGarbageCollector(messageSelected);
        peticiones.clear();
        ToolsUtil.forceGarbageCollector(peticiones);
        ToolsUtil.forceGarbageCollector(messageOnTop);
        mensajesDetailsPorGrupo.clear();
        ToolsUtil.forceGarbageCollector(mensajesDetailsPorGrupo);
        todosLosMensajesPorGrupo.clear();
        ToolsUtil.forceGarbageCollector(todosLosMensajesPorGrupo);
        todosLosMensajesPorId.clear();
        ToolsUtil.forceGarbageCollector(todosLosMensajesPorId);
        estadoMensaje.clear();
        ToolsUtil.forceGarbageCollector(estadoMensaje);

        ToolsUtil.forceGarbageCollector(instance);

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

        CopyOnWriteArrayList<MessageDetail> l = this.mensajesDetailsPorGrupo.get(idGrupo);
        int r=0;
        if (l==null)return r;
        for ( MessageDetail md : l){
            if (md.getUsuarioDestino().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())){
                if (md.getEstado().equals(MessageState.DESTINY_DELIVERED)
                        || md.getEstado().equals(MessageState.DESTINY_SERVER)){
                    r++;
                }
            }
        }
        return r;
    }

    public List getMensajesDetailsPorGrupo(String idGrupo){
        if ( !mensajesDetailsPorGrupo.containsKey(idGrupo)){
            mensajesDetailsPorGrupo.put((idGrupo), new CopyOnWriteArrayList<MessageDetail>());
        }

        return this.mensajesDetailsPorGrupo.get(idGrupo);

    }
    public List<MessageDetail> getAllMyMensajesDetailsToChangeStateOnReconnect(){

        List<MessageDetail> r = new ArrayList<MessageDetail>();
        Set<Grupo> gl = ObserverGrupo.getInstance().getMisGrupoList();

        for ( Grupo g : gl) {
            if (!mensajesDetailsPorGrupo.containsKey(g.getIdGrupo())) {
                mensajesDetailsPorGrupo.put((g.getIdGrupo()), new CopyOnWriteArrayList<MessageDetail>());
            }
            CopyOnWriteArrayList<MessageDetail> dl = this.mensajesDetailsPorGrupo.get(g.getIdGrupo());
            for ( MessageDetail d: dl){
                if ( d.isSendChangeState()){
                    r.add(d);
                }
            }
        }
        return r;


    }
    /*
    public void mensajes(MessageDTO[] body) {

        for ( int i = 0 ; i < body.length ; i++){
            if (!todosLosMensajesPorGrupo.containsKey(body[i].getIdGrupo())){
                todosLosMensajesPorGrupo.put(body[i].getIdGrupo(), new CopyOnWriteArrayList<MessageDTO>());
                mensajesDetailsPorGrupo.put(body[i].getIdGrupo(), new CopyOnWriteArrayList<MessageDetail>());
            }
            todosLosMensajesPorGrupo.get(body[i].getIdGrupo()).add(body[i]);
            todosLosMensajesPorId.put(body[i].buildIdMessageToMap(),body[i]);

            for ( int j = 0 ; j < body[i].getMessagesDetail().length ; j++){
                mensajesDetailsPorGrupo.get(body[i].getIdGrupo()).add(body[i].getMessagesDetail()[j]);
            }

        }

    }
*/
    public void emptyList(String idGrupo) {
        SingletonSessionFinish.getInstance().restart();

        if (todosLosMensajesPorGrupo.get(idGrupo) != null){
            for ( Message m : todosLosMensajesPorGrupo.get(idGrupo)){

                todosLosMensajesPorId.remove(m.buildIdMessageToMap());

            }
        }

        todosLosMensajesPorGrupo.put(idGrupo, new CopyOnWriteArrayList<Message>());
        mensajesDetailsPorGrupo.put(idGrupo, new CopyOnWriteArrayList<MessageDetail>());
        avisarEmptyList(idGrupo);
    }

    public void mensaje(Protocolo protocolo, Activity context, MessageDTO finalMedia) {
        mensaje(protocolo,true,context);
    }



    public void mensajeAddItem(Protocolo p , Message mensaje, String asyncId){
        SingletonSessionFinish.getInstance().restart();
        if (!todosLosMensajesPorGrupo.containsKey(mensaje.getIdGrupo())){
            todosLosMensajesPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<Message>());
            mensajesDetailsPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<MessageDetail>());
        }

        todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()).add(mensaje);
        sortById(todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()));
        mensaje.setIdMessage(asyncId);

        mensaje.getMessagesDetail()[0].setIdMessage(asyncId);
        mensajesDetailsPorGrupo.get(mensaje.getIdGrupo()).add(mensaje.getMessagesDetail()[0]);
        if (mensaje.getMedia() != null){
            mensaje.getMedia().setIdMessage(asyncId);
        }
        /*
        if(!mensaje.isAnonimo()){
            mensaje.setUsuarioCreacion(SingletonValues.getInstance().getUsuario());
        }
        */
        todosLosMensajesPorId.put(mensaje.buildIdMessageToMap(), mensaje);
        peticiones.put(asyncId, mensaje);

        //p.setMessage(mensaje);
// esto se podra borrar?

            for (ObservadoresMensajes e : o) {

                e.mensajeAddItem(mensaje, asyncId);
            }


        //avisar(p);

    }

    public void mensajeNuevoWSReply(Protocolo protocolo, boolean avisar, Activity context) {
        mensaje(protocolo,avisar,context,true);
    }

    public void mensajeNuevoWS(Protocolo protocolo, boolean avisar, Activity context) {

        mensaje(protocolo,avisar,context);
    }
    public void mensaje(Protocolo protocolo, boolean avisar, Activity context) {
        mensaje(protocolo,avisar,context,false);
    }
    public synchronized void mensaje(Protocolo protocolo, boolean avisar, Activity context, boolean isReply) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();




        System.out.println("mensaje recibido 1");
       // System.out.println(gson.toJson(protocoloDTO.getMessage()));

        Message messageMapped = protocolo.getMessage();
        messageMapped.setReply(isReply);
        protocolo.setMessage(messageMapped);

        if (protocolo.getAsyncId() != null) {
            Message mpt = peticiones.get(protocolo.getAsyncId());

            borrarMensaje2(mpt.getMessagesDetail()[0], true);
            //ObservatorGrupos.getInstance().avisarCambioUnread(mpt.idGrupo, +1);
            avisar=true;
            String mptId = mpt.buildIdMessageToMap();
            String mptIpDetails = mpt.getMessagesDetail()[0].buildIdMessageDetailToMap();


            mpt.setUsuarioCreacion( (protocolo.getMessage()).getUsuarioCreacion() );
            mpt.setParentResend((protocolo.getMessage()).getParentResend());
         //   mpt.setParentReply((protocoloDTO.getMessage()).getParentReply());
            mpt.setBlockResend((protocolo.getMessage()).isBlockResend());
            mpt.setBlackMessage((protocolo.getMessage()).isBlackMessage());
            mpt.setChangeNicknameToRandom((protocolo.getMessage()).isChangeNicknameToRandom());

            mpt.setIdMessage(protocolo.getMessage().getIdMessage());
            //mpt.setUsuarioCreacion(protocoloDTO.getMessage().getUsuarioCreacion());
            if (protocolo.getMessage().getMedia() != null) {
                mpt.getMedia().setIdMessage(protocolo.getMessage().getIdMessage());
            }
            mpt.setMessagesDetail(protocolo.getMessage().getMessagesDetail());


/*
            MessageDetail[] ms1 = protocoloDTO.getMessage().getMessagesDetail();

            for ( MessageDetail md1 : ms1 ) {

                MessageDetail[] ms2 = ((Message) protocoloDTO.getMessage()).getMessagesDetail();

                for (MessageDetail md2 : ms2) {
                    if (md1.buildIdMessageDetailToMap().equals(md2.buildIdMessageDetailToMap())) {
                        if (!md1.getUsuarioDestino().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())) {
                           if (md1.getEstado().ordinal()< md2.getEstado().ordinal()){
                               md1.setEstado( md2.getEstado());
                           }

                        }
                    }
                }
            }*/
            peticiones.remove(protocolo.getAsyncId());

            protocolo.setMessage(mpt);

        } else if (protocolo.getMessage().isSystemMessage()){
            estadoMensajeAddElement(protocolo.getMessage().getIdGrupo());

            if ( this.todosLosMensajesPorId.containsKey(protocolo.getMessage().buildIdMessageToMap())){
                return;
            }

        }else{

            if ( this.todosLosMensajesPorId.containsKey(protocolo.getMessage().buildIdMessageToMap())){
                //actualizar mensaje data
                this.todosLosMensajesPorId.get(protocolo.getMessage().buildIdMessageToMap()).setMessagesDetail(protocolo.getMessage().getMessagesDetail());
                for (int i = 0; i< this.todosLosMensajesPorId.get(protocolo.getMessage().buildIdMessageToMap()).getMessagesDetail().length; i++)
                {
                    avisarCambioEstado(todosLosMensajesPorId.get(protocolo.getMessage().buildIdMessageToMap()).getMessagesDetail()[i]);
                }
                return;
            }
            estadoMensajeAddElement(protocolo.getMessage().getIdGrupo());

            if (protocolo.getMessage().getText() != null && !protocolo.getMessage().getText().trim().equals("") ){
                String text;
                try {
                    text = Observers.grupo().getGrupoById(protocolo.getMessage().getIdGrupo()).getAESToUse().getAESDecrypt(protocolo.getMessage().getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    text = e.getMessage();
                }

                protocolo.getMessage().setText(text);

            }

            if (protocolo.getMessage().getMedia() != null &&
                    (
                    protocolo.getMessage().getMedia().getData() != null ||
                    protocolo.getMessage().getMedia().getMiniatura() != null
                    )
            ){
                Log.d(TAG, "Tiene media");

                try {
                    if (protocolo.getMessage().getMedia().getData() != null) {
                        Log.d(TAG, "procesando data ");
                        byte[] data;
                        data = Observers.grupo().getGrupoById(protocolo.getMessage().getIdGrupo()).getAESToUse().getAESDecryptData(protocolo.getMessage().getMedia().getData());
                        protocolo.getMessage().getMedia().setData(data);
                    }
                    if ( protocolo.getMessage().getMedia().getMiniatura() != null){
                        Log.d(TAG, "procesando data miniatura");
                        byte[] miniatura;
                        miniatura = Observers.grupo().getGrupoById(protocolo.getMessage().getIdGrupo()).getAESToUse().getAESDecryptData(protocolo.getMessage().getMedia().getMiniatura());
                        protocolo.getMessage().getMedia().setMiniatura(miniatura);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    protocolo.getMessage().setMedia(null);
                    protocolo.getMessage().setText("Error desencriptandomedia e="+ e.getMessage());
                }

            }
        }

        Message mensaje = protocolo.getMessage();



       System.out.println("mensaje recibido 2");
        //System.out.println(gson.toJson(mensaje));
        /*
        if (finalMedia != null){
            mensaje.setText(finalMedia.getText());
            if (mensaje.getMedia() != null){
                mensaje.getMedia().setData(finalMedia.getMedia().getData());
            }

        }*/

        System.out.println("mi id usuario");
        System.out.println(Singletons.usuario().getUsuario().getIdUsuario());
        MessageDetail miMensaje=null;

        if (!todosLosMensajesPorGrupo.containsKey(mensaje.getIdGrupo())){
            todosLosMensajesPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<Message>());
            mensajesDetailsPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<MessageDetail>());
        }

       // if ( todosLosMensajesPorId.get(mensaje.getIdMessage() ) != null) {
            todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()).add(mensaje);
            sortById(todosLosMensajesPorGrupo.get(mensaje.getIdGrupo()));
            todosLosMensajesPorId.put(mensaje.buildIdMessageToMap(), mensaje);

            for (int j = 0; j < mensaje.getMessagesDetail().length; j++) {
                MessageDetail nuevo = mensaje.getMessagesDetail()[j];



                if (mensajesDetailsPorGrupo.get(mensaje.getIdGrupo()) == null) {
                    mensajesDetailsPorGrupo.put(mensaje.getIdGrupo(), new CopyOnWriteArrayList<>());
                }
                mensajesDetailsPorGrupo.get(mensaje.getIdGrupo()).add(nuevo);

                if (nuevo.getUsuarioDestino().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())) {
                    System.out.println("miMensaje = nuevo;");
                    miMensaje = nuevo;
                }
            }

            // cambio el estado del mensaje







           cambiarEstadoUso(miMensaje, false, context, mensaje );
        if (avisar) avisar(protocolo);
        //}
    }
/*
    public void borrarMensaje(MessageDetail detail){
        //MessageDTO m = this.getMensajesPorId(detail.getIdMessage());
        CopyOnWriteArrayList<MessageDetail> details = mensajesDetailsPorGrupo.get(detail.getIdGrupo());
        for ( int i = details.size()-1 ; i >=0 ; i--){

            if (details.get(i).buildIdMessageDetailToMap().equals(detail.buildIdMessageDetailToMap())){
                details.remove(i);
            }
        }

        todosLosMensajesPorId.remove(detail.buildIdMessageToMap());
        removeMessage(e)

    }*/
public void cambiarEstadoUso(MessageDetail miMensaje, boolean forzar,Activity context){
     cambiarEstadoUso( miMensaje,  forzar, context, null);
}
    public void cambiarEstadoUso(MessageDetail miMensaje, boolean forzar,Activity context,Message message){
        //if (1==1) return;
        MessageState viejoEstado = miMensaje.getEstado();

        if ((!miMensaje.getEstado().equals(MessageState.MY_MESSAGE_SENDING)) &&
                (!miMensaje.getEstado().equals(MessageState.MY_MESSAGE_SENT)) &&
        (!miMensaje.getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND))) {

            //estadoMensajeAddElement(miMensaje.getIdGrupo());


            boolean reciboBlack = false;
            if (Observers.grupo().getGrupoById(miMensaje.getIdGrupo()).getUserConfDTO() != null) {

                if (Observers.grupo().getGrupoById(miMensaje.getIdGrupo()).getUserConfDTO().getBlackMessageAttachMandatoryReceived() != null) {
                    if (Observers.grupo().getGrupoById(miMensaje.getIdGrupo()).getUserConfDTO().getBlackMessageAttachMandatoryReceived().equals(RulesConfEnum.ON)) {
                        reciboBlack = true;
                    }

                }
            }

            if ((!this.getMensajesPorId(miMensaje.buildIdMessageToMap()).isBlackMessage() &&
                    !this.getMensajesPorId(miMensaje.buildIdMessageToMap()).amITimeMessage() &&
                    !reciboBlack

            ) || forzar) {
                if (SingletonValues.getInstance().getGrupoSeleccionado() != null && messageOnTop) {
                    if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(miMensaje.getIdGrupo())) {
                        miMensaje.setEstado(MessageState.DESTINY_READ);
                        estadoMensajeRestarElement(miMensaje.getIdGrupo());
                        List<MessageDetail> lista = mensajesDetailsPorGrupo.get(miMensaje.getIdGrupo());
                        for (int i = 0; i < lista.size(); i++) {
                            if (miMensaje.buildIdMessageDetailToMap().equals(lista.get(i).buildIdMessageDetailToMap())) {
                                lista.get(i).setEstado(MessageState.DESTINY_READ);

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
            MessageChangeState.mensajeChangeState(miMensaje,context);
            avisarCambioEstado(miMensaje);
        }

        if (miMensaje.getUsuarioDestino().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())) {
            avisarCambioEstado(miMensaje);
        }
    }

    private void cambiarToDestinyDelivered(MessageDetail miMensaje, Message message) {

        if  (miMensaje.getEstado().equals(MessageState.DESTINY_SERVER
        ) && message != null && !message.getUsuarioCreacion().getIdUsuario()
                .equals( Singletons.usuario().getUsuario().getIdUsuario())

        ){

            miMensaje.setEstado(MessageState.DESTINY_DELIVERED);
            //estadoMensajeAddElement(miMensaje.getIdGrupo());
            List<MessageDetail> lista = mensajesDetailsPorGrupo.get(miMensaje.getIdGrupo());
            for ( int i = 0 ; i < lista.size() ; i++){
                if (miMensaje.buildIdMessageDetailToMap().equals(lista.get(i).buildIdMessageDetailToMap())){
                    lista.get(i).setEstado(MessageState.DESTINY_DELIVERED);

                }
            }

            Notificacion.getInstance().notificacion();
        }
    }

    private void avisar(Protocolo protocolo) {
        for( ObservadoresMensajes e : o) {
            e.nuevoMensaje(protocolo);
        }
    }

    private void avisarBorradoMessage(String idMessageToMap) {
        for( ObservadoresMensajes e : o) {
            e.borrarMessage(idMessageToMap);
        }
    }
    private void avisarBorradoMessageDetail(MessageDetail detail, boolean avisarSoloGrupos) {
        for( ObservadoresMensajes e : o) {

            if (e instanceof GrupoActivity) {
                e.borrarMessageDetail(detail);
            }else{

                if (!avisarSoloGrupos){
                    e.borrarMessageDetail(detail);
                }

            }

        }
    }
    private void avisarEmptyList(String idGrupo) {
        for( ObservadoresMensajes e : o) {
            e.emptyList(idGrupo);
        }
    }






    public void mensajeDetailChangeState(Protocolo protocolo) {
        MessageDetail messageDetail = UtilsStringSingleton.getInstance().gson().fromJson(protocolo.getObjectDTO(), MessageDetail.class);
        Message m = this.getMensajesPorId(messageDetail.buildIdMessageToMap());

        if (m != null) {
            if (m.getMessagesDetail() != null) {
                for (int i = 0; i < m.getMessagesDetail().length; i++) {

                    if (messageDetail.getUsuarioDestino() != null  && m.getMessagesDetail()[i].buildIdMessageDetailToMap().equals(messageDetail.buildIdMessageDetailToMap())) {
                        if ( m.getMessagesDetail()[i].getEstado().ordinal() < messageDetail.getEstado().ordinal() ) {
                            m.getMessagesDetail()[i].setEstado(messageDetail.getEstado());

                            avisarCambioEstado(messageDetail);
                        }
                    }
                }
            }
        }else {
            perdidos.put(messageDetail.buildIdMessageDetailToMap(), messageDetail);

        }

        for  (MessageDetail mdx : perdidos.values()){
            if( getMensajesPorId(mdx.buildIdMessageToMap() )!= null){

                for ( MessageDetail ex : getMensajesPorId(mdx.buildIdMessageToMap()).getMessagesDetail()){
                    ex.setEstado(mdx.getEstado());
                    avisarCambioEstado(ex);
                }

            }
        }

    }
    private final Map<String, MessageDetail> perdidos = new HashMap<String, MessageDetail>();
    private void avisarCambioEstado(MessageDetail messageDetail) {
        for( ObservadoresMensajes e : o) {
            e.cambioEstado(messageDetail);
        }
    }

    public void borrarMensaje2(MessageDetail detail,boolean avisarSoloGrupos) {
        todosLosMensajesPorId.remove(detail.buildIdMessageToMap());

        List<MessageDetail> list = mensajesDetailsPorGrupo.get(detail.getIdGrupo());
        for (MessageDetail md : list){
            if (md.buildIdMessageDetailToMap().equals(detail.buildIdMessageDetailToMap())){
                list.remove(md);
            }
        }

        List<Message> listM = todosLosMensajesPorGrupo.get(detail.getIdGrupo());
        for (Message m : listM){
            if (m.buildIdMessageToMap().equals(detail.buildIdMessageToMap())){
                list.remove(m);
            }
        }

        avisarBorradoMessageDetail(detail,avisarSoloGrupos);
    }

    public void removeAllMessageFromUser(Protocolo protocolo) {
//        Grupo grupo = UtilsStringSingleton.getInstance().gson().fromJson(protocoloDTO.getObjectDTO(), Grupo.class);
//        String username = grupo.getUsersDTO()[0].getUsername();
//        removeAllMessageFromUser(grupo.getIdGrupo(), username);
    }
    public void removeAllMessageFromUser(String idGrupo, String idUsuario) {

        {
            if (this.todosLosMensajesPorGrupo.get(idGrupo) != null) {
                for (Message m : this.todosLosMensajesPorGrupo.get(idGrupo)) {
                    if (m.isThisIdUsuarioMessageCreator(idUsuario)) {
                        todosLosMensajesPorId.remove(m.buildIdMessageToMap());
                        this.todosLosMensajesPorGrupo.get(idGrupo).remove(m);
                    }
                }

                for (Message m : this.todosLosMensajesPorGrupo.get(idGrupo)) {
                    if (m.isThisIdUsuarioMessageCreator(idUsuario)) {
                        this.todosLosMensajesPorGrupo.get(idGrupo).remove(m);
                    }
                }
            }
            if (this.mensajesDetailsPorGrupo.get(idGrupo) != null) {
                for (int i = this.mensajesDetailsPorGrupo.get(idGrupo).size() - 1; i >= 0; i--) {
                    MessageDetail md = this.mensajesDetailsPorGrupo.get(idGrupo).get(i);

                    if (todosLosMensajesPorId.get(md.buildIdMessageToMap()) == null) {

                        this.mensajesDetailsPorGrupo.get(idGrupo).remove(i);
                        avisarBorradoMessageDetail(md, false);
                    } else if (md.isThisMessageDetailDestinyToUsuarioId(idUsuario)) {

                        this.mensajesDetailsPorGrupo.get(idGrupo).remove(i);
                        avisarBorradoMessageDetail(md, false);
                    }
                }

            }
        }

    }

    public void removeMessage(String idGrupo, String idMessageToMap) {
        {
            Message md = Observers.message().getMensajesPorId(idMessageToMap);
            if (md != null) {
                md.setDeleted(true);
                for (MessageDetail mdd : md.getMessagesDetail()) {
                    mdd.setDeleted(true);
                }
            }
        }

        this.getMessageSelected().remove(Observers.message().getMensajesPorId(idMessageToMap));


        {

            todosLosMensajesPorId.remove(idMessageToMap);

            for ( Message m : this.todosLosMensajesPorGrupo.get(idGrupo) ){
                if (m.buildIdMessageToMap().equals(idMessageToMap)){
                    this.todosLosMensajesPorGrupo.get(idGrupo).remove(m);
                }
            }

            for (int i = this.mensajesDetailsPorGrupo.get(idGrupo).size() - 1; i >= 0; i--) {
                MessageDetail md = this.mensajesDetailsPorGrupo.get(idGrupo).get(i);

                if ( md.buildIdMessageToMap().equals(idMessageToMap)){

                    this.mensajesDetailsPorGrupo.get(idGrupo).remove(i);
                    avisarBorradoMessageDetail(md, false);
                }
            }

        }
        if (idMessageToMap.equals(SingletonValues.getInstance().getMessageDetailSeleccionado() != null)) {
            if (idMessageToMap.equals(SingletonValues.getInstance().getMessageDetailSeleccionado().getMessage().buildIdMessageToMap())) {
                SingletonValues.getInstance().setMessageDetailSeleccionado(null);
            }
        }
        avisarBorradoMessage(idMessageToMap);
    }

    public void removeMessage(Protocolo protocolo) {
        Message m = protocolo.getMessage();
        removeMessage(m.getIdGrupo(), m.buildIdMessageToMap());

        //AVISAR A GRUPO
    }

    public void mensajePropio(Protocolo body) {
    }

    public void writting(Protocolo p) {

        WrittingDTO w = UtilsStringSingleton.getInstance().gson().fromJson(p.getObjectDTO(), WrittingDTO.class);
        Observers.grupo().getGrupoById(w.getIdGrupo()).setOtherAreWritting(true);
        for( ObservadoresMensajes e : o) {
            e.writting(w);
        }
    }

    public void writtingStop(Protocolo p) {

        WrittingDTO w = UtilsStringSingleton.getInstance().gson().fromJson(p.getObjectDTO(), WrittingDTO.class);

        Observers.grupo().getGrupoById(w.getIdGrupo()).setOtherAreWritting(false);
        for( ObservadoresMensajes e : o) {
            e.writtingStop(w);
        }
    }


}
