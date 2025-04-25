package com.privacity.cliente.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.IntentCompat;

import com.neovisionaries.i18n.LanguageCode;
import com.privacity.cliente.BuildConfig;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.DeveloperElements;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.activity.mainconfiguracion.MainConfiguracionActivity;
import com.privacity.cliente.activity.reconnect.ReconnectFrame;
import com.privacity.cliente.activity.registro.RegistroActivity;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.common.constants.IntentConstant;
import com.privacity.cliente.common.validations.FieldsValidations;
import com.privacity.cliente.frame.help.HelpFrame;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.restcalls.ServerConfRest;
import com.privacity.cliente.singleton.SingletonSessionFinish;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.countdown.LoginCountDownTimer;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.localconfiguration.SingletonLang;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesEnum;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.cliente.util.activities.IntentUtil;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.enumeration.EnvironmentEnum;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;


public class MainActivi2ty extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Locale myLocale;
    String currentLanguage = SingletonLang.getInstance().get();
    private EditText username;
    private EditText password;
    private Button login;
    private Button registrarse;
    @Getter
    private ProgressBar progressBar;
    @Getter
    private boolean noValidate;
    @Getter
    private MainActivityNoPass mainActivityNoPass;
    @Getter
    @Setter
    private LoginCountDownTimer timer;
    private TextView tvVersion;
    private View contentAll;
    //private String version="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            //Singletons.resetAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SingletonSessionClosing.getInstance().setClosing(false);
        SingletonSessionClosing.getInstance().setClose(true);
        SingletonSessionClosing.getInstance().setCloseApp(false);

//        SingletonValues.getInstance().reset();
        SingletonCurrentActivity.getInstance().set(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        //setLocale(SingletonLang.getInstance().get(this));
        currentLanguage = getIntent().getStringExtra("currentLang");
        initActionBar();
//getString()
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(), PackageManager.GET_META_DATA);

            String valor = ai.metaData.getString("privacity_environment");
//            version = ai.metaData.getString("privacity_version");
//            Log.i(TAG,"Version: " + version);
            Log.i(TAG,"Entorno: " + valor);
            SingletonServer.getInstance().setEnvironment(EnvironmentEnum.valueOf(valor));




        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
      //  setDefaultServer();
        initBroadCast();

        System.out.println( " ---> " + SingletonValues.getInstance().isLogout());

        chooseView();

    }

    private static void resetSingletons() {
        /*TODO
           CERRAR TODAS LAS VENTANAS CON UNA SEÑAL DE FINISH*/
        try {
           // SingletonValues.getInstance().getWebSocket().disconnectStomp();
        } catch (Exception e) {

        }

        //Singletons.resetAll();


    }

    private void initBroadCast() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BroadcastConstant.BROADCAST__FINISH_APPLICATION)) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_APPLICATION));
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar==null)return;
        actionBar.setTitle(getString(R.string.general__title));
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#000000"));
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    public static void restart(Context context){
        Intent mainIntent = IntentCompat.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER);
        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(mainIntent);
        System.exit(0);
    }
    @Override
    public void finish() {
        super.finish();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        // Observers.grupo().dessuscribirse(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDefaultServer();
        ServerConfRest.getTime(this, null, new InnerCallbackRest() {
            @Override
            public void action(Context context) {
            }
        });

    }

    @Override
    protected void onResume() {


        super.onResume();
        //restart(this);

        if (SingletonSessionClosing.getInstance().isClosing()){
            while (!SingletonSessionClosing.getInstance().isClose()){}
            SingletonSessionClosing.getInstance().setClosing(false);
            SingletonSessionClosing.getInstance().setClose(false);


/*            Intent i = new Intent(MainActivity.this, MainActivity.class);

            startActivity(i);
            this.finish();*/
        }
        if (SingletonSessionClosing.getInstance().isCloseApp()){
            SingletonSessionClosing.getInstance().setCloseApp(false);
            //System.exit(0);
            //finishAffinity();
            finish();
        }else {
            ProgressBarUtil.hide(MainActivi2ty.this, progressBar);
            SingletonSessionFinish.getInstance().cancel();



            new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + "com.privacity.cliente"));


        }



  /*      SingletonCurrentActivity.getInstance().set(this);
        String username2 = this.getIntent().getStringExtra(IntentConstant.USERNAME);
        String password2 = this.getIntent().getStringExtra(IntentConstant.PASSWORD );


        if (username2 != null && password2 != null){
*//*            username.setText(username2);
            password.setText(password2);*//*

            try {
                //MainActivity.this.callLoginRest(username2, password2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SingletonLang.getInstance().get(this).equals(currentLanguage)){
            setLocale(SingletonLang.getInstance().get(this));
        }*/


