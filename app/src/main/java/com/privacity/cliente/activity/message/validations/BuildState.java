package com.privacity.cliente.activity.message.validations;

import android.graphics.Color;

import com.privacity.cliente.activity.message.EstadoName;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;
import com.privacity.common.enumeration.MessageState;

import org.jetbrains.annotations.NotNull;

public class BuildState {

    public static void buildStatus(MessageActivity messageActivity, ItemListMessage item, RecyclerHolderGeneric rch, boolean isReply, boolean grupoDeDos) {
        rch.getTvState().setTextSize(14f);
        if (!item.getMessage().isSystemMessage()
                && !isReply
        ) {

            if (!item.getMessageDetail().getEstado().equals(MessageState.MY_MESSAGE_SENDING) &&
                    !item.getMessageDetail().getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)) {
                rch.getTvState().setBackground(null);
                String estado = getEstadoString(messageActivity, item, grupoDeDos);
                if (estado.equals("✓✓✓") ) {
                    if ((!item.getMessageDetail().isHideRead())) {
                        rch.getTvState().setTextColor(Color.BLUE);
                    }else{
                        rch.getTvState().setTextColor(Color.parseColor("#2A2A6FB8"));
                    }
                } else {
                    rch.getTvState().setTextColor(Color.BLACK);

                }
                rch.getTvState().setText(estado);
            } else {
                if (item.getMessageDetail().getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)){
                    rch.getTvState().setTextColor(Color.RED);
                    rch.getTvState().setText("❌");
                }else {
                    rch.getTvState().setTextColor(Color.BLACK);
                    rch.getTvState().setTextSize(24f);
                    rch.getTvState().setText("\uD83D\uDCE8");
                }
            }
        }else{
            rch.getTvState().setText("");

        }
    }
    @NotNull
    private static String getEstadoString(MessageActivity messageActivity, ItemListMessage item, boolean grupoDeDos) {
        int enviado = 0;
        int enviando = 0;
        int esperando = 0;
        int recibido = 0;
        int leido = 0;

        for (int i = 0; i < item.getMessage().getMessagesDetail().length; i++) {
            if (item.getMessage().getMessagesDetail()[i].getEstado().equals(MessageState.MY_MESSAGE_SENT)) {
                enviado++;
            } else if (item.getMessage().getMessagesDetail()[i].getEstado().equals(MessageState.MY_MESSAGE_SENDING)) {
                enviando++;
            } else if (item.getMessage().getMessagesDetail()[i].getEstado().equals(MessageState.DESTINY_SERVER)) {
                esperando++;
            } else if (item.getMessage().getMessagesDetail()[i].getEstado().equals(MessageState.DESTINY_DELIVERED)) {
                recibido++;
            } else if (item.getMessage().getMessagesDetail()[i].getEstado().equals(MessageState.DESTINY_READ)) {
                leido++;
            }
        }

        String estado;
        if ( esperando == 0 && recibido == 0 ){
            //estado = EstadoName.getNameToShow(item.getMessageDetail().getEstado()) + " ✓✓✓";

            estado = "✓✓✓";

        }else{


            if ( esperando == 0 && recibido == 0 && leido==0 ){
                estado = EstadoName.getNameToShow(messageActivity, item.getMessageDetail().getEstado().name());
            }else{
                String esperandoString;
                String recibidoString;
                String leidoString;
                if (grupoDeDos){
                    esperandoString = (esperando > 0) ? "✓" : "";
                    recibidoString = (recibido > 0) ? "✓✓" : "";
                    leidoString = (leido > 0) ? "✓✓✓" : "";
                    //estado = EstadoName.getNameToShow(item.getMessageDetail().getEstado()) + " Estado:"+ esperandoString + recibidoString + leidoString;

                }else{
                    esperandoString = (esperando > 0) ? " (" + esperando + " ✓)" : "";
                    recibidoString = (recibido > 0) ? " (" + recibido + " ✓✓)" : "";
                    leidoString = (leido > 0) ? " (" + leido + " ✓✓✓)" : "";
                    //estado = EstadoName.getNameToShow(item.getMessageDetail().getEstado()) + " Estado:"+ esperandoString + recibidoString + leidoString;

                }
                estado = esperandoString + recibidoString + leidoString;
            }

        }
        if (estado== null) estado = " ";
        return estado;
    }
}
