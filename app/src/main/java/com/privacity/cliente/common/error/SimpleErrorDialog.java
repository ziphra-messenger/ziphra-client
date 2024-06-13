package com.privacity.cliente.common.error;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RestCalls;
import com.privacity.cliente.enumeration.DeleteForEnum;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.restcalls.grupo.PasswordGrupoValidationCallRest;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.common.dto.GrupoGralConfPasswordDTO;

public class SimpleErrorDialog {

    public static void errorDialog(Context context, String title , String txt){
        errorDialog(context, title , txt, null);
    }
    public static void errorDialog(Context context, String title , String txt, PasswordValidationI acction){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (acction != null) acction.action();
            }
        });
        builder.setTitle(title);
        builder.setMessage(txt);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public interface PasswordValidationI{
        void action();
    }

    public interface PasswordGrupoValidationI{
        void action(Grupo p);
        void onPasswordWrong();
    }

    public static void passwordValidation(Context context, PasswordValidationI i){
        passwordValidation(context,null,i);
    }
    public static void passwordValidation(Context context, String mensaje , PasswordValidationI i){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText password = new EditText(context);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (password.getText().toString().equals(SingletonValues.getInstance().getPassword())){
                    SingletonValues.getInstance().passwordCountDownTimerRestart();
                    Observers.password().passwordSet();

                    SingletonValues.getInstance().getPasswordShortLiveCountDownTimer().restart();
                    i.action();
                }else{
                    Toast.makeText(context,"Password Incorrecto",Toast. LENGTH_SHORT).show();
                }
            }
        });
        builder.setTitle("Ingrese su password para realizar esta accion");

        if (mensaje != null ) builder.setMessage(mensaje);




        AlertDialog dialog = builder.create();

        if ( !SingletonValues.getInstance().getPasswordShortLiveCountDownTimer().isRunning()){
            dialog.show();

        }else{
            i.action();
        }


    }

    public static void passwordValidationObligatorio(Context context, String mensaje , PasswordValidationI i){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText password = new EditText(context);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (password.getText().toString().equals(SingletonValues.getInstance().getPassword())){
                    SingletonValues.getInstance().passwordCountDownTimerRestart();
                    i.action();
                }else{
                    Toast.makeText(context,"Password Incorrecto",Toast. LENGTH_SHORT).show();
                }
            }
        });
        builder.setTitle("Ingrese su password para realizar esta accion");

        if (mensaje != null ) builder.setMessage(mensaje);




        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public static void passwordGrupoValidation(Activity activity, ProgressBar progressBar, Grupo g){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        EditText password = new EditText(activity);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                GrupoDTO dto = new GrupoDTO();
                dto.setIdGrupo(g.getIdGrupo());
                dto.setGralConfDTO(new GrupoGralConfDTO());
                dto.setPassword(new GrupoGralConfPasswordDTO());
                dto.getPassword().setPassword(password.getText().toString());

                try {
                    PasswordGrupoValidationCallRest.call(activity,progressBar,g, dto);
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, "ERROR LOGIN GRUPO", e.getMessage());
                }
            }
        });
        builder.setTitle("Ingrese su password para realizar esta accion");

        AlertDialog dialog = builder.create();
        dialog.show();



    }



    public static void messageDelete(MessageActivity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton("Todos y sus reenvios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                activity.getMessageCustomActionBar().setOnActionBarMain();

            }
        });

        builder.setNegativeButton("Para todos", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                RestCalls.deleteForRest(activity, DeleteForEnum.FOR_EVERYONE);
                activity.getMessageCustomActionBar().setOnActionBarMain();
            }
        });


        builder.setNeutralButton("Para mi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RestCalls.deleteForRest(activity, DeleteForEnum.FOR_ME);
                activity.getMessageCustomActionBar().setOnActionBarMain();
            }
        });
        String titulo="Eliminar mensaje";
        if (Observers.message().getMessageSelected().size()> 1) titulo=titulo+"s";
        builder.setTitle(titulo);

        AlertDialog dialog = builder.create();

        dialog.show();

    }
}