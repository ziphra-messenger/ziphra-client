package com.privacity.cliente.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.activity.loading.LoadingActivity;
import com.privacity.cliente.activity.mainconfiguracion.MainConfiguracionActivity;
import com.privacity.cliente.activity.registro.RegistroActivity;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.rest.AsyncCaller;
import com.privacity.cliente.rest.CallbackActionRest;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.ServerConfRest;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonLoginValues;
import com.privacity.cliente.singleton.SingletonSessionFinish;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.countdown.LoginCountDownTimer;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.reconnect.SingletonReconnect;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesEnum;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.request.LoginRequestDTO;
import com.privacity.common.enumeration.EnvironmentEnum;

import org.springframework.http.ResponseEntity;


public class MainActivity extends AppCompatActivity {


    private EditText username;
    private EditText password;
    private Button login;
    private Button ver;

    private ProgressBar progressBar;
    private boolean noValidate;

    private MainActivityNoPass mainActivityNoPass;
    private LoginCountDownTimer timer;

    @Override
    protected void onResume() {


        super.onResume();
        ProgressBarUtil.hide(MainActivity.this, progressBar);
        SingletonSessionFinish.getInstance().cancel();

        setDefaultServer();


        //new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:"+"com.privacity.cliente"));
     }

    private void setDefaultServer() {

        SingletonServer.getInstance().setAppServer(SharedPreferencesUtil.getAppServer(this));
        SingletonServer.getInstance().setWsServer(SharedPreferencesUtil.getWsServer(this));
        SingletonServer.getInstance().setHelpServer(SharedPreferencesUtil.getAppServer(this));
        SingletonServer.getInstance().setDeveloperMode(SharedPreferencesUtil.getDeveloperMode(this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        if (noValidate){

            SingletonValues.getInstance().setLogout(true);
            Intent intent = new Intent("finish_application");
            this.sendBroadcast(intent);
            this.finish();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }else{

/*            SingletonValues.getInstance().setLogout(false);
            Intent intent = new Intent("finish_application");
            this.sendBroadcast(intent);*/
            this.finish();

            //System.exit(0);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Privacity");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#000000"));
        actionBar.setBackgroundDrawable(colorDrawable);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_application")) {
                    finish();
                }
            }
        };

        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(), PackageManager.GET_META_DATA);

            String valor = ai.metaData.getString("privacity_environment");

            System.out.println("-------- Entorno : " + valor);
            SingletonServer.getInstance().setEnvironment(EnvironmentEnum.valueOf(valor));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        registerReceiver(broadcastReceiver, new IntentFilter("finish_application"));

        System.out.println( " ---> " + SingletonValues.getInstance().isLogout());