//            setLocale(SharedPreferencesUtil.getLanguage(this));
//        }else{
//            currentLanguage=SharedPreferencesUtil.getLanguage(this);
//        }




    }


    private void setDefaultServer() {


        SingletonServer.getInstance().setAppServer(SharedPreferencesUtil.getAppServerToUse(this));
        SingletonServer.getInstance().setWsServer(SharedPreferencesUtil.getWsServerToUse(this));
        SingletonServer.getInstance().setHelpServer(SharedPreferencesUtil.getAppServerToUse(this));
//        SingletonServer.getInstance().setDeveloperMode(SharedPreferencesUtil.getDeveloperMode(this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        if (noValidate){

            SingletonValues.getInstance().setLogout(true);
            Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_APPLICATION);
            this.sendBroadcast(intent);
            this.finish();

            Intent i = new Intent(this, MainActivi2ty.class);
            startActivity(i);
        }else{

            SingletonValues.getInstance().setLogout(false);
            Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_APPLICATION);
            this.sendBroadcast(intent);
            this.finish();

            //System.exit(0);
        }


    }

    public void chooseView() {
        if (selectAction()) return;

        if (!SingletonValues.getInstance().isLogout() &&
                Singletons.usuario().getUsuario() != null){

            Intent i = new Intent(this, GrupoActivity.class);
            //startActivity(i);
            //this.finish();
            return;
        }

        resetSingletons();

        initMainView();
    }

    private void initMainView() {






        SingletonSessionFinish.getInstance().setup(this);



        contentAll  = findViewById(R.id.main_activity__scroll__content_all);

        tvVersion = (TextView) findViewById(R.id.tv_main_login__version);
        tvVersion.setText(getString(R.string.main_login__version, BuildConfig.VERSION_NAME));
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        login = GetButtonReady.get(this, R.id.lock_ingresar, "test", new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                try {
                    MainActivi2ty.this.callLoginRest(username.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        registrarse = GetButtonReady.get(this,R.id.main_login_registrarse);



        progressBar = (ProgressBar) findViewById(R.id.common__progress_bar);
        ProgressBarUtil.hide(MainActivi2ty.this, progressBar);
        initListener();





    }

    private void initListener() {

        SecureFieldAndEye usuarioSecureFieldAndEye  = new SecureFieldAndEye(null,username,
                (ImageButton) findViewById(R.id.username_eye_show),
                (ImageButton) findViewById(R.id.username_eye_hide));
        SecureFieldAndEyeUtil.listener(usuarioSecureFieldAndEye);

        SecureFieldAndEye passwordSecureFieldAndEye  = new SecureFieldAndEye(null,password,
                (ImageButton) findViewById(R.id.password_eye_confirmation_show),
                (ImageButton) findViewById(R.id.password_eye_confirmation_hide));
                SecureFieldAndEyeUtil.listener(passwordSecureFieldAndEye);




        setListenerRegistrarse(registrarse);
        setListenerRegistrarse(findViewById(R.id.main_login_content__registrarse));
        new ReconnectFrame(this);
// Check whether the runtime version is at least Android 5.0.


        //((Button) findViewById(R.id.C))
        DeveloperElements.developerLogin(this,usuarioSecureFieldAndEye, passwordSecureFieldAndEye);

        ((com.google.android.material.floatingactionbutton.FloatingActionButton)findViewById(R.id.develop_lock)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( SingletonServer.getInstance().isDeveloper()) {
                    SingletonServer.getInstance().setEnvironment(EnvironmentEnum.QA);
                }else {
                    SingletonServer.getInstance().setEnvironment(EnvironmentEnum.DEVELOPER);
                }
                Toast.makeText(MainActivi2ty.this,SingletonServer.getInstance().getEnvironment().name(),Toast. LENGTH_SHORT).show();
               //DeveloperElements.developerLogin(MainActivity.this,usuarioSecureFieldAndEye, passwordSecureFieldAndEye);
            }
        });
    }

    private void setListenerRegistrarse(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.callActivity(RegistroActivity.class);
                //MainActivity.this.finish();

            }
        });
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public boolean selectAction() {
        if (!SingletonValues.getInstance().isLogout()){
            resetSingletons();

            Intent intent = this.getIntent();

            String userP  = SharedPreferencesUtil.getUserPass(this, SharedPreferencesEnum.USER);
            String passwordP = SharedPreferencesUtil.getUserPass(this, SharedPreferencesEnum.PASSWORD);

            String username = intent.getStringExtra(IntentConstant.USERNAME);
            String password = intent.getStringExtra(IntentConstant.PASSWORD );
            if (username != null && password != null){
                userP=username;
                passwordP=password;
            }


            if ( userP != null & passwordP != null) {
                setDefaultServer();
                noValidate=true;
                try {
                    setContentView(R.layout.activity_main_nopass);
                    mainActivityNoPass = new MainActivityNoPass(MainActivi2ty.this);
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



    private boolean validarNotEmpty() {

        if (this.noValidate) return true;
        FieldsValidations.notEmptySetError(username, getString(R.string.main_login__validate_usuario__empty));
        FieldsValidations.notEmptySetError(password, getString(R.string.main_login__validate_password__empty));
        return (FieldsValidations.notEmptySetError(username, getString(R.string.main_login__validate_usuario__empty))
                && FieldsValidations.notEmptySetError(password, getString(R.string.main_login__validate_password__empty)));
    }


    public void callLoginRest(String user, String password) throws Exception {
        if (!validarNotEmpty()) return;

        CallRestLogin.callLoginRest(this, user,password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        if ( itemMenu.getItemId() == R.id.main_login__lang_espanol) {
            setLocale(LanguageCode.es.toString());}
        if ( itemMenu.getItemId() == R.id.main_login__lang_english){
            setLocale(LanguageCode.en.toString());}
        if ( itemMenu.getItemId() == R.id.main_login__lang_portuguese){
            setLocale(LanguageCode.pt.toString());}
        if ( itemMenu.getItemId() == R.id.main_menu_configuracion){
            Intent i = new Intent(MainActivi2ty.this, MainConfiguracionActivity.class);
            startActivity(i);
        }
        return true;
    }
    public void setLocale(String idioma) {
        if (currentLanguage==null) currentLanguage="";
        if  (idioma.equals(SingletonLang.getInstance().get(this))) return;

        //if  (currentLanguage.equals(idioma)) return;
        SingletonLang.getInstance().save(this, idioma);
        myLocale = new Locale(idioma);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent refresh = new Intent(this, MainActivi2ty.class);
        refresh.putExtra("currentLang", idioma);
        currentLanguage=idioma;
        startActivity(refresh);
        finish();


    }

    public void goToHelp(View v){
        HelpFrame g = new HelpFrame();

        g.show(tvVersion.getText().toString());
        //String url = ((View)v.getParent().getParent()).getTag().toString();
        //UserHelperUtil.open(this, url);
    }

    public void cleanForm() {
        username.setText("");
       password.setText("");
    }
}