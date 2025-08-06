package com.privacity.cliente.model.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.common.dto.RequestIdDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Protocolo implements Serializable {



    /**
     *
     */
    private static final long serialVersionUID = 1468646917559489843L;

    public Protocolo(ProtocoloComponentsEnum component, ProtocoloActionsEnum action) {
        super();
        this.component = component;
        this.action = action;
    }

    private ProtocoloComponentsEnum component;
    private ProtocoloActionsEnum action;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String asyncId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mensajeRespuesta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String codigoRespuesta;


    private RequestIdDTO requestIdDTO;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Message message;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String objectDTO;

     public com.privacity.common.dto.ProtocoloDTO convert(){

         return (com.privacity.common.dto.ProtocoloDTO) UtilsStringSingleton.getInstance().clon(com.privacity.common.dto.ProtocoloDTO.class, this);

     }
    public static Protocolo convert(com.privacity.common.dto.ProtocoloDTO p){

        return (Protocolo) UtilsStringSingleton.getInstance().clon(Protocolo.class, p);

    }
}