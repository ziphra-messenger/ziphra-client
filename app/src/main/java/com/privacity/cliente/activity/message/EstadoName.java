package com.privacity.cliente.activity.message;

import android.app.Activity;

import com.privacity.cliente.R;
import com.privacity.common.enumeration.MessageState;

public class EstadoName {

    public static String getNameToShow(Activity activity, String e){

        if (e.equals(MessageState.DESTINY_READ.name())){
            return activity.getString(R.string.general__message_status__destiny_readed);
        }
        if (e.equals(MessageState.DESTINY_DELIVERED.name())){
            return activity.getString(R.string.general__message_status__destiny_delivered);
        }

        if (e.equals(MessageState.DESTINY_SERVER.name())){
            return activity.getString(R.string.general__message_status__destiny_server);
        }

        if (e.equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND.name())){
            return activity.getString(R.string.general__message_status__my_message_error_not_send);
        }

        if (e.equals(MessageState.MY_MESSAGE_SENDING.name())){
            return activity.getString(R.string.general__message_status__my_message_sending);
        }
        if (e.equals(MessageState.MY_MESSAGE_SENT.name())){
            return activity.getString(R.string.general__message_status__my_message_sent);
        }
        return null;

    }
}
