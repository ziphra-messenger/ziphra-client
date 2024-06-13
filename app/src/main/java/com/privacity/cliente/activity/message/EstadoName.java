package com.privacity.cliente.activity.message;

import com.privacity.common.enumeration.MessageState;

public class EstadoName {

    public static String getNameToShow(String e){

        if (e.equals(MessageState.DESTINY_READED.name())){
            return "Leido";
        }
        if (e.equals(MessageState.DESTINY_DELIVERED.name())){
            return "Entregado";
        }

        if (e.equals(MessageState.DESTINY_SERVER.name())){
            return "Servidor";
        }

        if (e.equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND.name())){
            return "Error";
        }

        if (e.equals(MessageState.MY_MESSAGE_SENDING.name())){
            return "Enviando";
        }
        if (e.equals(MessageState.MY_MESSAGE_SENT.name())){
            return "Enviado";
        }
        return "estado desconocido";

    }
}
