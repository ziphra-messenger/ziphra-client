package com.privacity.cliente.includes;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.singleton.countdown.SingletonPasswordInMemoryLifeTime;
import com.privacity.common.config.ConstantValidation;

public class SecureFieldAndEyeUtil {

    public static final int CONSTANT_PASSWORD_SHOWING_TIME = 15000;

    public static void setPasswordMaxLenght(SecureFieldAndEye s){
            s.getField().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_PASSWORD_MAX_LENGTH)});

    }

    public static boolean passwordValidation(Activity activity, SecureFieldAndEye s){
        EditText f = s.getField();
        if (f.getText().toString().equals("")){
            f.setError(activity.getResources().getString(R.string.registro_validation_password1_empty));
            return false;
        }else if (f.getText().toString().length() < ConstantValidation.USER_PASSWORD_MIN_LENGTH){
            f.setError(activity.getResources().getString(R.string.registro_validation_password1_too_short));
            return false;
        }else if (f.getText().toString().length() > ConstantValidation.USER_PASSWORD_MAX_LENGTH){
            f.setError(activity.getResources().getString(R.string.registro_validation_password1_too_long));
            return false;

        }
        return true;
    }

    public static boolean passwordValidationGrupo(Activity activity, SecureFieldAndEye s){
        EditText f = s.getField();
        if (f.getText().toString().equals("")){
            f.setError(activity.getResources().getString(R.string.registro_validation_password1_empty));
            return false;
        }
        return true;
    }
    public static void listener(SecureFieldAndEye s) {
        listener (s, true);
    }
    public static void listener(SecureFieldAndEye s, boolean time) {

        EditText secureField =s.getField();
        ImageButton eyeShow = s.getEyeShow();
        ImageButton eyeHide = s.getEyeHide();

        eyeHide.setVisibility(View.GONE);
        eyeShow.setVisibility(View.VISIBLE);

        eyeShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eyeShow.setVisibility(View.GONE);
                eyeHide.setVisibility(View.VISIBLE);
                secureField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                if (time){
                    new CountDownTimer(CONSTANT_PASSWORD_SHOWING_TIME, CONSTANT_PASSWORD_SHOWING_TIME) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            secureField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            eyeHide.setVisibility(View.GONE);
                            eyeShow.setVisibility(View.VISIBLE);
                        }
                    }.start();
                }
            }

        });

        eyeHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secureField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eyeHide.setVisibility(View.GONE);
                eyeShow.setVisibility(View.VISIBLE);
            }

        });
    }

    private static boolean passwordCompare(SecureFieldAndEye newPassword,
                                           SecureFieldAndEye confirmationPassword
                                           ) {
        if (!newPassword.getField().getText().toString().equals(confirmationPassword.getField().getText().toString())) {
            return false;
        }

        return true;

    }

    public static boolean passwordCompareNewAndConfirmation(Activity activity, SecureFieldAndEye newPassword, SecureFieldAndEye confirmationPassword) {
        if (!passwordCompare(newPassword, confirmationPassword )){
            confirmationPassword.getField().setError(activity.getResources().getString(R.string.registro_validation_password2));
            return false;
        }
        return true;

    }

    public static boolean passwordUserCompare(Activity activity, SecureFieldAndEye currentPassword) {
        if (currentPassword.getField().getText().toString().equals(SingletonValues.getInstance().getPassword())){
            SingletonPasswordInMemoryLifeTime.getInstance().restart();
            return true;
        }else{
            currentPassword.getField().setError(activity.getResources().getString(R.string.password_incorrect));
            return false;
        }
    }

    public static boolean passwordCompareOldAndNew(Activity activity, SecureFieldAndEye currentPassword, SecureFieldAndEye newPassword) {


        if (passwordCompare(newPassword, currentPassword )){
            newPassword.getField().setError(activity.getResources().getString(R.string.password_old_new_equals));
            return true;
        }
        return false;
    }
}
