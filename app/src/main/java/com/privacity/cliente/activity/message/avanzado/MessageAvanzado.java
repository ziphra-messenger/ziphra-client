package com.privacity.cliente.activity.message.avanzado;

import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.privacity.cliente.activity.message.ActionMessageEncryptKeyI;
import com.privacity.cliente.activity.message.ListListener;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.AEStoUseFactory;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.util.ToolsUtil;

public class MessageAvanzado {

    private static CountDownTimer secretKeyCounter=null;

    public static void initListenerAvanzado(MessageActivity messageActivity,
                                            ImageView ibMessageAvanzadoCopy,

                                            Button btMessageAvanzadaAutoGen,
                                            ImageButton ibMessageAvanzadoShowSecretKey,
                                            ImageButton ibMessageAvanzadoHideSecretKey,
                                            ImageButton avanzada,
                                            View viewAvanzada,
                                            Spinner spinnerTime, EditText tvMessageSecretKey) {





        ibMessageAvanzadoCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleErrorDialog.passwordValidation(messageActivity, new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        ToolsUtil.setClipboard(messageActivity, tvMessageSecretKey.getText().toString());
                    }
                });

            }
        });


        EncryptUtil.generateSecretPersonalKeyListener(btMessageAvanzadaAutoGen,tvMessageSecretKey);

        //SecureFieldAndEye extraAes = new SecureFieldAndEye(null, tvMessageSecretKey, ibMessageAvanzadoShowSecretKey, ibMessageAvanzadoHideSecretKey);
        ibMessageAvanzadoShowSecretKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleErrorDialog.passwordValidationObligatorio(messageActivity, "Será visible por 15 segundos" , new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        secretKeyCounter = new CountDownTimer(15000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                ibMessageAvanzadoHideSecretKey.setVisibility(View.VISIBLE);
                                ibMessageAvanzadoShowSecretKey.setVisibility(View.GONE);
                                tvMessageSecretKey.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            }

                            public void onFinish() {
                                tvMessageSecretKey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                ibMessageAvanzadoHideSecretKey.setVisibility(View.GONE);
                                ibMessageAvanzadoShowSecretKey.setVisibility(View.VISIBLE);
                            }
                        };
                        secretKeyCounter.start();
                    }
                });

            }
        });
        ibMessageAvanzadoHideSecretKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        if (secretKeyCounter != null) secretKeyCounter.cancel();

                        tvMessageSecretKey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ibMessageAvanzadoHideSecretKey.setVisibility(View.GONE);
                        ibMessageAvanzadoShowSecretKey.setVisibility(View.VISIBLE);

                };

            }
        );



        avanzada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( viewAvanzada.getVisibility() == View.GONE){
                    viewAvanzada.setVisibility(View.VISIBLE);
                    ;                }else{
                    viewAvanzada.setVisibility(View.GONE);
                }
            }
        });
    }

    public static AEStoUse aplicarExtraAES(Grupo grupo, MessageActivity messageActivity, EditText tvMessageSecretKey, AEStoUse extraAEStoUse, boolean showMessage, ActionMessageEncryptKeyI actionMessageEncryptKeyI){


            if (tvMessageSecretKey.getText().toString().equals("")) {
                ListListener.dialogSecretKey(messageActivity, actionMessageEncryptKeyI);
            } else {
                try {
                    if (tvMessageSecretKey.getText().toString().equals("")) {
                        SimpleErrorDialog.errorDialog(messageActivity, "Extra Encrypt Key", "No puede ser nulo. Debe completar el valor haciendo click en el boton CONFIG");
                        return extraAEStoUse;
                    }

                    if (extraAEStoUse == null) {

                        extraAEStoUse = AEStoUseFactory.getAEStoUseExtra(
                                tvMessageSecretKey.getText().toString(), tvMessageSecretKey.getText().toString());
                        return extraAEStoUse;

                    }
                } catch(Exception e){
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(messageActivity, "Extra Encrypt Key", e.getMessage());
                    return extraAEStoUse;
                }
        }
        return extraAEStoUse;
    }

}
