package com.privacity.cliente.activity.grupoinfo.nicknameframe;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoNicknameFrame;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.util.CopyPasteUtil;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.common.util.RandomNicknameUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NickNameFrameView {
    private Button reset;
    private Button save;
    private EditText nickname;
    private Button generate;
    private MenuAcordeonObject menuAcordeon;
    private ProgressBar progressBar;
    private TextView messageInfo;

    private GrupoInfoActivity activity;
    private GrupoInfoNicknameFrame frame;

    public NickNameFrameView(GrupoInfoActivity activity,GrupoInfoNicknameFrame frame){
       this.activity=activity;
       this.frame=frame;

        this.progressBar = progressBar;
        reset= GetButtonReady.get(activity,R.id.grupo_info_nickname_reset);

        save=GetButtonReady.get(activity,R.id.grupo_info_nickname_save, v -> {
            try {
                frame.save();
            } catch (Exception e) {
                e.printStackTrace();
                SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
            }

        });

        nickname=(EditText) activity.findViewById(R.id.grupo_info_nickname_new);
        NicknameUtil.setNicknameMaxLenght(nickname);

        generate=GetButtonReady.get(activity,R.id.grupo_info_nickname_generate);

        this.messageInfo =(TextView) activity.findViewById(R.id.grupo_info_nickname__mensaje__info);

        menuAcordeon= new MenuAcordeonObject(
                GetButtonReady.get(activity,R.id.grupo_info_nickname_title),
                (View) activity.findViewById(R.id.grupo_info_nickname_content));

        setListener();
        loadValues();



    }



    @SuppressLint("ClickableViewAccessibility")
    public void setListener(){



        MenuAcordeonUtil.setActionMenu(menuAcordeon);



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

}
