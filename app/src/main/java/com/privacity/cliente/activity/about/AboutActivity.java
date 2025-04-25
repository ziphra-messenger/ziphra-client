package com.privacity.cliente.activity.about;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.dto.servergralconf.SGCAESDTO;
import com.privacity.common.dto.servergralconf.SGCAESSimple;
import com.privacity.common.dto.servergralconf.SGCAsymEncrypt;
import com.privacity.common.dto.servergralconf.SGCAuth;
import com.privacity.common.dto.servergralconf.SGCServerInfo;

public class AboutActivity extends CustomAppCompatActivity {

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Acerca de Privacity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        {
            SGCServerInfo conf = SingletonServerConfiguration.getInstance().getSystemGralConf().getServerInfo();
            MenuAcordeonUtil.setActionMenu(new MenuAcordeonObject(
                    (Button) this.findViewById(R.id.about_server_info_title),
                    (View) this.findViewById(R.id.about_server_info_content))
            );

            String appServer = SingletonServer.getInstance().getAppServer();
            String wsServer = SingletonServer.getInstance().getWsServer();
            String helpServer = SingletonServer.getInstance().getHelpServer();
            boolean developerMode = SingletonServer.getInstance().isDeveloper();

            TextView tv = (TextView) findViewById(R.id.about_server_info_text);
            tv.setKeyListener(null);
            String text =
                    "Nombre: " + conf.getName() + "\n" +
                            "Version: " + conf.getVersion() + "\n" +
                            "Api Rest: " + appServer + "\n" +
                            "WebSocket: " + wsServer + "\n" +
                            "HelpServer: " + helpServer + "\n" +
                            "developerMode: " + developerMode + "\n";
            tv.setText(text);
        }

        {
            SGCAsymEncrypt conf = SingletonServerConfiguration.getInstance().getSystemGralConf().getAsymEncrypt();
            MenuAcordeonUtil.setActionMenu(new MenuAcordeonObject(
                    (Button) this.findViewById(R.id.about_asym_encrypt_title),
                    (View) this.findViewById(R.id.about_asym_encrypt_content))
            );

            TextView tv = (TextView) findViewById(R.id.about_asym_encrypt_text);
            tv.setKeyListener(null);
            String text =
                    "Tipo: " + conf.getType() + "\n" +
                            "Bits: " + conf.getBits() + "\n";
            tv.setText(text);
        }

        aes(SingletonServerConfiguration.getInstance().getSystemGralConf().getPublicAES(), R.id.about_public_aes_title, R.id.about_public_aes_content, R.id.about_public_aes_text);
        aes(SingletonServerConfiguration.getInstance().getSystemGralConf().getSessionAES(), R.id.about_session_aes_title, R.id.about_session_aes_content, R.id.about_session_aes_text);
        aes(SingletonServerConfiguration.getInstance().getSystemGralConf().getMessagingAES(), R.id.about_messaging_aes_title, R.id.about_messaging_aes_content, R.id.about_messaging_aes_text);
        aes(SingletonServerConfiguration.getInstance().getSystemGralConf().getInvitationAES(), R.id.about_invitation_aes_title, R.id.about_invitation_aes_content, R.id.about_invitation_aes_text);
        {
            SGCAESDTO conf = SingletonServerConfiguration.getInstance().getSystemGralConf().getPrivacityIdAES();
            MenuAcordeonUtil.setActionMenu(new MenuAcordeonObject(
                    (Button) this.findViewById(R.id.about_privacity_id_aes_title),
                    (View) this.findViewById(R.id.about_privacity_id_aes_content))
            );

            TextView tv = (TextView) findViewById(R.id.about_privacity_id_aes_text);
            tv.setKeyListener(null);
            String text =
                    "Tipo: " + conf.getType() + "\n" +
                            "Bits: " + conf.getBits() + "\n" +
                            "\n"+
                            "Randon Type Generator: " + conf.getRandomGeneratorType().name() + "\n" +
                            "\n"+
                            "Min Lenght Key: " + conf.getKeyMinLenght() + "\n" +
                            "Max Lenght Key: " + conf.getKeyMaxLenght() + "\n" +
                            "\n"+
                            "Min Lenght Salt: " + conf.getSaltMinLenght() + "\n" +
                            "Max Lenght Salt: " + conf.getSaltMaxLenght() + "\n" +
                            "\n"+
                            "Min Iteration: " + conf.getIterationMinValue() + "\n" +
                            "Max Iteration: " + conf.getIterationMaxValue() + "\n" +
                            "\n"+
                            "Status: " + SingletonServerConfiguration.getInstance().getSystemGralConf().isPrivacityIdAESOn() + "\n" ;

            tv.setText(text);
        }

