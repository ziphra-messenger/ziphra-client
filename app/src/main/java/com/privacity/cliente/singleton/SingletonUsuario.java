package com.privacity.cliente.singleton;

import com.privacity.cliente.singleton.interfaces.SingletonReset;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class SingletonUsuario implements SingletonReset {


    private Map<String, String> nicknameForGrupo = new HashMap<String, String>();

    private static SingletonUsuario instance;

        public static SingletonUsuario getInstance() {

            if (instance == null){
                instance = new SingletonUsuario();
            }
            return instance;
        }

        private SingletonUsuario() { }


    @Override
    public void reset() {
        instance = null;
    }
}
