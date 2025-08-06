package com.privacity.cliente.activity.lock;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.countdown.SingletonPasswordInMemoryLifeTime;
import com.privacity.common.enumeration.ProtocoloActionsEnum;

public class LockActivity extends AppCompatActivity {


    private SecureFieldAndEye currentPassword;
    private Button lockIngresar;



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
                if (action.equals(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES)) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES));
*/
        SingletonValues.getInstance().setShowingLock(true);

        lockIngresar = (Button) findViewById(R.id.lock_ingresar);


        this.currentPassword =new SecureFieldAndEye(null,
                (EditText) this.findViewById(R.id.password_eye_field),
                (ImageButton) this.findViewById(R.id.password_eye_show),
                (ImageButton) this.findViewById(R.id.password_eye_hide)
        );
        //SecureFieldAndEyeUtil.setPasswordMaxLenght(currentPassword);
        SecureFieldAndEyeUtil.listener(currentPassword);

        lockIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentPassword.getField().getText().toString().equals(SingletonValues.getInstance().getPassword())){
                    SingletonMyAccountConfLockDownTimer.getInstance().setLocked(false);
                    SingletonPasswordInMemoryLifeTime.getInstance().restart();
                    SingletonMyAccountConfLockDownTimer.getInstance().restart();
                    SingletonValues.getInstance().getPasswordReintentosReset();
                    finish();
                }else{
                    SingletonValues.getInstance().getPasswordReintentosAdd(LockActivity.this);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean(ProtocoloActionsEnum.GRUPO_BLOCK_REMOTO.toString())){
            this.findViewById(R.id.lock_bloqueo_remoto_mensaje).setVisibility(View.VISIBLE);
        }else{
            this.findViewById(R.id.lock_bloqueo_remoto_mensaje).setVisibility(View.INVISIBLE);
        }


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