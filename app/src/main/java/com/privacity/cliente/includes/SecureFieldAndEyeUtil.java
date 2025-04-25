package com.privacity.cliente.includes;

import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.countdown.SingletonPasswordInMemoryLifeTime;

public class SecureFieldAndEyeUtil {
    private static final String TAG = "SecureFieldAndEyeUtil";
    public static final int CONSTANT_PASSWORD_SHOWING_TIME = 15000;



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
        listener (s, true,null);
    }
    public static void listener(SecureFieldAndEye s, boolean time,CallBackSecureField cb) {

        try {
            EditText secureField = s.getField();
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
                    if (cb != null)cb.showAction();
                    if (time) {
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
                    if (cb != null)cb.hideAction();
                }

            });
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e(TAG, "listener: ", e );

        }
    }

    private static boolean passwordCompare(SecureFieldAndEye newPassword,
                                           SecureFieldAndEye confirmationPassword
                                           ) {
        return newPassword.getField().getText().toString().equals(confirmationPassword.getField().getText().toString());

    }

    public static boolean passwordCompareNewAndConfirmation(Activity activity, SecureFieldAndEye newPassword, SecureFieldAndEye confirmationPassword) {
        if (!passwordCompare(newPassword, confirmationPassword )){
            confirmationPassword.getField().setError(activity.getResources().getString(R.string.registro_validation_password2));
            return false;
        }
        return true;

    }

    public static boolean passwordUserCompare(Activity activity, SecureFieldAndEye currentPassword, TextView validate) {
        if (currentPassword.getField().getText().toString().equals(SingletonValues.getInstance().getPassword())){
            SingletonPasswordInMemoryLifeTime.getInstance().restart();
            validate.setTextColor(Color.BLACK);
            validate.setText("");
            validate.setVisibility(View.GONE);
            SingletonValues.getInstance().getPasswordReintentosReset();
            return true;
        }else{

            validate.setTextColor(Color.RED);
            validate.setVisibility(View.VISIBLE);
            validate.setText(activity.getResources().getString(R.string.general__alert__validation__password_incorrect));
            if(!currentPassword.getField().getText().toString().equals("")) {
                SingletonValues.getInstance().getPasswordReintentosAdd(activity);
            }
            return false;
        }
    }

    public static boolean passwordUserCompare(Activity activity, SecureFieldAndEye currentPassword) {
        if (currentPassword.getField().getText().toString().equals(SingletonValues.getInstance().getPassword())){
            SingletonPasswordInMemoryLifeTime.getInstance().restart();
            return true;
        }else{
            currentPassword.getField().setError(activity.getResources().getString(R.string.general__alert__validation__password_incorrect));
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

    public static boolean passwordCompareOldAndNew(Activity activity, SecureFieldAndEye currentPassword, SecureFieldAndEye newPassword, TextView validate) {
        if (currentPassword.getField().getText().equals("")){
            validate.setTextColor(Color.BLACK);
            validate.setText(R.string.register_activity__requerido);
            return false;
        }
        if (!passwordCompare(newPassword, currentPassword )){

            validate.setTextColor(Color.BLACK);
            validate.setText("");
            return false;
        }else{
            SingletonPasswordInMemoryLifeTime.getInstance().restart();
            validate.setTextColor(Color.RED);
            validate.setVisibility(View.VISIBLE);
            validate.setText(activity.getResources().getString(R.string.password_old_new_equals));
            return true;
        }
    }
}
