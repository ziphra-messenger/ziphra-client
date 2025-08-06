package com.privacity.cliente.singleton.observers;

import com.privacity.cliente.singleton.interfaces.ObservadoresPassword;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ObserverPassword implements SingletonReset {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<ObservadoresPassword> o;

    private static ObserverPassword instance;

    @Override
    public void reset() {
        ToolsUtil.forceGarbageCollector(o);
        ToolsUtil.forceGarbageCollector(instance);

    }


    public static ObserverPassword getInstance() {

        if ( instance == null){
            instance = new ObserverPassword();
            instance.o = new LinkedHashSet<ObservadoresPassword>();
//            ConcurrentHashMap<ObservadoresPassword, Integer> certificationCosts = new ConcurrentHashMap<>();
//            instance.o = certificationCosts.newKeySet();
        }
        return instance;
    }
    private ObserverPassword() { }

    public void suscribirse( ObservadoresPassword n) {
        if (o.contains(n)) return;
        o.add(n);
    }
    public void remove( ObservadoresPassword n) {
        o.remove(n);
    }
    public void passwordExpired(){
        ObservadoresPassword[] op = new ObservadoresPassword[o.size()];
        int j = 0;
        for( ObservadoresPassword e : o) {
            op[j] = e;
            j++;
        }

        for (int i = op.length-1 ; i >= 0 ; i--){
            op[i].passwordExpired();
        }
    }

    public void passwordSet(){
        for( ObservadoresPassword e : o) {
            e.passwordSet();
        }
    }
}
