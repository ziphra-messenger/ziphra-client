package com.privacity.cliente.activity.myaccount;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.TemporalMessage;
import com.privacity.common.config.ConstantProtocolo;

import com.privacity.common.dto.MyAccountConfDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.servergralconf.SGCMyAccountConfDTO;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class MyAccountConfiguracionGeneralFrame {

    private CheckBox reenviar;

    private ConfiguracionGeneralTemporal temporal;
    private CheckBox confBlackObligatorioAdjunto;

    private ConfiguracionGeneralDescarga descarga;
    private ConfiguracionGeneralOtras otras;
    private Button reset;

    private Button save;

    private MyAccountActivity activity;

    private MenuAcordeonObject menuAcordeon;

    public MyAccountConfiguracionGeneralFrame(MyAccountActivity activity) {

        this.setMenuAcordeon( new MenuAcordeonObject(
                (Button) activity.findViewById(R.id.my_account_conf_gral_conf_title),
                (View) activity.findViewById(R.id.my_account_conf_gral_conf_content))
        );

        this.activity = activity;


        this.setReenviar(
                (CheckBox) activity.findViewById(R.id.my_account_conf_reenvio)
        );


        this.setTemporal(new ConfiguracionGeneralTemporal(
                (CheckBox) activity.findViewById(R.id.my_account_conf_temporal_obligatorio),
                (Spinner) activity.findViewById(R.id.my_account_conf_temporal_maximo_tiempo_permitido)
        ));
        this.setConfBlackObligatorioAdjunto((CheckBox) activity.findViewById(R.id.my_account_conf_black_obligatorio_adjunto));

        this.setDescarga(new ConfiguracionGeneralDescarga(
                (CheckBox) activity.findViewById(R.id.my_account_conf_descarga_imagen)
        ));
        this.setOtras(new ConfiguracionGeneralOtras(
                (CheckBox) activity.findViewById(R.id.my_account_conf_gen_ocultar_estado),
                (CheckBox) activity.findViewById(R.id.my_account_conf_gen_ocultar_lista_integrantes)
        ));


        this.setReset((Button) activity.findViewById(R.id.my_account_conf_reset));
        this.setSave((Button) activity.findViewById(R.id.my_account_conf_save));

    }

    public void setDefaultValues(){
        SGCMyAccountConfDTO c = SingletonValues.getInstance().getSystemGralConf().getMyAccountConf();
        reenviar.setChecked(c.isResend());
        temporal.getConfTemporalSiempre().setChecked(c.isTimeMessageAlways());
        temporal.getConfTemporalTiempoDefault().setSelection(TemporalMessage.getIndexBySeconds(c.getTimeMessageDefaultTime()));
        confBlackObligatorioAdjunto.setChecked(c.isBlackMessageAttachMandatory());
        descarga.getConfDescargaImagen().setChecked(c.isDownloadAttachAllowImage());
        //descarga.getConfDescargaAudio().setChecked(true);
        //descarga.getConfDescargaVideo().setChecked(true);
        otras.getConfEscondermeDetalles().setChecked(c.isHideMyInDetails());
        otras.getConfGenOcultarEstado().setChecked(c.isHideMyMessageState());
    }

    public void setListener(){


        MenuAcordeonUtil.setActionMenu(this.getMenuAcordeon());

        reset.setOnClickListener(v -> setDefaultValues());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleErrorDialog.passwordValidation(activity, new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                            save();
                    }

                });

            }
        });
    }
    private void save() {
        ProgressBarUtil.show(activity, activity.progressBar);
        MyAccountConfDTO dto =  buildDTO();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MY_ACCOUNT);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MY_ACCOUNT_SAVE_GENERAL_CONFIGURATION);
        p.setObjectDTO(GsonFormated.get().toJson(dto));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        SingletonValues.getInstance().setMyAccountConfDTO(dto);
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
    private MyAccountConfDTO buildDTO() {
        MyAccountConfDTO dto = new MyAccountConfDTO();
        dto.setResend(this.getReenviar().isChecked());
        dto.setBlackMessageAttachMandatory(this.getConfBlackObligatorioAdjunto().isChecked());
        dto.setDownloadAttachAllowImage(this.getDescarga().getConfDescargaImagen().isChecked());
        dto.setTimeMessageAlways(this.temporal.getConfTemporalSiempre().isChecked());
        dto.setTimeMessageDefaultTime(TemporalMessage.getSecondsByIndex(this.temporal.getConfTemporalTiempoDefault().getSelectedItemPosition()));
        dto.setHideMyMessageState(this.getOtras().getConfGenOcultarEstado().isChecked());

        return dto;
    }

    public void loadValues() {
        MyAccountConfDTO c = SingletonValues.getInstance().getMyAccountConfDTO();
        reenviar.setChecked(c.isResend());
        temporal.getConfTemporalSiempre().setChecked(c.isTimeMessageAlways());
        confBlackObligatorioAdjunto.setChecked(c.isBlackMessageAttachMandatory());
        descarga.getConfDescargaImagen().setChecked(c.isDownloadAttachAllowImage());
        otras.getConfGenOcultarEstado().setChecked(c.isHideMyMessageState());
        temporal.getConfTemporalTiempoDefault().setSelection(TemporalMessage.getIndexBySeconds(c.getTimeMessageDefaultTime()));

    }
}
