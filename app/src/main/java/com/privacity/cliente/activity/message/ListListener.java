package com.privacity.cliente.activity.message;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.avanzado.MessageAvanzado;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;

public class ListListener {

    public static String setListenerReadMoreLess(Message message, RecyclerHolderGeneric rch, String txt, boolean isReply) {
        int lenghtI=250;
        if (isReply){
            lenghtI=50;
        }
        final int lenght = lenghtI;
        if (txt.length() > lenght ){
            String txtCompleto = txt +"";
            txt = txt.substring(0,lenght) + " ...";


                rch.getTvLeermas().setVisibility(View.VISIBLE);
                rch.getTvLeermenos().setVisibility(View.GONE);

            final String txtCompletoFinal= txtCompleto;
            rch.getTvLeermas().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rch.getTvMessageListText().setText(txtCompletoFinal);
                    rch.getTvLeermas().setVisibility(View.GONE);
                    rch.getTvLeermenos().setVisibility(View.VISIBLE);
                }
            });


            rch.getTvLeermenos().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rch.getTvMessageListText().setText(txtCompletoFinal.substring(0,lenght) + " ...");
                    rch.getTvLeermenos().setVisibility(View.GONE);
                    rch.getTvLeermas().setVisibility(View.VISIBLE);
                }
            });

        }else{
            rch.getTvLeermas().setVisibility(View.GONE);
            rch.getTvLeermenos().setVisibility(View.GONE);
        }
        return txt;
    }



    public static void setListenerLockClose(MessageActivity context, RecyclerHolderGeneric rch){

        if ( rch.getBtPersonalEncryptLockClose().getVisibility()  == View.VISIBLE){
            rch.getBtPersonalEncryptLockClose().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSecretKey(context, null);

                }
            });
        }
    }
    public static void setListenerLockOpen(MessageActivity context, RecyclerHolderGeneric rch){

        if ( rch.getBtPersonalEncryptLockOpen().getVisibility()  == View.VISIBLE){
            rch.getBtPersonalEncryptLockOpen().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSecretKeyErase(context);

                }
            });
        }
    }
    public static void dialogSecretKeyErase(MessageActivity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //context.getMessageAvanzado().getTvMessageSecretKey().setText("");

            }
        });

        builder.setNegativeButton(context.getString(R.string.general__cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //context.tvMessageSecretKey.setText(password.getText().toString());

            }
        });
        builder.setTitle(context.getString(R.string.message_activity__extra_encrypt__delete__question));





        AlertDialog dialog = builder.create();

        dialog.show();
    }
    public static void dialogSecretKey(MessageActivity context, ActionMessageEncryptKeyI actionMessageEncryptKeyI){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText password = new EditText(context);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //context.getMessageAvanzado().getTvMessageSecretKey().setText(password.getText().toString());

                if (actionMessageEncryptKeyI != null){
                    actionMessageEncryptKeyI.action();
                }else{
/*                    MessageAvanzado.aplicarExtraAES(
                            SingletonValues.getInstance().getGrupoSeleccionado(),
                            context ,
                            context.getMessageAvanzado().getTvMessageSecretKey(),
                            null,true,null);*/
                }


            }
        });

        builder.setNegativeButton(R.string.close, (dialog, which) -> {});

        builder.setTitle(context.getString(R.string.message_activity__extra_encrypt__add_key));




        AlertDialog dialog = builder.create();

        dialog.show();


    }


}
