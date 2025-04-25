package com.privacity.cliente.common.error;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RestCalls;
import com.privacity.cliente.enumeration.DeleteForEnum;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.frame.error.ErrorView;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.restcalls.grupo.PasswordGrupoValidationCallRest;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.countdown.SingletonPasswordInMemoryLifeTime;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.common.dto.GrupoGralConfPasswordDTO;

public class SimpleErrorDialog {

    public static void errorDialog(Activity context, String title , String txt){
        errorDialog(context, title , txt, null);
    }
    public static void errorDialog(Activity context, String title , String txt, PasswordValidationI acction){

        ErrorPojo pojo = new ErrorPojo();

        pojo.setRecomendacion(txt);
        pojo.setErrorDescription(title);

        new ErrorView(context).setPasswordValidationI(new PasswordValidationI() {
            @Override
            public void action() {
                if (acction != null) acction.action();
            }
        }).show(pojo);


    }

    public interface PasswordValidationI{
        void action();
    }
    public interface OkI{
        void action();
    }
    public interface CancelI{
        void action();
    }
    public interface PasswordGrupoValidationI{
        void action(Grupo p);
        void onPasswordWrong();
    }

    public static void passwordValidation(Activity activity, PasswordValidationI i){
        passwordValidation(activity,null,i);
    }
    public static void passwordValidation(Activity activity, String mensaje , PasswordValidationI i){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        EditText password = new EditText(activity);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (password.getText().toString().equals(SingletonValues.getInstance().getPassword())){
                    SingletonPasswordInMemoryLifeTime.getInstance().restart();
                    i.action();
                }else{
                    Toast.makeText(activity,activity.getString(R.string.general__alert__validation__password_incorrect),Toast. LENGTH_SHORT).show();
                }
            }
        });
        builder.setTitle(activity.getString(R.string.general__enter_password__for_action));

        if (mensaje != null ) builder.setMessage(mensaje);




        AlertDialog dialog = builder.create();

        if ( !SingletonPasswordInMemoryLifeTime.getInstance().isRunning()){
            dialog.show();

        }else{
            i.action();
        }


    }

    public static void passwordValidationObligatorio(Activity activity, String mensaje , PasswordValidationI i){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        EditText password = new EditText(activity);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (password.getText().toString().equals(SingletonValues.getInstance().getPassword())){
                    SingletonPasswordInMemoryLifeTime.getInstance().restart();
                    i.action();
                }else{
                    Toast.makeText(activity,activity.getString(R.string.general__alert__validation__password_incorrect),Toast. LENGTH_SHORT).show();
                }
            }
        });
        builder.setTitle(activity.getString(R.string.general__enter_password__for_action));


        if (mensaje != null ) builder.setMessage(mensaje);




        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public static void passwordGrupoValidation(Activity activity, ProgressBar progressBar, Grupo g){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        EditText password = new EditText(activity);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.alertcustom, null);
        builder.setView(customLayout);
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
        builder.setTitle(activity.getString(R.string.general__enter_password__for_action));

        AlertDialog dialog = builder.create();
        dialog.show();



    }



    public static void messageDelete(MessageActivity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.alertcustom, null);
        builder.setView(customLayout);
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

    public static void confirmAction(Activity activity, OkI ok, CancelI cancelI){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.alertcustom, null);
        builder.setView(customLayout);
        builder.setPositiveButton(activity.getString(R.string.general__confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ok.action();


            }
        });

        builder.setNegativeButton(activity.getString(R.string.general__cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancelI.action();

            }
        });


        builder.setTitle(activity.getString(R.string.general__are_u_sure));

        AlertDialog dialog = builder.create();

        dialog.show();

    }
}