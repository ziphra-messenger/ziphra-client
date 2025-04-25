package com.privacity.cliente.activity.grupoinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupoinfo.gralconfOtras.ValuesFiller;
import com.privacity.cliente.activity.myaccount.SwitchTxt;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class ConfiguracionGeneral {


    private MultipleOption anonimo;
   // private ConfiguracionGeneralAudioChat audioChat;
    private ConfiguracionGeneralTemporal temporal;

    private MultipleOption extraEncript;

    private ConfiguracionGeneralOtras otras;
    private Button reset;

    private Button save;
    private Button saveclose;
    private Button close;
    private Activity activity;

    public ConfiguracionGeneral(Activity activity) {
        this.activity = activity;
        initView();


        loadValues(SingletonValues.getInstance().getGrupoSeleccionado());
        setListener();
    }

    public void setDefaultValues(){

        anonimo.getPermitir().setChecked(true);
/*        audioChat.getAudioChat().getPermitir().setChecked(true);
        audioChat.getConfAudioChatMaximoTiempo().setSelection(4);*/
        temporal.getConfTemporalObligatorio().setChecked(false);
        temporal.getConfTemporalMaximoTiempoPermitido().setSelection(4);

        extraEncript.getPermitir().setChecked(true);

//        descarga.getConfDescargaAudio().setChecked(true);
//        descarga.getConfDescargaVideo().setChecked(true);

        ValuesFiller.setDefaultValues(otras);

    }

    private void initView() {

        


        setAnonimo(new MultipleOption(
                (TextView) activity.findViewById(R.id.grupo_info_conf_anonimo_titulo),
                (RadioGroup) activity.findViewById(R.id.grupo_info_conf_anonimo_grupo),
                (RadioButton) activity.findViewById(R.id.grupo_info_conf_anonimo_permitir),
                (RadioButton) activity.findViewById(R.id.grupo_info_conf_anonimo_bloquear),
                (RadioButton) activity.findViewById(R.id.grupo_info_conf_anonimo_obligatorio))
        );
        setTemporal(new ConfiguracionGeneralTemporal(
                (CheckBox) activity.findViewById(R.id.grupo_info_conf_temporal_obligatorio),
                (Spinner) activity.findViewById(R.id.grupo_info_conf_temporal_maximo_tiempo_permitido)
        ));






        setOtras(new ConfiguracionGeneralOtras(activity,
               activity.findViewById(R.id.tl_grupoinfo_menu_message_content)
                ,
                new SwitchTxt((Switch) activity.findViewById(R.id.grupo_info_conf_gen__random_nickname),
                        (TextView) activity.findViewById(R.id.grupo_info_conf_gen__random_nickname_txt)
                        ,(Button) activity.findViewById(R.id.grupo_info_conf_gen__random_nickname_icon)
                ),

                new SwitchTxt((Switch) activity.findViewById(R.id.grupo_info_conf_gen__hide_message_details),
                        (TextView) activity.findViewById(R.id.grupo_info_conf_gen__hide_message_details_txt)
                        ,(Button) activity.findViewById(R.id.grupo_info_conf_gen__hide_message_details_icon)
                ),
                new SwitchTxt((Switch) activity.findViewById(R.id.grupo_info_conf_gen__hide_message_read_state),
                        (TextView) activity.findViewById(R.id.grupo_info_conf_gen__hide_message_read_state_txt)
                        ,(Button) activity.findViewById(R.id.grupo_info_conf_gen__hide_message_read_state_icon)
                ),
                new SwitchTxt((Switch)activity.findViewById(R.id.grupo_info_conf_gen__allow_audio),
                        (TextView)activity.findViewById(R.id.grupo_info_conf_gen__allow_audio_txt)
                        ,(Button)activity.findViewById(R.id.grupo_info_conf_gen__allow_audio_icon)
                ),
                new SwitchTxt((Switch)activity.findViewById(R.id.grupo_info_conf_gen__hide_member_list),
                        (TextView)activity.findViewById(R.id.grupo_info_conf_gen__hide_member_list_txt)
                        ,(Button)activity.findViewById(R.id.grupo_info_conf_gen__hide_member_list_icon)
                ),
                new SwitchTxt((Switch)activity.findViewById(R.id.grupo_info_conf_gen__black_message_attach_mandatory),
                        (TextView)activity.findViewById(R.id.grupo_info_conf_gen__black_message_attach_mandatory_txt)
                        ,(Button)activity.findViewById(R.id.grupo_info_conf_gen__black_message_attach_mandatory_icon)
                ),
                new SwitchTxt((Switch)activity.findViewById(R.id.grupo_info_conf_gen__block_resend),
                        (TextView)activity.findViewById(R.id.grupo_info_conf_gen__block_resend_txt)
                        ,(Button)activity.findViewById(R.id.grupo_info_conf_gen__block_resend_icon)
                ),

                new SwitchTxt((Switch)activity.findViewById(R.id.grupo_info_conf_gen__block_download),
                        (TextView)activity.findViewById(R.id.grupo_info_conf_gen__block_download_txt)
                        ,(Button)activity.findViewById(R.id.grupo_info_conf_gen__block_download_icon)
                ),
                (Spinner)activity.findViewById(R.id.grupo_info_conf_audiochat_maximo_tiempo)
        ));


        setExtraEncript(new MultipleOption(
                (TextView)activity.findViewById(R.id.grupo_info_conf_extra_encrip_titulo),
                (RadioGroup)activity.findViewById(R.id.grupo_info_conf_extra_encrip_grupo),
                (RadioButton)activity.findViewById(R.id.grupo_info_conf_extra_encrip_permitir),
                (RadioButton)activity.findViewById(R.id.grupo_info_conf_extra_encrip_bloquear),
                (RadioButton)activity.findViewById(R.id.grupo_info_conf_extra_encrip_obligatorio))
        );


        setReset(GetButtonReady.get(activity,R.id.grupo_info_conf_reset));
        setSave(GetButtonReady.get(activity,R.id.grupo_info_conf_save,new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                if (validar()){
                    SimpleErrorDialog.passwordValidation(activity, new SimpleErrorDialog.PasswordValidationI() {
                        @Override
                        public void action() {
                            if (validar()){
                                save(false);

                            }
                        }

                    });

                }}
        }));
        setSaveclose(GetButtonReady.get(activity,R.id.grupo_info_conf_save_close,new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                if (validar()){
                    SimpleErrorDialog.passwordValidation(activity, new SimpleErrorDialog.PasswordValidationI() {
                        @Override
                        public void action() {
                            if (validar()){
                                save(true);


                            }
                        }

                    });

                }}
        }));

        setClose(GetButtonReady.get(activity,R.id.grupo_info_conf_close));
        close.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         activity.onBackPressed();
                                     }
                                 }
        );

    }


    public void setListener(){
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultValues();
            }
        });


    }
    private void save(boolean close) {

        GrupoGralConfDTO dto =  buildDTO();



        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_GENERAL_CONFIGURATION);
        p.setObjectDTO(new Gson().toJson(dto));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        SingletonValues.getInstance().getGrupoSeleccionado().setGralConfDTO(dto);
                        Intent intent = new Intent(BroadcastConstant.BROADCAST__RELOAD_CONFIGURACION_AVANZADA_MESSAGE_ACTIVITY);
                        activity.sendBroadcast(intent);
                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();

                        if (close){
                            activity.onBackPressed();
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                });


    }
    private GrupoGralConfDTO buildDTO() {
        SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        GrupoGralConfDTO dto = UtilsStringSingleton.getInstance().gson().fromJson(UtilsStringSingleton.getInstance().gson().toJson(SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO()),GrupoGralConfDTO.class);
//       dto.setIdGrupo();
//        dto.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());

        dto.setAnonimo(this.getAnonimo().getSelectedValue());
    //    dto.setAnonimo(RulesConfEnum.OFF);
        //dto.setAudiochat(this.getAudioChat().getAudioChat().getSelectedValue());


//        dto.setDownloadAllowAudio(this.getDescarga().getConfDescargaAudio().isChecked());
//        dto.setDownloadAllowVideo(this.getDescarga().getConfDescargaVideo().isChecked());
        //dto.setExtraEncrypt(this.getExtraEncript().getSelectedValue());

        dto.setAudiochatMaxTime(((this.getOtras().getConfAudiochatMaxTime().getSelectedItemPosition()+1)*60));

        {
            Resources res = activity.getResources();
            String[] a = res.getStringArray(R.array.time_messages_values_in_seconds);
            int t = Integer.parseInt(a[this.temporal.getConfTemporalMaximoTiempoPermitido().getSelectedItemPosition()]);
            dto.setTimeMessageMaxTimeAllow(t);
            dto.setTimeMessageMandatory(this.getTemporal().getConfTemporalObligatorio().isChecked());
        }
        dto.setHideMessageDetails(this.getOtras().getConfGenHideMessageDetails().isChecked());
        dto.setBlockAudioMessages(this.getOtras().getConfGenBlockAudioMessages().isChecked());
        dto.setRandomNickname(this.getOtras().getConfGenRandomNickname().isChecked());
        dto.setHideMessageReadState(this.getOtras().getConfGenHideMessageReadState().isChecked());
        dto.setHideMemberList(this.getOtras().getConfGenHideMemberList().isChecked());

        dto.setBlackMessageAttachMandatory(this.getOtras().getConfGenBlackMessageAttachMandatory().isChecked());
        dto.setBlockResend(this.getOtras().getConfGenBlockResend().isChecked());
        dto.setBlockMediaDownload(this.getOtras().getConfGenBlockMediaDownload().isChecked());

        return dto;
    }

    public boolean validar(){
        if (buildDTO().equals(SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO())){

            Toast toast = Toast.makeText(activity, activity.getString(R.string.general__save__not_changes), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    public void loadValues(Grupo g) {
        GrupoGralConfDTO c = g.getGralConfDTO();


        anonimo.setValue(c.getAnonimo());
       // audioChat.getAudioChat().setValue(c.getAudiochat());
      //  extraEncript.setValue(c.getExtraEncrypt());

        temporal.getConfTemporalObligatorio().setChecked(c.isTimeMessageMandatory());

        //descargaImagen.setValue(c.isBlockMediaDownload());
//        descarga.getConfDescargaAudio().setChecked(c.isDownloadAllowAudio());
//        descarga.getConfDescargaVideo().setChecked(c.isDownloadAllowVideo());

        ValuesFiller.initValues(c, otras);


        temporal.getConfTemporalMaximoTiempoPermitido().setSelection(4);

    }


}
