package com.privacity.cliente.activity.myaccount;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.MyAccountConfDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.dto.request.LoginRequestDTO;

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

        SecureFieldAndEyeUtil.setPasswordMaxLenght(currentPassword);
        SecureFieldAndEyeUtil.setPasswordMaxLenght(newPassword);
        SecureFieldAndEyeUtil.setPasswordMaxLenght(confirmationPassword);

        this.setMenuAcordeon( new MenuAcordeonObject(
                (Button) activity.findViewById(R.id.my_account_password_title),
                (View) activity.findViewById(R.id.my_account_password_content))
        );

        this.activity = activity;

        this.setSave((Button) activity.findViewById(R.id.my_account_password_save));

    }

    public void setListener(){




        SecureFieldAndEyeUtil.listener(currentPassword);
        SecureFieldAndEyeUtil.listener(newPassword);
        SecureFieldAndEyeUtil.listener(confirmationPassword);

        MenuAcordeonUtil.setActionMenu(this.getMenuAcordeon());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, "Mensaje de Error", e.getMessage());
                }

            }
        });
    }
    private void save() throws Exception {

        if (!validationSave()) return;

        ProgressBarUtil.show(activity, activity.progressBar);
        MyAccountConfDTO dto =  buildDTO();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MY_ACCOUNT);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MY_ACCOUNT_SAVE_PASSWORD);
        LoginRequestDTO l = new LoginRequestDTO();
        l.setPassword(EncryptUtil.toHash(newPassword.getField().getText().toString()));
        p.setObjectDTO(GsonFormated.get().toJson(l));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        SingletonValues.getInstance().setPassword(newPassword.getField().getText().toString());
                        Toast.makeText(activity,"Guardado",Toast. LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(activity, activity.progressBar);

                    }

                });


    }

    private boolean validationSave() {
        boolean validationCurrent = SecureFieldAndEyeUtil.passwordValidation(activity, currentPassword);
        boolean validationNew = SecureFieldAndEyeUtil.passwordValidation(activity, newPassword);
        boolean validationConfirmation = false;

        if (validationNew){
            validationConfirmation = SecureFieldAndEyeUtil.passwordCompareNewAndConfirmation(activity, newPassword, confirmationPassword);
        }

        if ( !validationCurrent || !validationNew || !validationConfirmation ){
            return false;
        }

        if (!SecureFieldAndEyeUtil.passwordUserCompare(activity, currentPassword)) return false;
        if (SecureFieldAndEyeUtil.passwordCompareOldAndNew(activity, currentPassword, newPassword)) return false;

        return true;
    }

    private MyAccountConfDTO buildDTO() {
        MyAccountConfDTO dto = new MyAccountConfDTO();


        return dto;
    }


}
