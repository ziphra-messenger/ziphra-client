package com.privacity.cliente.activity.myaccount;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.request.MyAccountNicknameRequestDTO;

import org.springframework.http.ResponseEntity;

public class MyAccountNicknameFrame {

    private final Button reset;
    private final Button save;

    private final EditText nickname;
    private final Button generate;


    private final MyAccountActivity activity;

    private final MenuAcordeonObject menuAcordeon;

    public MyAccountNicknameFrame(MyAccountActivity activity) {

        reset=(Button) activity.findViewById(R.id.my_account_nickname_reset);
        save=(Button) activity.findViewById(R.id.my_account_nickname_reset);

        nickname=(EditText) activity.findViewById(R.id.my_account_nickname_new);
        NicknameUtil.setNicknameMaxLenght(nickname);

        generate=(Button) activity.findViewById(R.id.my_account_nickname_generate);



        menuAcordeon= new MenuAcordeonObject(
                (Button) activity.findViewById(R.id.my_account_nickname_title),
                (View) activity.findViewById(R.id.my_account_nickname_content));

        this.activity = activity;
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

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nickname.setText(NicknameUtil.generateRandomNickname());

            }
        });


    }
    private void save() throws Exception {

        if (!validationSave()) return;

        ProgressBarUtil.show(activity, activity.progressBar);
        MyAccountNicknameRequestDTO dto =  buildDTO();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MY_ACCOUNT);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MY_ACCOUNT_SAVE_NICKNAME);

        p.setObjectDTO(GsonFormated.get().toJson(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        SingletonValues.getInstance().getUsuario().setNickname(nickname.getText().toString());
                        //SingletonValues.getInstance().getMyAccountConfDTO().setShowAlias(aliasSwitch.isChecked());
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

        if (!NicknameUtil.validarNickname(activity, nickname)) return false;

        if (NicknameUtil.compareCurrentNickname(nickname)){
            Toast.makeText(activity,"Guardado",Toast. LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private MyAccountNicknameRequestDTO buildDTO() {
        MyAccountNicknameRequestDTO dto = new MyAccountNicknameRequestDTO();
        if (!NicknameUtil.compareCurrentNickname(nickname)){
            dto.setNickname(nickname.getText().toString());
        }
        //dto.setShowAlias(aliasSwitch.isChecked());

        return dto;
    }


    public void loadValues() {
        nickname.setText(SingletonValues.getInstance().getUsuario().getNickname());
        //alias.setText(SingletonValues.getInstance().getUsuario().getAlias());
    }
}
