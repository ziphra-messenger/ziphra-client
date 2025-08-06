package com.privacity.cliente.activity.message.avanzado;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoNicknameFrame;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.util.CopyPasteUtil;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.common.dto.request.GrupoInfoNicknameRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;
import com.privacity.common.util.RandomNicknameUtil;

import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class  NickNameView {
    private final Button close;
    private Button reset;
    private Button save;
    private EditText nickname;
    private Button generate;

    private ProgressBar progressBar;
    private TextView messageInfo;

    private Activity activity;


    public NickNameView(Activity activity){
        this.activity=activity;

        this.progressBar = progressBar;
        reset= GetButtonReady.get(activity,R.id.include__nickname_reset);

        save=GetButtonReady.get(activity,R.id.include__nickname_save, v -> {
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
                SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
            }

        });

        nickname=(EditText) activity.findViewById(R.id.include__nickname_new);
        NicknameUtil.setNicknameMaxLenght(nickname);

        generate=GetButtonReady.get(activity,R.id.include__nickname_generate);
        close =  GetButtonReady.get(SingletonCurrentActivity.getInstance().getMessageActivity(), R.id.include__nickname__close);

        this.messageInfo =(TextView) activity.findViewById(R.id.include__nickname__mensaje__info);


        setListener();
        loadValues();



    }



    @SuppressLint("ClickableViewAccessibility")
    public void setListener(){


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(
                        R.id.include__nickname__content__all
                ).setVisibility(View.GONE);

            }
        });


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    reset();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nickname.setText(RandomNicknameUtil.get());

            }
        });

        CopyPasteUtil.setListenerIconClearText(nickname);
    }

    public void loadValues() {
        reset();
        getMessageInfo().setText(activity.getString(R.string.frame_grupo_info_nickname__message_info, Singletons.usuario().getUsuario().getNickname()));

    }

    public void reset() {
        getNickname().setText(SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().getNickname());
    }
    public void save() throws Exception {

        if (!validationSave()) return;

    //    ProgressBarUtil.show(activity, activity.progressBar);


        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_NICKNAME);

        GrupoInfoNicknameRequestDTO l = new GrupoInfoNicknameRequestDTO();
        l.setNickname(getNickname().getText().toString());
        l.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(l));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                      //  ProgressBarUtil.hide(activity, activity.progressBar);
                        //Singletons.usuario().getUsuario().setNickname(nickname.getText().toString());
                        //SingletonValues.getInstance().getMyAccountConfDTO().setShowAlias(aliasSwitch.isChecked());



                        SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().setNickname ( getNickname().getText().toString());
                        //loadValues();

                        String nn = SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().getNickname();
                        if ( nn == null || nn.trim().equals("") ){
                            nn = Singletons.usuario().getUsuario().getNickname();
                        }

                        ((TextView) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__nickname__conclusion)).setText( (nn));

                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();
                        SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(
                                R.id.include__nickname__content__all
                        ).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                       // ProgressBarUtil.hide(activity, activity.progressBar);

                    }

                });


    }

    private boolean validationSave() {

        return NicknameUtil.validarNickname(activity, getNickname(), true);
/*        if (!NicknameUtil.compareCurrentNickname(
                SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().getNicknameGrupo(),
                nickname)){
            Toast.makeText(activity,activity.getString(R.string.general__saved)",Toast. LENGTH_SHORT).show();
            return false;
        }
*/
    }

    private String buildDTO() {
        return getNickname().getText().toString();
    }
}
