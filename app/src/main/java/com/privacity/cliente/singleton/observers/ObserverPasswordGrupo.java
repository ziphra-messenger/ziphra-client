package com.privacity.cliente.singleton.observers;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ObserverPasswordGrupo implements SingletonReset {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<ObservadoresPasswordGrupo> o;

    private static ObserverPasswordGrupo instance;

    @Override
    public void reset() {
        ToolsUtil.forceGarbageCollector(o);
        ToolsUtil.forceGarbageCollector(instance);
    }

    public static ObserverPasswordGrupo getInstance() {

        if ( instance == null){
            instance = new ObserverPasswordGrupo();
            instance.o = new LinkedHashSet<ObservadoresPasswordGrupo>();
//            ConcurrentHashMap<ObservadoresPasswordGrupo, Integer> certificationCosts = new ConcurrentHashMap<>();
//            instance.o = certificationCosts.newKeySet();
        }
        return instance;
    }
    private ObserverPasswordGrupo() { }

    public void suscribirse( ObservadoresPasswordGrupo n) {
        if (o.contains(n)) return;
        o.add(n);
    }
    public void remove( ObservadoresPasswordGrupo n) {
        o.remove(n);
    }
    public void passwordExpired(Grupo g){
        ObservadoresPasswordGrupo[] op = new ObservadoresPasswordGrupo[o.size()];
        int j = 0;
        for( ObservadoresPasswordGrupo e : o) {
            op[j] = e;
            j++;
        }

        for (int i = op.length-1 ; i >= 0 ; i--){
            op[i].passwordExpired(g);
        }
    }

    public void passwordSet(Grupo g){
        for( ObservadoresPasswordGrupo e : o) {
            e.passwordSet(g);
        }
    }

    public void deleteExtraEncrypt(Grupo g) {
        g.getPassword().setPasswordExtraEncrypt(null);

        for( ObservadoresPasswordGrupo e : o) {
            e.deleteExtraEncrypt(g);
        }
    }

}
