package com.privacity.cliente.activity.myaccount;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.MyAccountConfDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class MyAccountLoginSkipFrame {

    private final ProgressBar progressBar;
    private final Button reset;
    private final Button save;
    private final Button defecto;
    private final Switch enabled;

    private MyAccountActivity activity;

    private MenuAcordeonObject menuAcordeon;

    public MyAccountLoginSkipFrame(MyAccountActivity activity, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.setMenuAcordeon( new MenuAcordeonObject(
                (Button) activity.findViewById(R.id.my_account_login_skip_title),
                (View) activity.findViewById(R.id.my_account_login_skip_content))
        );

        this.activity = activity;

        enabled = (Switch) activity.findViewById(R.id.my_account_login_skip_enabled);

        reset = (Button) activity.findViewById(R.id.my_account_login_skip_reset);
        save = (Button) activity.findViewById(R.id.my_account_login_skip_save);
        defecto = (Button) activity.findViewById(R.id.my_account_login_skip_default);
    }

    public void setListener(){

        MenuAcordeonUtil.setActionMenu(menuAcordeon);


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

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadValues();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, "Mensaje de Error", e.getMessage());
                }

            }
        });

        defecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    enabled.setChecked(SingletonValues.getInstance().getSystemGralConf().getMyAccountConf().isLoginSkip());
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
        boolean dto =  enabled.isChecked();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MY_ACCOUNT);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MY_ACCOUNT_SAVE_LOGIN_SKIP);

        p.setObjectDTO(GsonFormated.get().toJson(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        SingletonValues.getInstance().getMyAccountConfDTO().setLoginSkip(dto);
                        Toast.makeText(activity,"Guardado",Toast. LENGTH_SHORT).show();

                        if (dto){
                                SharedPreferencesUtil.saveSharedPreferencesUserPass(activity,
                                SingletonValues.getInstance().getUsernameNoHash(),
                                SingletonValues.getInstance().getPasswordNoHash());
                        }else{
                            SharedPreferencesUtil.deleteSharedPreferencesUserPass(activity);
                        }
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

/*        if ( enabled.isChecked() == SingletonValues.getInstance().getMyAccountConfDTO().isLoginSkip()){
            Toast.makeText(activity,"Guardado",Toast. LENGTH_SHORT).show();
            return false;
        }*/
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
