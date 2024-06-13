package com.privacity.cliente.activity.grupoinfo;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

public class GrupoInfoNicknameFrame {

    private final Button reset;
    private final Button save;

    private final EditText nickname;
    private final Button generate;


    private final GrupoInfoActivity activity;

    private final MenuAcordeonObject menuAcordeon;
    private final ProgressBar progressBar;
    private final TextView help;

    public GrupoInfoNicknameFrame(GrupoInfoActivity activity, ProgressBar progressBar) {
        this.progressBar = progressBar;
        reset=(Button) activity.findViewById(R.id.grupo_info_nickname_reset);
        save=(Button) activity.findViewById(R.id.grupo_info_nickname_save);

        nickname=(EditText) activity.findViewById(R.id.grupo_info_nickname_new);
        NicknameUtil.setNicknameMaxLenght(nickname);

        generate=(Button) activity.findViewById(R.id.grupo_info_nickname_generate);

        this.help=(TextView) activity.findViewById(R.id.grupo_info_nickname_help);

        menuAcordeon= new MenuAcordeonObject(
                (Button) activity.findViewById(R.id.grupo_info_nickname_title),
                (View) activity.findViewById(R.id.grupo_info_nickname_content));

        this.activity = activity;
    }

    @SuppressLint("ClickableViewAccessibility")
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

        nickname.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                //ese 35 es inventado...
                if(event.getX() >= (nickname.getRight() - 35 - nickname.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    nickname.setText("");
                }
            }
            return false;
        });
    }
    private void save() throws Exception {

        if (!validationSave()) return;

        ProgressBarUtil.show(activity, activity.progressBar);
        String dto =  buildDTO();

        ProtocoloDTO p = new ProtocoloDTO();
/*        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MY_ACCOUNT);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_INFO_SAVE_NICKNAME);
*/
        p.setObjectDTO(GsonFormated.get().toJson(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        //SingletonValues.getInstance().getUsuario().setNickname(nickname.getText().toString());
                        //SingletonValues.getInstance().getMyAccountConfDTO().setShowAlias(aliasSwitch.isChecked());
                        Observers.grupo().getGrupoById(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).setAlias(nickname.getText().toString());
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

        if (!NicknameUtil.validarNickname(activity, nickname,true)) return false;
        if (!NicknameUtil.compareCurrentNickname(
                SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),
                nickname)){
            Toast.makeText(activity,"Guardado",Toast. LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String buildDTO() {
        return nickname.getText().toString();
    }


    public void loadValues() {
        nickname.setText(Observers.grupo().getGrupoById(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).getAlias());
        help.setText(help.getText().toString() + " " + SingletonValues.getInstance().getUsuario().getNickname());

    }
}