        chooseView();


    }

    public void chooseView() {


        if (selectAction()) return;

        if (!SingletonValues.getInstance().isLogout() &&
            SingletonValues.getInstance().getUsuario() != null){

            Intent i = new Intent(this, GrupoActivity.class);
            startActivity(i);
            this.finish();
            return;
        }

        resetSingletons();

        initMainView();
    }

    private void initMainView() {
        SingletonSessionFinish.getInstance().setup(this);




        // Set BackgroundDrawable


        progressBar = (ProgressBar) findViewById(R.id.gral_progress_bar);
        ProgressBarUtil.hide(MainActivity.this, progressBar);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //activity = this;
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        SecureFieldAndEyeUtil.listener(new SecureFieldAndEye(null,username,
                (ImageButton) findViewById(R.id.username_eye_show),
                (ImageButton) findViewById(R.id.username_eye_hide)));

        SecureFieldAndEyeUtil.listener(new SecureFieldAndEye(null,password,
                (ImageButton) findViewById(R.id.password_eye_confirmation_show),
                (ImageButton) findViewById(R.id.password_eye_confirmation_hide)));


        login = (Button) findViewById(R.id.lock_ingresar);

        ver = (Button) findViewById(R.id.ver);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MainActivity.this.callLoginRest(username.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //((Button) findViewById(R.id.C))
                ;
        ((Button) SingletonServer.getInstance().setVisibility( findViewById(R.id.C) )).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("1");
                password.setText("1");
                try {
                    callLoginRest(username.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((Button) SingletonServer.getInstance().setVisibility( findViewById(R.id.C2) )).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("2");
                password.setText("2");
                try {
                    callLoginRest(username.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((Button) SingletonServer.getInstance().setVisibility( findViewById(R.id.C3) )).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("3");
                password.setText("3");
                try {
                    callLoginRest(username.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((Button) SingletonServer.getInstance().setVisibility( findViewById(R.id.C4) )).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("4");
                password.setText("4");
                try {
                    callLoginRest(username.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(i);

            }
        });

/*        username.setText("5");
        password.setText("5");
        try {
            callLoginRest();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public boolean selectAction() {
        if (!SingletonValues.getInstance().isLogout()){
            resetSingletons();
            String userP  = SharedPreferencesUtil.getUserPass(this, SharedPreferencesEnum.USER);
            String passwordP = SharedPreferencesUtil.getUserPass(this, SharedPreferencesEnum.PASSWORD);

            if ( userP != null & passwordP != null) {
                setDefaultServer();
                noValidate=true;
                try {
                    setContentView(R.layout.activity_main_nopass);
                    mainActivityNoPass = new MainActivityNoPass(MainActivity.this);
                    mainActivityNoPass.setVisibilityEntry();
                    callLoginRest(
                            userP,
                            passwordP);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                setContentView(R.layout.activity_main);
                noValidate=false;
            }
        }else{
            setContentView(R.layout.activity_main);
            noValidate=false;

        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!SingletonServer.getInstance().isDeveloper()) return false;

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        Intent i = new Intent(MainActivity.this, MainConfiguracionActivity.class);
        startActivity(i);
        return true;
    }

    private static void resetSingletons() {
        /*TODO
           CERRAR TODAS LAS VENTANAS CON UNA SEÑAL DE FINISH*/

        try {
            SingletonValues.getInstance().getWebSocket().disconnectStomp();
        } catch (Exception e) {

        }

        Observers.reset();
        Singletons.reset();

        SingletonValues.getInstance().reset();

    }

    private boolean validarNotEmpty() {

        if (this.noValidate) return true;

        boolean validation = true;
        if (username.getText().toString().equals("")) {
            username.setError("No puede estar vacío");
            validation = false;
        }else{
            username.setError(null);
        }

        if (password.getText().toString().equals("")) {
            password.setError("No puede estar vacío");
            validation = false;
        }else{
            password.setError(null);
        }
        return validation;
    }



    private void callLoginRest (String user, String password) throws Exception {
        if (!validarNotEmpty()) return;

        ProgressBarUtil.show(this, progressBar);
        if (!SingletonReconnect.isOnline(this)){
            SimpleErrorDialog.errorDialog( MainActivity.this,"Sin Internet", "Verifique su conexion a internet.");
            ProgressBarUtil.hide(this, progressBar);
            return;
        }


        AsyncCaller t = new AsyncCaller(new CallbackActionRest() {
            @Override
            public void onError() {
                ProgressBarUtil.hide(MainActivity.this, progressBar);
                SimpleErrorDialog.errorDialog( MainActivity.this,"Servidor", "El servidor configurado no responde: " + SingletonServer.getInstance().getAppServer());
            }

            @Override
            public void onSucess() {
                try {

                    checkServerRunning(user, password);
                } catch (Exception e) {
                    SimpleErrorDialog.errorDialog( MainActivity.this,"ERROR", "Ejecutando Rest " + e.getMessage());
                }
            }
        });
        t.execute();
    }
    private void checkServerRunning(String user, String password) throws Exception {



        this.timer = new LoginCountDownTimer(this);
        timer.restart();

        ServerConfRest.getTime(this, loginCallbackRest(user, password), new InnerCallbackRest() {
            @Override
            public void action(Context context) {
                try {
                    callLoginRestInnerCallBack(user, password);
                } catch (Exception e) {
                    ProgressBarUtil.changeState(MainActivity.this, progressBar);
                    timer.stop();
                    e.printStackTrace();
                }
            }
        });
    }





//    private boolean checkServerRunning() {
//        try{
//            String url =SingletonServer.getInstance().getAppServer();
//            int timeout = 3000;
//            URL myUrl = new URL(url);
//            URLConnection connection = myUrl.openConnection();
//            connection.setConnectTimeout(timeout);
//            connection.connect();
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    private void callLoginRestInnerCallBack(String user, String password) throws Exception {


        String userP;
        String passwordP;
        if ( noValidate ){
            userP  = SharedPreferencesUtil.getUserPass(this, SharedPreferencesEnum.USER);
            passwordP = SharedPreferencesUtil.getUserPass(this, SharedPreferencesEnum.PASSWORD);
        }else{
            userP  = user;
            passwordP = password;
        }


        Gson gson = GsonFormated.get();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_AUTH);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_AUTH_LOGIN);

        LoginRequestDTO t = new LoginRequestDTO();
        t.setUsername(EncryptUtil.toHash(userP));
        t.setPassword(EncryptUtil.toHash(passwordP));

        SingletonValues.getInstance().setUsernameHash(t.getUsername());
        SingletonValues.getInstance().setPasswordHash(t.getPassword());
        SingletonValues.getInstance().setUsernameNoHash(userP);
        SingletonValues.getInstance().setPasswordNoHash(passwordP);
        p.setObjectDTO(gson.toJson(t));


        RestExecute.doitPublic(MainActivity.this, p, loginCallbackRest(userP, passwordP)
                , SingletonLoginValues.getInstance().getAEStoUse(),
                SingletonLoginValues.getInstance().getAEStoSend()
        );
    }

    private CallbackRest loginCallbackRest(String user, String password) {
        CallbackRest r = new CallbackRest() {

            @Override
            public void response(ResponseEntity<ProtocoloDTO> response) {




                timer.stop();
                MainActivity.this.finish();

                Intent i = new Intent(MainActivity.this, LoadingActivity.class);

                i.putExtra("protocoloDTO", response.getBody().getObjectDTO());
                i.putExtra("username", user);
                i.putExtra("password", password);

                startActivity(i);


            }

            @Override
            public void onError(ResponseEntity<ProtocoloDTO> response) {
//                ProgressBarUtil.hide(MainActivity.this, progressBar);

//                  if (noValidate) mainActivityNoPass.setVisibilityError();

//                setContentView(R.layout.activity_main_nopass);
//                try {
//                    callLoginRest(user, password);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

            @Override
            public void beforeShowErrorMessage(String msg) {
                timer.stop();
                if (noValidate) mainActivityNoPass.setVisibilityError();
                ProgressBarUtil.hide(MainActivity.this, progressBar);
            }
        };
        return r;
    }

    @Override
    public void finish() {
        super.finish();

/*        Intent intent = new Intent("finish_all_activities");
        this.sendBroadcast(intent);*/

       // System.exit(0);
    }
}