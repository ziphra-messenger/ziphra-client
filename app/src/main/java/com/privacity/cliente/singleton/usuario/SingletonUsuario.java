package com.privacity.cliente.singleton.usuario;

import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;
import com.privacity.common.dto.UsuarioDTO;

import lombok.Getter;
import lombok.Setter;

public class SingletonUsuario  implements SingletonReset {

    @Getter
    @Setter
    private String invitationCode;
    @Getter
    @Setter
    private UsuarioDTO usuario;

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
        ToolsUtil.forceGarbageCollector(usuario);
        ToolsUtil.forceGarbageCollector(invitationCode);
        ToolsUtil.forceGarbageCollector(instance);
    }
}