        simpleAes(SingletonServerConfiguration.getInstance().getSystemGralConf().getPersonalAES(), R.id.about_personal_aes_title, R.id.about_personal_aes_content, R.id.about_personal_aes_text);
        simpleAes(SingletonServerConfiguration.getInstance().getSystemGralConf().getExtraAES(), R.id.about_extra_aes_title, R.id.about_extra_aes_content, R.id.about_extra_aes_text);

        {
            SGCAuth conf = SingletonServerConfiguration.getInstance().getSystemGralConf().getAuth();
            MenuAcordeonUtil.setActionMenu(new MenuAcordeonObject(
                    (Button) this.findViewById(R.id.about_auth_title),
                    (View) this.findViewById(R.id.about_auth_content))
            );

            TextView tv = (TextView) findViewById(R.id.about_auth_text);
            tv.setKeyListener(null);
            String text =
                    "Token: " + conf.getTokenType() + "\n" +
                            "Length: " + conf.getTokenLenght() + "\n";


            tv.setText(text);
        }

        {

            MenuAcordeonUtil.setActionMenu(new MenuAcordeonObject(
                    (Button) this.findViewById(R.id.about_team_title),
                    (View) this.findViewById(R.id.about_team_content))
            );

            TextView tv = (TextView) findViewById(R.id.about_team_text);
            tv.setKeyListener(null);
            String text =
                    "Desarrollador: Jorge Kagiagian" + "\n" +
                            "Fecha de Inicio: 05/2021"+ "\n" +
                            "Todos los Derechos Reservados"+ "\n";


            tv.setText(text);
        }
    }

    private void simpleAes(SGCAESSimple extraAES, int p, int p2, int p3) {
        SGCAESSimple conf = extraAES;
        MenuAcordeonUtil.setActionMenu(new MenuAcordeonObject(
                (Button) this.findViewById(p),
                (View) this.findViewById(p2))
        );

        TextView tv = (TextView) findViewById(p3);
        tv.setKeyListener(null);
        String text =
                "Tipo: " + conf.getType() + "\n" +
                        "Bits: " + conf.getBits() + "\n" +
                        "\n" +
                        "Iterations: " + conf.getIteration() + "\n";


        tv.setText(text);
    }

    private void aes(SGCAESDTO publicAES, int p, int p2, int p3) {
        SGCAESDTO conf = publicAES;
        MenuAcordeonUtil.setActionMenu(new MenuAcordeonObject(
                (Button) this.findViewById(p),
                (View) this.findViewById(p2))
        );

        TextView tv = (TextView) findViewById(p3);
        tv.setKeyListener(null);
        String text =
                "Tipo: " + conf.getType() + "\n" +
                        "Bits: " + conf.getBits() + "\n" +
                        "\n" +
                        "Randon Type Generator: " + conf.getRandomGeneratorType().name() + "\n" +
                        "\n" +
                        "Min Lenght Key: " + conf.getKeyMinLenght() + "\n" +
                        "Max Lenght Key: " + conf.getKeyMaxLenght() + "\n" +
                        "\n" +
                        "Min Lenght Salt: " + conf.getSaltMinLenght() + "\n" +
                        "Max Lenght Salt: " + conf.getSaltMaxLenght() + "\n" +
                        "\n" +
                        "Min Iteration: " + conf.getIterationMinValue() + "\n" +
                        "Max Iteration: " + conf.getIterationMaxValue() + "\n";

        tv.setText(text);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}