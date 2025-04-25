package com.privacity.cliente.activity.myaccount;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.ValidarUsuarioPassword;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.ChangePasswordRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class MyAccountPasswordFrame {

    private Button save;

    private SecureFieldAndEye currentPassword;
    private SecureFieldAndEye newPassword;
    private SecureFieldAndEye confirmationPassword;

    private MyAccountActivity activity;

    private MenuAcordeonObject menuAcordeon;

    public MyAccountPasswordFrame(MyAccountActivity activity) {

        this.setCurrentPassword(new SecureFieldAndEye(null,
                (EditText) activity.findViewById(R.id.password_eye_field),
                (ImageButton) activity.findViewById(R.id.password_eye_show),
                (ImageButton) activity.findViewById(R.id.password_eye_hide)
        ));

        this.setNewPassword(new SecureFieldAndEye(null,
                (EditText) activity.findViewById(R.id.password_eye_change_field),
                (ImageButton) activity.findViewById(R.id.password_eye_change_show),
                (ImageButton) activity.findViewById(R.id.password_eye_change_hide)
        ));

        this.setConfirmationPassword(new SecureFieldAndEye(null,
                (EditText) activity.findViewById(R.id.password_eye_confirmation_field),
                (ImageButton) activity.findViewById(R.id.password_eye_confirmation_show),
                (ImageButton) activity.findViewById(R.id.password_eye_confirmation_hide)
        ));

//        SecureFieldAndEyeUtil.setPasswordMaxLenght(currentPassword);
//        SecureFieldAndEyeUtil.setPasswordMaxLenght(newPassword);
//        SecureFieldAndEyeUtil.setPasswordMaxLenght(confirmationPassword);

        this.setMenuAcordeon( new MenuAcordeonObject(
                GetButtonReady.get(activity, R.id.my_account_password_title),
                (View) activity.findViewById(R.id.my_account_password_content))
        );

        this.activity = activity;
        this.setSave(GetButtonReady.get(activity, R.id.my_account_password_save,v -> {
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
                SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
            }

        }));
    }

    public void setListener(){




        SecureFieldAndEyeUtil.listener(currentPassword);
        SecureFieldAndEyeUtil.listener(newPassword);
        SecureFieldAndEyeUtil.listener(confirmationPassword);

        MenuAcordeonUtil.setActionMenu(this.getMenuAcordeon());


        confirmationPassword.getField().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                validationSave();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        newPassword.getField().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                validationSave();


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


    }
    private void save() throws Exception {

        if (!validationSave()) return;
        if (!SecureFieldAndEyeUtil.passwordUserCompare(activity, currentPassword,activity.findViewById(R.id.tv_my_account_password_actual_validacion))) return;
        if (SecureFieldAndEyeUtil.passwordCompareOldAndNew(activity, currentPassword, newPassword,activity.findViewById(R.id.tv_my_account_password_actual_validacion))) return;


        ProgressBarUtil.show(activity, activity.progressBar);
        ChangePasswordRequestDTO dto =  buildDTO();
        dto.setNewPassword(EncryptUtil.toHash(newPassword.getField().getText().toString()));
        dto.setNewPasswordConfirm(EncryptUtil.toHash(confirmationPassword.getField().getText().toString()));
        dto.setOldPassword(EncryptUtil.toHash(currentPassword.getField().getText().toString()));
        dto.setUsername(SingletonValues.getInstance().getUsernameHash());
        currentPassword.getField().setText("");
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_PASSWORD);
      //  LoginRequestDTO l = new LoginRequestDTO();
       // l.setPassword(EncryptUtil.toHash(newPassword.getField().getText().toString()));
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        SingletonValues.getInstance().setPassword(newPassword.getField().getText().toString());
                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();
                        newPassword.getField().setText("");
                        currentPassword.getField().setText("");
                        confirmationPassword.getField().setText("");
                        ((TextView)activity.findViewById(R.id.tv_my_account_password_actual_validacion)).setText(R.string.register_activity__requerido);
                        ((TextView)activity.findViewById(R.id.tv_my_account_password1_validacion)).setText(R.string.register_activity__requerido);
                        ((TextView)activity.findViewById(R.id.tv_my_account_password2_validacion)).setText(R.string.register_activity__requerido);
                        ((TextView)activity.findViewById(R.id.tv_my_account_password_actual_validacion)).setVisibility(View.GONE);
                        ((TextView)activity.findViewById(R.id.tv_my_account_password1_validacion)).setVisibility(View.GONE);
                        ((TextView)activity.findViewById(R.id.tv_my_account_password2_validacion)).setVisibility(View.GONE);
                        save.setEnabled(false);
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(activity, activity.progressBar);

                    }

                });


    }

    private boolean validationSave() {
        //boolean validationCurrent = true;//SecureFieldAndEyeUtil.passwordValidation(activity, currentPassword);
        boolean r= ValidarUsuarioPassword.validatePassword(
                activity,activity.findViewById(R.id.tv_my_account_password1_validacion), SingletonServerConfiguration.getInstance().getSystemGralConf().getPasswordConfig().getPasswordUsuarioRegistration()
                , SingletonValues.getInstance().getUsernameNoHash()
                , newPassword.getField().getText().toString(), confirmationPassword.getField().getText().toString(),activity.findViewById(R.id.tv_my_account_password2_validacion)
        , Singletons.usuario().getUsuario().getNickname(),false);
        //boolean validationConfirmation = false;

        save.setEnabled(r);

        return r;


/*        if ( !validationCurrent || !validationNew ){
            return false;
        }*/


        //return !
    }

    private ChangePasswordRequestDTO buildDTO() {
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO();


        return dto;
    }


}
