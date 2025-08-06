package com.privacity.cliente.activity.grupoinfo;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupoinfo.nicknameframe.NickNameFrameView;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.GrupoInfoNicknameRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import lombok.Getter;

public class GrupoInfoNicknameFrame {

    private final GrupoInfoActivity activity;
    private final ProgressBar progressBar;
    @Getter
    private NickNameFrameView view;
    public GrupoInfoNicknameFrame(GrupoInfoActivity activity, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.activity = activity;

        view = new NickNameFrameView(activity,this);


    }


    public void save() throws Exception {

        if (!validationSave()) return;

        ProgressBarUtil.show(activity, activity.progressBar);


        Protocolo p = new Protocolo();
       p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_NICKNAME);

        GrupoInfoNicknameRequestDTO l = new GrupoInfoNicknameRequestDTO();
        l.setNickname(view.getNickname().getText().toString());
        l.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(l));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        //Singletons.usuario().getUsuario().setNickname(nickname.getText().toString());
                        //SingletonValues.getInstance().getMyAccountConfDTO().setShowAlias(aliasSwitch.isChecked());


                        SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().setNickname ( view.getNickname().getText().toString());
                        //loadValues();
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

        return NicknameUtil.validarNickname(activity, view.getNickname(), true);
/*        if (!NicknameUtil.compareCurrentNickname(
                SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().getNicknameGrupo(),
                nickname)){
            Toast.makeText(activity,activity.getString(R.string.general__saved)",Toast. LENGTH_SHORT).show();
            return false;
        }
*/
    }

    private String buildDTO() {
        return view.getNickname().getText().toString();
    }



}
