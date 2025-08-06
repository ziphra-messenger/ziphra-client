package com.privacity.cliente.singleton.observers;

import com.privacity.cliente.activity.grupo.RecyclerGrupoAdapter;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.interfaces.ObservadoresUnread;
import com.privacity.common.SingletonReset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObserverUnread implements SingletonReset {
    private final Map<String,CopyOnWriteArrayList<MessageDetail>> mdXgrupo = new HashMap<String,CopyOnWriteArrayList<MessageDetail>>();
    private final Map<String,CopyOnWriteArrayList<ObservadoresUnread>> oXgrupo = new HashMap<String,CopyOnWriteArrayList<ObservadoresUnread>>();

    private static ObserverUnread instance;

    private ObserverUnread() {
    }

    public synchronized static ObserverUnread getInstance(){
        if (instance== null){
            instance = new ObserverUnread();
        }

        return instance;
    }
    private synchronized void avisar(String idGrupo) {
        for( ObservadoresUnread e : oXgrupo.get(idGrupo)) {
            e.avisar(idGrupo,  mdXgrupo.get(idGrupo).size());
        }
    }
    public synchronized void addMe(MessageDetail md){

        if (!mdXgrupo.containsKey(md.getIdGrupo()) ){
            mdXgrupo.put(md.getIdGrupo(), new CopyOnWriteArrayList<>());
        }
        if (mdXgrupo.get(md.getIdGrupo()).contains(md))return;
        mdXgrupo.get(md.getIdGrupo()).add(md);
        avisar(md.getIdGrupo());

    }

    public synchronized void removeMe(MessageDetail md){

        if (!mdXgrupo.containsKey(md.getIdGrupo()) ){

            mdXgrupo.put(md.getIdGrupo(), new CopyOnWriteArrayList<>());
            return;
        }

        mdXgrupo.get(md.getIdGrupo()).remove(md);
        avisar(md.getIdGrupo());

    }

    @Override
    public void reset() {

    }

    public int get(String idGrupo) {
        if ( mdXgrupo.get(idGrupo) == null) return 0;
        return  mdXgrupo.get(idGrupo).size();
    }

    public synchronized void suscribe(ObservadoresUnread y, String idGrupo) {
        if (!oXgrupo.containsKey(idGrupo)){
            oXgrupo.put(idGrupo, new CopyOnWriteArrayList<>());
        }
        oXgrupo.get(idGrupo).add(y);
    }
}
