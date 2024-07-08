package com.privacity.cliente.activity.grupoinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.gson.Gson;
import com.privacity.cliente.R;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class ConfiguracionGeneral {


    private CheckBox reenviar;
    private MultipleOption anonimo;
    private ConfiguracionGeneralAudioChat audioChat;
    private ConfiguracionGeneralTemporal temporal;
    private CheckBox confBlackObligatorioAdjunto;
    private MultipleOption extraEncript;
    private MultipleOption descargaImagen;
    private ConfiguracionGeneralOtras otras;
    private Button reset;

    private Button save;

    private Activity activity;

    public ConfiguracionGeneral(Activity activity) {
        this.activity = activity;
    }

    public void setDefaultValues(){
        reenviar.setChecked(true);
        anonimo.getPermitir().setChecked(true);
        audioChat.getAudioChat().getPermitir().setChecked(true);
        audioChat.getConfAudioChatMaximoTiempo().setSelection(4);
        temporal.getConfTemporalObligatorio().setChecked(false);
        temporal.getConfTemporalMaximoTiempoPermitido().setSelection(4);
        confBlackObligatorioAdjunto.setChecked(false);
        extraEncript.getPermitir().setChecked(true);
        descargaImagen.getPermitir().setChecked(true);
//        descarga.getConfDescargaAudio().setChecked(true);
//        descarga.getConfDescargaVideo().setChecked(true);

        otras.getConfGenCambiarNicknameToNumber().setChecked(false);
        otras.getConfGenOcultarDetalles().setChecked(false);
        otras.getConfGenOcultarEstado().setChecked(false);
        otras.getConfGenOcultarListaIntegrantes().setChecked(false);

    }

    public void setListener(){
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultValues();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleErrorDialog.passwordValidation(activity, new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        if (validar()){
                            save();

                        }
                    }

                });

            }
        });
    }
    private void save() {

        GrupoGralConfDTO dto =  buildDTO();



        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_GENERAL_CONFIGURATION);
        p.setObjectDTO(new Gson().toJson(dto));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        SingletonValues.getInstance().getGrupoSeleccionado().setGralConfDTO(dto);
                        Intent intent = new Intent("reload_configuracion_avanzada_message_activity");
                        activity.sendBroadcast(intent);
                        Toast.makeText(activity,"Guardado",Toast. LENGTH_SHORT).show();


                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                });


    }
    private GrupoGralConfDTO buildDTO() {
        GrupoGralConfDTO dto = new GrupoGralConfDTO();
        dto.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        dto.setResend(this.getReenviar().isChecked());
        dto.setAnonimo(this.getAnonimo().getSelectedValue());
        dto.setAudiochat(this.getAudioChat().getAudioChat().getSelectedValue());
        dto.setBlackMessageAttachMandatory(this.getConfBlackObligatorioAdjunto().isChecked());
        dto.setDownloadAllowImage(this.getDescargaImagen().getSelectedValue());
//        dto.setDownloadAllowAudio(this.getDescarga().getConfDescargaAudio().isChecked());
//        dto.setDownloadAllowVideo(this.getDescarga().getConfDescargaVideo().isChecked());
        dto.setExtraEncrypt(this.getExtraEncript().getSelectedValue());

        dto.setAudiochatMaxTime(((this.getAudioChat().getConfAudioChatMaximoTiempo().getSelectedItemPosition()+1)*60));

        {
            Resources res = activity.getResources();
            String[] a = res.getStringArray(R.array.time_messages_values_in_seconds);
            int t = Integer.parseInt(a[this.temporal.getConfTemporalMaximoTiempoPermitido().getSelectedItemPosition()]);
            dto.setTimeMessageMaxTimeAllow(t);
            dto.setTimeMessageMandatory(this.getTemporal().getConfTemporalObligatorio().isChecked());
        }
        dto.setHideMessageDetails(this.getOtras().getConfGenOcultarDetalles().isChecked());
        dto.setChangeNicknameToNumber(this.getOtras().getConfGenCambiarNicknameToNumber().isChecked());
        dto.setHideMessageState(this.getOtras().getConfGenOcultarEstado().isChecked());
        dto.setHideMemberList(this.getOtras().getConfGenOcultarListaIntegrantes().isChecked());

        return dto;
    }

    public boolean validar(){
        boolean valido=true;

        return valido;
    }

    public void loadValues(GrupoDTO g) {
        GrupoGralConfDTO c = g.getGralConfDTO();

        reenviar.setChecked(c.isResend());
        anonimo.setValue(c.getAnonimo());
        audioChat.getAudioChat().setValue(c.getAudiochat());
        extraEncript.setValue(c.getExtraEncrypt());

        temporal.getConfTemporalObligatorio().setChecked(c.isTimeMessageMandatory());
        confBlackObligatorioAdjunto.setChecked(c.isBlackMessageAttachMandatory());

        descargaImagen.setValue(c.getDownloadAllowImage());
//        descarga.getConfDescargaAudio().setChecked(c.isDownloadAllowAudio());
//        descarga.getConfDescargaVideo().setChecked(c.isDownloadAllowVideo());

        otras.getConfGenCambiarNicknameToNumber().setChecked(c.isChangeNicknameToNumber());
        otras.getConfGenOcultarDetalles().setChecked(c.isHideMessageDetails());
        otras.getConfGenOcultarEstado().setChecked(c.isHideMessageState());

        audioChat.getConfAudioChatMaximoTiempo().setSelection(4);
        temporal.getConfTemporalMaximoTiempoPermitido().setSelection(4);

    }
}
