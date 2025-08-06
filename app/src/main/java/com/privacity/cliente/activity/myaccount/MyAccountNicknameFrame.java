package com.privacity.cliente.activity.myaccount;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.util.CopyPasteUtil;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.cliente.util.ValidarUsuarioPassword;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.MyAccountNicknameRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class MyAccountNicknameFrame {
    private static final String TAG = "MyAccountNicknameFrame";
    private final Button reset;
    private final Button save;
    private final Button title;
    private final EditText nickname;
    private final Button generate;

    private final TextView nicknameValidate;
    private final MyAccountActivity activity;

    private final MenuAcordeonObject menuAcordeon;

    public MyAccountNicknameFrame(MyAccountActivity activity) {


        reset = (GetButtonReady.get(activity, R.id.my_account_nickname_reset));
        save =GetButtonReady.get(activity, R.id.my_account_nickname_save,v -> save());
        generate = (GetButtonReady.get(activity, R.id.my_account_nickname_generate));
        title =(GetButtonReady.get(activity, R.id.my_account_nickname_title));

        nickname=(EditText) activity.findViewById(R.id.my_account_nickname_new);
        nicknameValidate=(TextView) activity.findViewById(R.id.my_account_nickname_new__validate);
        NicknameUtil.setNicknameMaxLenght(nickname);






        menuAcordeon= new MenuAcordeonObject(
               title,
                (View) activity.findViewById(R.id.my_account_nickname_content));

        this.activity = activity;
    }

    public void setListener(){

        MenuAcordeonUtil.setActionMenu(menuAcordeon);



        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadValues();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nickname.setText(NicknameUtil.generateRandomNickname());
                while(!validationSave()){
                    Log.e(TAG, "generando: " + nickname.getText().toString() );
                    nickname.setText(NicknameUtil.generateRandomNickname());
                }
            }
        });

        CopyPasteUtil.setListenerIconClearText(nickname);

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validationSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
    private void save() {

        if (!validationSave()) return;

        ProgressBarUtil.show(activity, activity.progressBar);
        MyAccountNicknameRequestDTO dto =  buildDTO();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_NICKNAME);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        Singletons.usuario().getUsuario().setNickname(nickname.getText().toString());
                        //SingletonValues.getInstance().getMyAccountConfDTO().setShowAlias(aliasSwitch.isChecked());
                        loadValues();
                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();

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
        save.setEnabled(false);

        if ( nickname.getText().toString().equals(Singletons.usuario().getUsuario().getNickname())){
            reset.setEnabled(false);
        }else{
            reset.setEnabled(true);
        }

        boolean r= ValidarUsuarioPassword.validateNickname(activity,nicknameValidate, SingletonServerConfiguration.getInstance().getSystemGralConf().getPasswordConfig().getNickname(),
                nickname.getText().toString(),Singletons.usuario().getUsuario().getNickname(),
                SingletonValues.getInstance().getUsernameNoHash(), SingletonValues.getInstance().getPassword(),true);

        save.setEnabled(r);
        return r;
    }

    private MyAccountNicknameRequestDTO buildDTO() {
        MyAccountNicknameRequestDTO dto = new MyAccountNicknameRequestDTO();
        dto.setNickname(nickname.getText().toString().trim());
        //dto.setShowAlias(aliasSwitch.isChecked());

        return dto;
    }


    public void loadValues() {
        nickname.setText(Singletons.usuario().getUsuario().getNickname());
        nicknameValidate.setVisibility(View.GONE);
        nicknameValidate.setText("");
        save.setEnabled(false);
        reset.setEnabled(false);
        //alias.setText(Singletons.usuario().getUsuario().getAlias());
    }
}
