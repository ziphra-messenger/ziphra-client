package com.privacity.cliente.singleton;

import com.privacity.common.SingletonReset;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class SingletonUsuarioNickname implements SingletonReset {


    private final Map<String, String> nicknameForGrupo = new HashMap<String, String>();

    private static SingletonUsuarioNickname instance;

        public static SingletonUsuarioNickname getInstance() {

            if (instance == null){
                instance = new SingletonUsuarioNickname();
            }
            return instance;
        }

        private SingletonUsuarioNickname() { }


    @Override
    public void reset() {
        instance = null;
    }
}
