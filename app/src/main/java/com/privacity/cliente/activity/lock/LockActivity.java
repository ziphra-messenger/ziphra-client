package com.privacity.cliente.activity.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.countdown.SingletonPasswordInMemoryLifeTime;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

public class LockActivity extends AppCompatActivity {

    private SecureFieldAndEye currentPassword;
    private Button lockIngresar;
    int reintentos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
/**
 * agregar reintentos
 /*
        SingletonMyAccountConfLockDownTimer.getInstance().setLocked(true);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_all_activities")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_all_activities"));
*/
        SingletonValues.getInstance().setShowingLock(true);

        lockIngresar = (Button) findViewById(R.id.lock_ingresar);


        this.currentPassword =new SecureFieldAndEye(null,
                (EditText) this.findViewById(R.id.password_eye_field),
                (ImageButton) this.findViewById(R.id.password_eye_show),
                (ImageButton) this.findViewById(R.id.password_eye_hide)
        );
        SecureFieldAndEyeUtil.setPasswordMaxLenght(currentPassword);
        SecureFieldAndEyeUtil.listener(currentPassword);
        reintentos=0;
        lockIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentPassword.getField().getText().toString().equals(SingletonValues.getInstance().getPassword())){
                    SingletonMyAccountConfLockDownTimer.getInstance().setLocked(false);
                    SingletonPasswordInMemoryLifeTime.getInstance().restart();
                    SingletonMyAccountConfLockDownTimer.getInstance().restart();

                    finish();
                }else{
                    reintentos++;
                    //currentPassword.getField().setError("Password Incorrecto");
                    Toast.makeText(LockActivity.this,"Password Incorrecto\nIntento " + reintentos + " de 3",Toast. LENGTH_SHORT).show();

                    if (reintentos >= 3 ){
                        lockIngresar.setEnabled(false);
                        try {
                            if (SingletonValues.getInstance().getMyAccountConfDTO().isLoginSkip()){
                                save();
                            }else{
                                Intent intent = new Intent("finish_all_activities");
                                LockActivity.this.sendBroadcast(intent);
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean(ProtocoloActionsEnum.PROTOCOLO_ACTION_GRUPO_BLOCK_REMOTO.toString())){
            this.findViewById(R.id.lock_bloqueo_remoto_mensaje).setVisibility(View.VISIBLE);
        }else{
            this.findViewById(R.id.lock_bloqueo_remoto_mensaje).setVisibility(View.INVISIBLE);
        }


    }
    private void save() throws Exception {


        boolean dto =  false;

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.PROTOCOLO_ACTION_MY_ACCOUNT_SAVE_LOGIN_SKIP);

        p.setObjectDTO(GsonFormated.get().toJson(dto));


        RestExecute.doit(this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        SingletonValues.getInstance().getMyAccountConfDTO().setLoginSkip(dto);
                        SharedPreferencesUtil.deleteSharedPreferencesUserPass(LockActivity.this);
                        LockActivity.this.finish();
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                        Intent intent = new Intent("finish_all_activities");
                        LockActivity.this.sendBroadcast(intent);
                        LockActivity.this.finish();
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        Intent intent = new Intent("finish_all_activities");
                        LockActivity.this.sendBroadcast(intent);
                        LockActivity.this.finish();
                    }

                });


    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SingletonMyAccountConfLockDownTimer.getInstance().setLocked(false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SingletonMyAccountConfLockDownTimer.getInstance().setLocked(true);

    }
}