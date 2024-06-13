package com.privacity.cliente.common.error;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;

import androidx.appcompat.app.AlertDialog;

import com.privacity.cliente.activity.main.MainActivity;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.loading.LoadingActivity;
import com.privacity.cliente.activity.messageresend.MessageResendActivity;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.enumeration.ExceptionReturnCode;

import org.springframework.http.ResponseEntity;

public class ErrorDialog {

    public static void errorDialog(Context context, ResponseEntity<ProtocoloDTO> response, CallbackRest callbackRest){

            if (context instanceof MessageResendActivity) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    callbackRest.onError(response);

                    if (response.getBody().getCodigoRespuesta().trim().equals("401")){

                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
            builder.setTitle("Mensaje de Error");
            ExceptionReturnCode erc = ExceptionReturnCode.getByCode(response.getBody().getCodigoRespuesta());

            String msg = null;
            if ( ExceptionReturnCode.getByCode(response.getBody().getCodigoRespuesta()) == null){
                msg = "Error: " + response.getBody().getCodigoRespuesta();

            }else{
                msg=getDescription(context, response.getBody().getCodigoRespuesta());
            }
             builder.setMessage(msg);
            callbackRest.beforeShowErrorMessage(msg);

            AlertDialog dialog = builder.create();
            try {
                if (!(context instanceof LoadingActivity)){
                    dialog.show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }


    }

    public static void errorDialog(Context context, String error, CallbackRest callbackRest){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callbackRest.onError(null);
            }
        });
        builder.setTitle("Mensaje de Error");

            builder.setMessage("Error: " + error);


        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public static String getDescription(Context context, String code){
        Resources res = context.getResources();
        return res.getString(res.getIdentifier("error_code__" + code, "string", context.getPackageName()));

    }
}
