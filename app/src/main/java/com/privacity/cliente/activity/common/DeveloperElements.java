package com.privacity.cliente.activity.common;

import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.main.MainActivi2ty;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.singleton.impl.SingletonServer;

public class DeveloperElements {

    private static void setListener(MainActivi2ty activity, Button button, SecureFieldAndEye username, SecureFieldAndEye password, int visibility) {
        button.setVisibility(visibility);

        SingletonServer.getInstance().setVisibility(button).setOnClickListener(v -> {
            username.getField().setText(button.getText());
            password.getField().setText(button.getText());
            try {
                activity.callLoginRest(username.getField().getText().toString(), password.getField().getText().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
        public static void developerLogin(MainActivi2ty activity, SecureFieldAndEye username, SecureFieldAndEye password) {
            View container = activity.findViewById(R.id.developer__main_login__container);
            SingletonServer.getInstance().setVisibility(container);
            int visibility = View.VISIBLE;
            if (!SingletonServer.getInstance().isDeveloper()){

                visibility = View.GONE;
            }

            username.getField().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.getField().setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_1) ),username,password, visibility);
            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_2) ),username,password, visibility);
            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_3) ),username,password, visibility);

            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_4) ),username,password, visibility);
            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_5) ),username,password, visibility);
            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_6) ),username,password, visibility);

            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_7) ),username,password, visibility);
            setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_8) ),username,password, visibility);
         //   setListener(activity, ((Button) activity.findViewById(R.id.developer__main_login__button_9) ),username,password);



        }
}
