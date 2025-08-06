package com.privacity.cliente.activity.myaccount;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.selecttime.CallbackSelectTime;
import com.privacity.cliente.common.component.selecttime.SelectTime;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.TemporalMessage;
import com.privacity.common.dto.MyAccountConfDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.servergralconf.SGCMyAccountConfDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class MyAccountConfiguracionGeneralFrame {

    private SwitchTxt blockResend;

    private ConfiguracionGeneralTemporal temporal;
    private SwitchTxt confBlackMessageAttachMandatory;
    private SwitchTxt confBlackMessageAttachMandatoryReceived;

    private ConfiguracionGeneralDescarga descarga;
    private ConfiguracionGeneralOtras otras;
    private Button reset;

    private Button save;

    private MyAccountActivity activity;

    private MenuAcordeonObject menuAcordeon;
    private SelectTime selectTime;

    public MyAccountConfiguracionGeneralFrame(MyAccountActivity activity) {

        this.setMenuAcordeon( new MenuAcordeonObject(
                GetButtonReady.get(activity, R.id.my_account_conf_gral_conf_title),
                (View) activity.findViewById(R.id.my_account_conf_gral_conf_content))
        );

        this.activity = activity;

        this.setBlockResend(new SwitchTxt(
                (Switch) activity.findViewById(R.id.my_account_conf__block_resend),
                (TextView) activity.findViewById(R.id.my_account_conf_reenvio_txt),
                (Button) activity.findViewById(R.id.my_account_conf_reenvio_icon)

        ));

        this.setTemporal(new ConfiguracionGeneralTemporal(
                new SwitchTxt((Switch) activity.findViewById(R.id.my_account_conf_temporal_obligatorio),
                        (TextView) activity.findViewById(R.id.my_account_conf_temporal_obligatorio_txt),
                        (Button) activity.findViewById(R.id.my_account_conf_temporal_obligatorio_icon)
                        ),
                (Button) activity.findViewById(R.id.my_account_conf_temporal_maximo_tiempo_permitido)
        ));

        this.setConfBlackMessageAttachMandatory(new SwitchTxt(
                (Switch) activity.findViewById(R.id.my_account_conf_black_obligatorio_adjunto),
                (TextView) activity.findViewById(R.id.my_account_conf_black_obligatorio_adjunto_txt),
                (Button) activity.findViewById(R.id.my_account_conf_black_obligatorio_adjunto_icon)

        ));
        this.setConfBlackMessageAttachMandatoryReceived(new SwitchTxt(
                (Switch) activity.findViewById(R.id.my_account_conf_black_obligatorio_adjunto_recibido),
                (TextView) activity.findViewById(R.id.my_account_conf_black_obligatorio_adjunto_recibido_txt),
                (Button) activity.findViewById(R.id.my_account_conf_black_obligatorio_adjunto_recibido_icon)

        ));

        this.setDescarga(new ConfiguracionGeneralDescarga(new SwitchTxt(
                (Switch) activity.findViewById(R.id.my_account_conf_descarga_imagen),
                (TextView) activity.findViewById(R.id.my_account_conf_descarga_imagen_txt),
                (Button) activity.findViewById(R.id.my_account_conf_descarga_imagen_icon))

        ));

        this.setOtras(new ConfiguracionGeneralOtras(new SwitchTxt(
                (Switch) activity.findViewById(R.id.my_account_conf_gen_ocultar_estado),
                (TextView) activity.findViewById(R.id.my_account_conf_gen_ocultar_estado_txt),
                (Button) activity.findViewById(R.id.my_account_conf_gen_ocultar_estado_icon))

        ));

        this.setReset(GetButtonReady.get(activity, R.id.my_account_conf_reset));
        this.setSave(GetButtonReady.get(activity, R.id.my_account_conf_save,getSaveOnClickListener()));
        selectTime = new SelectTime(activity, new CallbackSelectTime() {
            @Override
            public void action(String timeToShow, int timeInSeconds) {
                temporal.getConfTemporalTiempoDefault().setText(timeToShow);
                temporal.getConfTemporalTiempoDefault().setTag(timeInSeconds);
            }
        });


    }

    public void setDefaultValues(){
        SGCMyAccountConfDTO c = SingletonServerConfiguration.getInstance().getSystemGralConf().getMyAccountConf();
        blockResend.setChecked(c.isResend());
        temporal.getConfTemporalSiempre().setChecked(c.isTimeMessageAlways());
        temporal.getConfTemporalTiempoDefault().setText(MessageUtil.CalcularTiempoFormater (c.getTimeMessageDefaultTime()));
        temporal.getConfTemporalTiempoDefault().setTag(c.getTimeMessageDefaultTime());

        confBlackMessageAttachMandatory.setChecked(c.isBlackMessageAttachMandatory());
        confBlackMessageAttachMandatoryReceived.setChecked(c.isBlackMessageAttachMandatoryReceived());
        descarga.getConfBlockMediaDownload().setChecked(c.isDownloadAttachAllowImage());
        //descarga.getConfDescargaAudio().setChecked(true);
        //descarga.getConfDescargaVideo().setChecked(true);
        //otras.getConfEscondermeDetalles().setChecked(c.isHideMyInDetails());
        otras.getConfGenOcultarEstado().setChecked(c.isHideMyMessageState());
    }

    public void setListener(){


        MenuAcordeonUtil.setActionMenu(this.getMenuAcordeon());

        reset.setOnClickListener(v -> setDefaultValues());


        this.temporal.getConfTemporalTiempoDefault().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime.getPickTimeView().loadValue(
                        Integer.parseInt(temporal.getConfTemporalTiempoDefault().getTag().toString())

                );
                selectTime.open();
            }
        }
        );
    }

    public ViewCallbackActionInterface getSaveOnClickListener(){
        return v -> SimpleErrorDialog.passwordValidation(activity, new SimpleErrorDialog.PasswordValidationI() {
            @Override
            public void action() {
                save();
            }

        });
    }
    private void save() {
        ProgressBarUtil.show(activity, activity.progressBar);
        MyAccountConfDTO dto =  buildDTO();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_GENERAL_CONFIGURATION);
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, activity.progressBar);
                        dto.setLock(SingletonValues.getInstance().getMyAccountConfDTO().getLock());
                        SingletonValues.getInstance().setMyAccountConfDTO(dto);

                        SingletonValues.getInstance().setMyAccountConfDTO(dto);
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
    private MyAccountConfDTO buildDTO() {
        MyAccountConfDTO dto = new MyAccountConfDTO();
        dto.setBlockResend(this.getBlockResend().isChecked());
        dto.setBlackMessageAttachMandatory(this.getConfBlackMessageAttachMandatory().isChecked());
        dto.setBlackMessageAttachMandatoryReceived
                (this.getConfBlackMessageAttachMandatoryReceived().isChecked());
        dto.setBlockMediaDownload(this.getDescarga().getConfBlockMediaDownload().isChecked());
        dto.setTimeMessageAlways(this.temporal.getConfTemporalSiempre().isChecked());
        dto.setTimeMessageDefaultTime(Long.parseLong(this.temporal.getConfTemporalTiempoDefault().getTag().toString()));
        dto.setHideMyMessageState(this.getOtras().getConfGenOcultarEstado().isChecked());

        return dto;
    }

    public void loadValues() {
        MyAccountConfDTO c = SingletonValues.getInstance().getMyAccountConfDTO();
        blockResend.setChecked(c.isBlockResend());
        temporal.getConfTemporalSiempre().setChecked(c.isTimeMessageAlways());
        confBlackMessageAttachMandatory.setChecked(c.isBlackMessageAttachMandatory());
        confBlackMessageAttachMandatoryReceived.setChecked(c.isBlackMessageAttachMandatoryReceived());
        descarga.getConfBlockMediaDownload().setChecked(c.isBlockMediaDownload());
        otras.getConfGenOcultarEstado().setChecked(c.isHideMyMessageState());

        temporal.getConfTemporalTiempoDefault().setText(MessageUtil.CalcularTiempoFormater (c.getTimeMessageDefaultTime()));
        temporal.getConfTemporalTiempoDefault().setTag(c.getTimeMessageDefaultTime());


    }
}
