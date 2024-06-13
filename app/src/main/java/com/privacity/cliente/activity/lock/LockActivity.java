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
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;

public class LockActivity extends AppCompatActivity {

    private SecureFieldAndEye currentPassword;
    private Button lockIngresar;
    int reintentos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
/**
 * agregar reintentos
 */
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

        SingletonValues.getInstance().setShowingLock(true);

        lockIngresar = (Button) findViewById(R.id.lock_ingresar);


        this.currentPassword =new SecureFieldAndEye(null,
                (EditText) this.findViewById(R.id.password_eye_field),
                (ImageButton) this.findViewById(R.id.password_eye_show),
                (ImageButton) this.findViewById(R.id.password_eye_hide)
        );
        SecureFieldAndEyeUtil.setPasswordMaxLenght(currentPassword);
        SecureFieldAndEyeUtil.listener(currentPassword);

        lockIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentPassword.getField().getText().toString().equals(SingletonValues.getInstance().getPassword())){
                    SingletonValues.getInstance().passwordCountDownTimerRestart();
                    SingletonValues.getInstance().setShowingLock(false);
                    finish();
                }else{
                    reintentos++;
                    //currentPassword.getField().setError("Password Incorrecto");
                    Toast.makeText(LockActivity.this,"Password Incorrecto\nIntento " + reintentos + " de 3",Toast. LENGTH_SHORT).show();

                    if (reintentos == 3 ){
                        Intent intent = new Intent("finish_all_activities");
                        LockActivity.this.sendBroadcast(intent);
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}