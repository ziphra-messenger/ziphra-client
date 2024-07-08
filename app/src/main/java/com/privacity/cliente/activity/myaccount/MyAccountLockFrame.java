package com.privacity.cliente.activity.myaccount;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.LockDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

public class MyAccountLockFrame {

    protected final NumberPicker hora;
    protected final ProgressBar progressBar;
    protected NumberPicker segundos;
    protected final Button reset;
    protected final Button save;
    protected final Button defecto;

    protected final Activity activity;

    protected final MenuAcordeonObject menuAcordeon;
    protected final NumberPicker minutos;
    protected final Switch enabled;
    protected final TextView enHoras;

    public MyAccountLockFrame(Activity activity, ProgressBar progressBar) {
        this.progressBar = progressBar;
        reset = (Button) activity.findViewById(R.id.my_account_lock_reset);
        save = (Button) activity.findViewById(R.id.my_account_lock_save);
        defecto = (Button) activity.findViewById(R.id.my_account_lock_default);


        enabled = (Switch) activity.findViewById(R.id.my_account_lock_enabled);

        enHoras = (TextView) activity.findViewById(R.id.my_account_lock_en_horas);

        menuAcordeon = new MenuAcordeonObject(
                (Button) activity.findViewById(R.id.my_account_lock_title),
                (View) activity.findViewById(R.id.my_account_lock_content));

        this.activity = activity;

        hora = (NumberPicker) activity.findViewById(R.id.my_account_lock_picker_hora);
        hora.setMinValue(0);
        hora.setMaxValue(23);
        hora.setFormatter(TWO_DIGIT_FORMATTER);
        
        minutos = (NumberPicker) activity.findViewById(R.id.my_account_lock_picker_minutos);
        minutos.setMinValue(0);
        minutos.setMaxValue(59);
        minutos.setFormatter(TWO_DIGIT_FORMATTER);

        segundos = (NumberPicker) activity.findViewById(R.id.my_account_lock_picker_segundos);
        segundos.setMinValue(0);
        segundos.setMaxValue(59);
        segundos.setFormatter(TWO_DIGIT_FORMATTER);
    }

    public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER =
            new NumberPicker.Formatter() {

                @Override
                public String format(int value) {
                    // TODO Auto-generated method stub
                    return String.format("%02d", value);
                }
            };
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

        defecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadValuesGeneral(SingletonValues.getInstance().getSystemGralConf().getMyAccountConf().getLock());
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, "Mensaje de Error", e.getMessage());
                }

            }
        });
    }

    protected void save() throws Exception {

        if (!validationSave()) return;

        ProgressBarUtil.show(activity, progressBar);
        LockDTO dto =  buildDTO();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_LOCK);

        p.setObjectDTO(GsonFormated.get().toJson(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        ProgressBarUtil.hide(activity, progressBar);
                        SingletonValues.getInstance().getMyAccountConfDTO().setLock(dto);

                        SingletonMyAccountConfLockDownTimer.getInstance().restart(true);
                        Toast.makeText(activity,"Guardado",Toast. LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(activity, progressBar);

                    }

                });


    }

    protected boolean validationSave() {

        return true;
    }
    private LockDTO buildDTO() {
        LockDTO dto = new LockDTO();
        dto.setEnabled(enabled.isChecked());

        int value = hora.getValue()*60*60+
        minutos.getValue()*60+
        segundos.getValue();

        if (value != 0 ){
            dto.setSeconds(value);
        }
        return dto;
    }
    public void loadValues() {
        loadValuesGeneral(SingletonValues.getInstance().getMyAccountConfDTO().getLock());
    }

    protected void loadValuesGeneral(LockDTO lock) {
        int lockSeconds = lock.getSeconds();

        hora.setValue(MessageUtil.CalcularTiempoFormaterSoloHora(lockSeconds));
        minutos.setValue(MessageUtil.CalcularTiempoFormaterSoloMinutos(lockSeconds));
        segundos.setValue(MessageUtil.CalcularTiempoFormaterSoloSegundos(lockSeconds));

        enabled.setChecked(lock.isEnabled());


    }
}

