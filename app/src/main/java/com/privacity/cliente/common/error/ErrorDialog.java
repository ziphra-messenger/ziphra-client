package com.privacity.cliente.common.error;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;

import androidx.appcompat.app.AlertDialog;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.loading.LoadingActivity;
import com.privacity.cliente.activity.main.MainActivi2ty;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.frame.error.ErrorView;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.cliente.model.dto.Protocolo;

import org.springframework.http.ResponseEntity;

public class ErrorDialog {

    public static void errorDialog(Activity context, ResponseEntity<Protocolo> response, CallbackRest callbackRest){
        if (SingletonSessionClosing.getInstance().isClosing())return;
        ErrorPojo pojo = new ErrorPojo();
        if (response.getBody() != null  && response.getBody().getObjectDTO() != null){
            pojo= UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), ErrorPojo.class);

        }            pojo.setErrorCode(response.getBody().getCodigoRespuesta())
                .setErrorDescription(getErrorDescription(context,pojo.getErrorCode() )

                ).setRecomendacion(getErrorRecomendacion(context,pojo.getErrorCode() ));


        ErrorView g = new ErrorView(context);

        g.setCallbackRest(callbackRest);
        g.setResponse(response);

        try {
           // if (context instanceof MessageResendActivity) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(SingletonCurrentActivity.getInstance().get());

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    callbackRest.onError(response);

                    if (response.getBody().getCodigoRespuesta().trim().equals("401")){

                        Intent intent = new Intent(SingletonCurrentActivity.getInstance().get(), MainActivi2ty.class);
                        context.startActivity(intent);
                    }
                }
            });

            //ExceptionReturnCode erc = ExceptionReturnCode.getByCode(response.getBody().getCodigoRespuesta());



        callbackRest.beforeShowErrorMessage(pojo.toString());



                if (!(context instanceof LoadingActivity)){


                        try {
                            g.show(pojo);
                        } catch (Exception e) {
                            e.printStackTrace();
                           // errorDialog( context, response,  callbackRest);
                        }
                        //callbackRest.onError(response);

                }

            }catch (Exception e){
                e.printStackTrace();
                //errorDialog( context, response,  callbackRest);
            }


    }

    public static void errorDialog(Activity context, String error, CallbackRest callbackRest){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callbackRest.onError(null);
            }
        });
        builder.setTitle(context.getString(R.string.general__error_message));

            builder.setMessage(context.getString(R.string.general__error_ph1, error));


        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public static String getErrorDescriptionWithCode(Context context, String code){
        Resources res = context.getResources();
        try{
            return code + " - " +res.getString(res.getIdentifier("error_code__" + code, "string", context.getPackageName()));
        }catch (Exception e){
            return context.getString(R.string.general__error__no_description, code);
        }
    }
    public static String getErrorDescription(Context context, String code){
        Resources res = context.getResources();
        try{
            return res.getString(res.getIdentifier("error_code__" + code, "string", context.getPackageName()));
        }catch (Exception e){
            return "Error " + code;
        }
    }
    public static String getErrorRecomendacion(Context context, String code){
        Resources res = context.getResources();
        try{
            return res.getString(res.getIdentifier("error_code__recomendacion__" + code, "string", context.getPackageName()));
        }catch (Exception e){
            return null;
        }
    }
}
