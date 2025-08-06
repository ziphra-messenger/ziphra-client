package com.privacity.cliente.activity.myaccount;

import android.app.Activity;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.picktime.PickTimeView;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.dto.LockDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import static android.view.View.GONE;
import static android.view.View.OnTouchListener;
import static android.view.View.VISIBLE;

public class MyAccountLockFrame {



    protected final ProgressBar progressBar;
    //protected final Button reset;
    protected final Button save;
    protected final Button defecto;
    protected final Activity activity;
    protected final MenuAcordeonObject menuAcordeon;

    protected final Switch enabled;
    protected final TextView enHoras;
    private final TextView validate;

    private PickTimeView pickTimeView;


    public MyAccountLockFrame(Activity activity, ProgressBar progressBar) {
        this.progressBar = progressBar;
        //  reset = (Button) activity.findViewById(R.id.my_account_lock_reset);
        save = GetButtonReady.get(activity, R.id.my_account_lock_save,new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                try {
                    save.setEnabled(false);
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        });
        defecto =GetButtonReady.get(activity, R.id.my_account_lock_default,new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                try {
                    save.setEnabled(false);
                    loadValuesGeneral(SingletonServerConfiguration.getInstance().getSystemGralConf().getMyAccountConf().getLock());
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        });


        enabled = (Switch) activity.findViewById(R.id.my_account_lock_enabled);

        enHoras = (TextView) activity.findViewById(R.id.my_account_lock_en_horas);
        validate = (TextView) activity.findViewById(R.id.my_account_lock__validate);
        validate.setTextColor(Color.RED);

        validate.setText(activity.getString(R.string.frame_my_account_lock_validate_min_seconds, SingletonServerConfiguration.getInstance().getSystemGralConf().getMyAccountConf().getLock().getMinSecondsValidation()+""));
        menuAcordeon = new MenuAcordeonObject(
                GetButtonReady.get(activity, R.id.my_account_lock_title),
                (View) activity.findViewById(R.id.my_account_lock_content));

        this.activity = activity;

        pickTimeView = new PickTimeView(
        (NumberPicker) activity.findViewById(R.id.my_account_lock_picker_hora),
        (NumberPicker) activity.findViewById(R.id.my_account_lock_picker_minutos),
        (NumberPicker) activity.findViewById(R.id.my_account_lock_picker_segundos)
        );
        pickTimeView.initViewFullValues();
    }

    public void setListener(){

        pickTimeView.getHora().setOnTouchListener(getSaveEnabledListener());
        pickTimeView.getMinutos().setOnTouchListener(getSaveEnabledListener());
        pickTimeView.getSegundos().setOnTouchListener(getSaveEnabledListener());
        MenuAcordeonUtil.setActionMenu(menuAcordeon);




//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    loadValues();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
//                }
//
//            }
//        });

    }

    @NonNull
    private OnTouchListener getSaveEnabledListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                actionEnabled();
                return false;
            }
        };
    }



    private void actionEnabled() {
        if (validationSave()) {
            save.setEnabled(true);
            validate.setVisibility(GONE);
        } else {
            validate.setVisibility(VISIBLE);
            save.setEnabled(false);
        }
    }

    protected void save() throws Exception {

        if (!validationSave()) return;

        ProgressBarUtil.show(activity, progressBar);
        LockDTO dto =  buildDTO();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_LOCK);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, progressBar);
                        SingletonValues.getInstance().getMyAccountConfDTO().setLock(dto);
                        save.setEnabled(false);
                        SingletonMyAccountConfLockDownTimer.getInstance().restart(true);
                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(activity, progressBar);

                    }

                });


    }

    protected boolean validationSave() {
        LockDTO dto = buildDTO();
        if (dto.isEnabled()){
            if ( dto.getSeconds() < SingletonServerConfiguration.getInstance().getSystemGralConf().getMyAccountConf().getLock().getMinSecondsValidation()){

                validate.setVisibility(VISIBLE);
                return false;
            }
        }
        validate.setVisibility(GONE);
        return true;
    }
    private LockDTO buildDTO() {
        LockDTO dto = new LockDTO();
        dto.setEnabled(enabled.isChecked());

        int value = pickTimeView.getValue();


            dto.setSeconds(value);

        return dto;
    }
    public void loadValues() {
        loadValuesGeneral(SingletonValues.getInstance().getMyAccountConfDTO().getLock());
    }

    protected void loadValuesGeneral(LockDTO lock) {
        int lockSeconds = lock.getSeconds();

        pickTimeView.loadValue(lockSeconds);

        save.setEnabled(false);


    }
}

