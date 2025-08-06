package com.privacity.cliente.activity.grupoinfo.lock;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.common.component.picktime.PickTimeView;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.common.dto.GrupoGralConfPasswordDTO;
import com.privacity.common.dto.LockDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

public class GrupoInfoLock {

    private SecureFieldAndEye newPassword;
    private SecureFieldAndEye confirmationPassword;
    private Switch passwordEnabled;
    private Switch encryptKeyDefaultEnabled;


    protected ProgressBar progressBar;
    protected PickTimeView pickTimeView;
    protected Button reset;
    protected Button saveLock;
    protected Button savePassword;
    protected Button defecto;

    protected Activity activity;

    protected MenuAcordeonObject menuAcordeon;

    protected Switch enabledLock;
    protected TextView enHoras;
/*
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
 */
    public GrupoInfoLock(Activity activity, ProgressBar progressBar) {

        this.progressBar = progressBar;
        reset = GetButtonReady.get(activity,R.id.grupo_info_lock_reset);
        saveLock = GetButtonReady.get(activity,R.id.grupo_info_lock_save,new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                SimpleErrorDialog.passwordValidation(activity, new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        try {
                            saveLock();
                        } catch (Exception e) {
                            e.printStackTrace();
                            SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                        }
                    }

                });

            }
        });
        savePassword =GetButtonReady.get(activity,R.id.grupo_info_lock_password_save,new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                SimpleErrorDialog.passwordValidation(activity, new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        try {
                            savePassword();
                        } catch (Exception e) {
                            e.printStackTrace();
                            SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                        }
                    }

                });

            }
        });
        defecto = GetButtonReady.get(activity,R.id.grupo_info_lock_default);


        enabledLock = (Switch) activity.findViewById(R.id.grupo_info_lock_enabled);

        enHoras = (TextView) activity.findViewById(R.id.grupo_info_lock_en_horas);

        menuAcordeon = new MenuAcordeonObject(
                GetButtonReady.get(activity,R.id.grupo_info_lock_title),
                (View) activity.findViewById(R.id.grupo_info_lock_content));

        this.activity = activity;

        pickTimeView = new PickTimeView(
                (NumberPicker) activity.findViewById(R.id.grupo_info_lock_picker_hora),
                (NumberPicker) activity.findViewById(R.id.grupo_info_lock_picker_minutos),
                (NumberPicker) activity.findViewById(R.id.grupo_info_lock_picker_segundos)
        );
        pickTimeView.initViewFullValues();


        this.passwordEnabled = (Switch) activity.findViewById(R.id.grupo_info_password_enabled);
        this.encryptKeyDefaultEnabled = (Switch) activity.findViewById(R.id.grupo_info_lock_use_extra_pass_default_enabled);



        this.newPassword=new SecureFieldAndEye(
                (LinearLayout) activity.findViewById(R.id.password_eye_change_content),
                (EditText) activity.findViewById(R.id.password_eye_change_field),
                (ImageButton) activity.findViewById(R.id.password_eye_change_show),
                (ImageButton) activity.findViewById(R.id.password_eye_change_hide)
        );

        this.confirmationPassword=new SecureFieldAndEye(
                (LinearLayout) activity.findViewById(R.id.password_eye_confirmation_content),
                (EditText) activity.findViewById(R.id.password_eye_confirmation_field),
                (ImageButton) activity.findViewById(R.id.password_eye_confirmation_show),
                (ImageButton) activity.findViewById(R.id.password_eye_confirmation_hide)
        );


        setListener();
        loadValues();
        //SecureFieldAndEyeUtil.setPasswordMaxLenght(newPassword);
        //SecureFieldAndEyeUtil.setPasswordMaxLenght(confirmationPassword);
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





        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadValues();
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        });

        defecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadValuesGeneral(SingletonServerConfiguration.getInstance().getSystemGralConf().getMyAccountConf().getLock());
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(activity, activity.getString(R.string.general__error_message), e.getMessage());
                }

            }
        });
    }


    protected void savePassword() throws Exception {

        if (!validationSavePassword()) return;



        ProgressBarUtil.show(activity, progressBar);
        GrupoDTO dto = buildPasswordDTO();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_GRAL_CONF_PASSWORD);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, progressBar);
                        SingletonValues.getInstance().getGrupoSeleccionado().getPassword().setPassword(dto.getPassword().getPassword());
                        //SingletonValues.getInstance().getGrupoSeleccionado().setLock(dto.getLock());


                            Observers.passwordGrupo().passwordSet(SingletonValues.getInstance().getGrupoSeleccionado());




                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();
                        /** TODO
                         * agregar q se active el timer
                         */
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
    private GrupoDTO buildPasswordDTO() {
        GrupoDTO g = new GrupoDTO();
        g.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        g.setGralConfDTO(new GrupoGralConfDTO());
        g.setPassword(new GrupoGralConfPasswordDTO() );
        //g.getPassword().setEnabled(passwordEnabled.isChecked());

        //g.getPassword().setExtraEncryptDefaultEnabled( encryptKeyDefaultEnabled.isChecked());
        g.getPassword().setPassword(newPassword.getField().getText().toString());
        //g.setLock(dto);

        return g;
    }

    private boolean validationSavePassword() {
        boolean validationNew = SecureFieldAndEyeUtil.passwordValidationGrupo(activity, newPassword);

        if (!validationNew) return false;

        return SecureFieldAndEyeUtil.passwordCompareNewAndConfirmation(activity, newPassword, confirmationPassword);

    }

    protected void saveLock() throws Exception {

        if (!validationSaveLock()) return;

        ProgressBarUtil.show(activity, progressBar);
        GrupoDTO dto =  buildLockDTO();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_GENERAL_CONFIGURATION_LOCK);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));


        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(activity, progressBar);
                        SingletonValues.getInstance().getGrupoSeleccionado().setPassword(dto.getPassword());
                        SingletonValues.getInstance().getGrupoSeleccionado().setLock(dto.getLock());

                        if (SingletonValues.getInstance().getGrupoSeleccionado().getCountDownTimer() != null){
                            SingletonValues.getInstance().getGrupoSeleccionado().getCountDownTimer().restart();
                            Observers.passwordGrupo().passwordSet(SingletonValues.getInstance().getGrupoSeleccionado());
                        }

                        Toast.makeText(activity,activity.getString(R.string.general__saved),Toast. LENGTH_SHORT).show();
                        /** TODO
                         * agregar q se active el timer
                         */
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

    protected boolean validationSaveLock() {
        return true;
    }
    private GrupoDTO buildLockDTO() {


        LockDTO dto = new LockDTO();
        dto.setEnabled(enabledLock.isChecked());

        int value =pickTimeView.getValue();

        if (value != 0 ){
            dto.setSeconds(value);
        }

        GrupoDTO g = new GrupoDTO();
        g.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        g.setGralConfDTO(new GrupoGralConfDTO());
        g.setPassword(new GrupoGralConfPasswordDTO() );
        g.getPassword().setEnabled(passwordEnabled.isChecked());

        g.getPassword().setExtraEncryptDefaultEnabled( encryptKeyDefaultEnabled.isChecked());
        //g.getPassword().setPassword(newPassword.getField().getText().toString());
        g.setLock(dto);

        return g;

    }
    public void loadValues() {
        Grupo p = SingletonValues.getInstance().getGrupoSeleccionado();
        loadValuesGeneral(p.getLock());

        encryptKeyDefaultEnabled.setChecked(p.getPassword().isExtraEncryptDefaultEnabled());
        passwordEnabled.setChecked(p.getPassword().isEnabled());

    }

    protected void loadValuesGeneral(LockDTO lock) {
        int lockSeconds = ((lock.getSeconds() == 0) ? 30 : lock.getSeconds());

        pickTimeView.loadValue(lockSeconds);
        enabledLock.setChecked(lock.isEnabled());


    }
}

