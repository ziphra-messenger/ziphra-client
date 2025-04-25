package com.privacity.cliente.activity.myaccount;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.main.MainActivi2ty;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.dto.MyAccountConfDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class MyAccountLoginSkipFrame {

    private final ProgressBar progressBar;

    private final Button save;
    private final Button defecto;
    private final Switch enabled;

    private MyAccountActivity activity;

    private MenuAcordeonObject menuAcordeon;

    public MyAccountLoginSkipFrame(MyAccountActivity activity, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.setMenuAcordeon( new MenuAcordeonObject(
                (GetButtonReady.get(activity, R.id.my_account_login_skip_title)),
                (View) activity.findViewById(R.id.my_account_login_skip_content))
        );

        this.activity = activity;

        enabled = (Switch) activity.findViewById(R.id.my_account_login_skip_enabled);
        save = (GetButtonReady.get(activity, R.id.my_account_login_skip_save,getSaveOnClickListener()));
        defecto =(GetButtonReady.get(activity, R.id.my_account_login_skip_default));

    }
    public ViewCallbackActionInterface getSaveOnClickListener(){
        return new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                try {
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        };
    }
    public void setListener(){

        MenuAcordeonUtil.setActionMenu(menuAcordeon);

        enabled.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                    save.setEnabled(true);
                return false;
            }
        });





        defecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    enabled.setChecked(false);
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        });
    }



    private void save() throws Exception {
        save.setEnabled(false);
        if (!validationSave()) return;

        ProgressBarUtil.show(activity, activity.progressBar);
        boolean dto =  enabled.isChecked();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_LOGIN_SKIP);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        SingletonValues.getInstance().getMyAccountConfDTO().setLoginSkip(dto);
                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();

                        if (dto){
                                SharedPreferencesUtil.saveSharedPreferencesUserPass(activity,
                                SingletonValues.getInstance().getUsernameNoHash(),
                                SingletonValues.getInstance().getPasswordNoHash());
                        }else{
                            SharedPreferencesUtil.deleteSharedPreferencesUserPass(activity);
                        }
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

    public static void saveDisabled(Activity activity) throws Exception {

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_LOGIN_SKIP);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(false));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        SingletonValues.getInstance().getMyAccountConfDTO().setLoginSkip(false);
                        SharedPreferencesUtil.deleteSharedPreferencesUserPass(activity);
                        activity.finish();
                        Intent i = new Intent(activity, MainActivi2ty.class);
                        activity.startActivity(i);

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES);
                        activity.sendBroadcast(intent);
                        activity.finish();
                        Intent i = new Intent(activity, MainActivi2ty.class);
                        activity.startActivity(i);

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {


                    }

                });


    }

    private boolean validationSave() {

        if ( enabled.isChecked() == SingletonValues.getInstance().getMyAccountConfDTO().isLoginSkip()){
            Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private MyAccountConfDTO buildDTO() {
        MyAccountConfDTO dto = new MyAccountConfDTO();


        return dto;
    }


    public void loadValues() {
        enabled.setChecked(SingletonValues.getInstance().getMyAccountConfDTO().isLoginSkip());
    }
}
