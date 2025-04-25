package com.privacity.cliente.activity.main;

import android.content.Context;
import android.content.Intent;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.loading.LoadingActivity;
import com.privacity.cliente.common.constants.IntentConstant;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.frame.error.ErrorView;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.AsyncCaller;
import com.privacity.cliente.rest.CallbackActionRest;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.ServerConfRest;
import com.privacity.cliente.singleton.SingletonLoginValues;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.countdown.LoginCountDownTimer;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.reconnect.SingletonReconnect;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.LoginRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

public class CallRestLogin {
    private static void showErrorSinInternet(MainActivi2ty activity) {
        ErrorPojo pojo = new ErrorPojo();
        pojo.setUrl(SingletonServer.getInstance().getAppServer());
        pojo.setRecomendacion(activity.getString(R.string.main_login__alert__sin_internet__detail));
        pojo.setErrorDescription(activity.getString(R.string.main_login__alert__sin_internet__title));

        new ErrorView(activity).show(pojo);
    }
    public static void callLoginRest (MainActivi2ty activity, String user, String password) throws Exception {

        ProgressBarUtil.show(activity, activity.getProgressBar());
        if (!SingletonReconnect.isOnline(activity)){
            showErrorSinInternet(activity);
            ProgressBarUtil.hide(activity, activity.getProgressBar());
            return;
        }


        AsyncCaller t = LoginRest(activity, user, password);
        t.execute();
    }

    @NotNull
    public static AsyncCaller LoginRest(MainActivi2ty activity, String user, String password) {
        return new AsyncCaller(new CallbackActionRest() {
            @Override
            public void onError() {
                ProgressBarUtil.hide(activity, activity.getProgressBar());
                ErrorPojo pojo = new ErrorPojo();
                pojo.setUrl(SingletonServer.getInstance().getAppServer());

                pojo.setErrorDescription(activity.getString(R.string.main_login__alert__servidor_no_responde__title));
                pojo.setRecomendacion(activity.getString(R.string.main_login__alert__servidor_no_responde___detail));
                new ErrorView(activity).show(pojo);


            }

            @Override
            public void onSucess() {
                try {

                    checkServerRunning(activity, user, password);
                } catch (Exception e) {
                    SimpleErrorDialog.errorDialog(activity,
                            activity.getString(R.string.main_login__alert__alert__error_rest__title)
                            , activity.getString(R.string.main_login__alert__alert__error_rest__detail, e.getMessage()));
                }
            }
        }, activity);
    }

    public static void checkServerRunning(MainActivi2ty activity, String user, String password) throws Exception {



        activity.setTimer(new LoginCountDownTimer(activity));
        activity.getTimer().restart();

        ServerConfRest.getTime(activity, loginCallbackRest(activity, user, password), new InnerCallbackRest() {
            @Override
            public void action(Context context) {
                try {
                    callLoginRestInnerCallBack(activity, user, password);
                    ProgressBarUtil.changeState(activity, activity.getProgressBar());
                    activity.getTimer().stop();
                } catch (Exception e) {
                    ProgressBarUtil.changeState(activity, activity.getProgressBar());
                    activity.getTimer().stop();
                    e.printStackTrace();
                }
            }
        });
    }





    //    private boolean checkServerRunning() {
//        try{
//            String url =SingletonServer.getInstance().getAppServer();
//            int timeout = 3000;
//            URL myUrl = new URL(url);
//            URLConnection connection = myUrl.openConnection();
//            connection.setConnectTimeout(timeout);
//            connection.connect();
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public static void callLoginRestInnerCallBack(MainActivi2ty activity, String user, String password) throws Exception {


        String userP;
        String passwordP;
/*        if (activity.isNoValidate() ){
            userP  = SharedPreferencesUtil.getUserPass(activity, SharedPreferencesEnum.USER);
            passwordP = SharedPreferencesUtil.getUserPass(activity, SharedPreferencesEnum.PASSWORD);
        }else{*/
            userP  = user;
            passwordP = password;
//  /      }



        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.AUTH);
        p.setAction(ProtocoloActionsEnum.AUTH_LOGIN);

        LoginRequestDTO t = new LoginRequestDTO();
        t.setUsername(EncryptUtil.toHash(userP));
        t.setPassword(EncryptUtil.toHash(passwordP));

        SingletonValues.getInstance().setUsernameHash(t.getUsername());
        SingletonValues.getInstance().setPasswordHash(t.getPassword());
        SingletonValues.getInstance().setUsernameNoHash(userP);
        SingletonValues.getInstance().setPasswordNoHash(passwordP);
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(t));


        RestExecute.doitPublic(activity, p, loginCallbackRest(activity, userP, passwordP)
                , SingletonLoginValues.getInstance().getAEStoUse(),
                SingletonLoginValues.getInstance().getAEStoSend()
        );
    }

    public static CallbackRest loginCallbackRest(MainActivi2ty activity, String user, String password) {
        CallbackRest r = new CallbackRest() {

            @Override
            public void response(ResponseEntity<Protocolo> response) {
                activity.cleanForm();
                Intent i = new Intent(activity, LoadingActivity.class);

                i.putExtra(IntentConstant.PROTOCOLO_DTO, response.getBody().getObjectDTO());
                i.putExtra(IntentConstant.USERNAME, user);
                i.putExtra(IntentConstant.PASSWORD, password);

                activity.startActivity(i);
                //activity.finish();

            }

            @Override
            public void onError(ResponseEntity<Protocolo> response) {



//                ProgressBarUtil.hide(MainActivity.this, progressBar);

//                  if (noValidate) mainActivityNoPass.setVisibilityError();

//                setContentView(R.layout.activity_main_nopass);
//                try {
//                    callLoginRest(user, password);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

            @Override
            public void beforeShowErrorMessage(String msg) {
                activity.getTimer().stop();
                if (activity.isNoValidate()) activity.getMainActivityNoPass().setVisibilityError();
                ProgressBarUtil.hide(activity, activity.getProgressBar());
            }
        };
        return r;
    }
}
