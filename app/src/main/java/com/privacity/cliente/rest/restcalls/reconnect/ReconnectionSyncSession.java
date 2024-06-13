package com.privacity.cliente.rest.restcalls.reconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.privacity.cliente.activity.loading.LoadingActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.ServerConfRest;
import com.privacity.cliente.singleton.SingletonLoginValues;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.request.LoginRequestDTO;

import org.springframework.http.ResponseEntity;

public class ReconnectionSyncSession {

//    private ProgressBar progressBar=null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reconnection);
//        progressBar = (ProgressBar) findViewById(R.id.gral_progress_bar);
//        ProgressBarUtil.hide(ReconnectionActivity.this, progressBar);
//        try {
//            //callLoginRest();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        ((Button)findViewById(R.id.reintentar_login)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    callLoginRest(ReconnectionActivity.this, progressBar);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    SimpleErrorDialog.errorDialog(ReconnectionActivity.this,"reintentando 1", e.getMessage());
//
//                }
//            }
//        });
//    }

    public static void sync(Activity activity, ProgressBar progressBar) throws Exception {
        ProgressBarUtil.show(activity, progressBar);
        ServerConfRest.getTime(activity, loginCallbackRest(activity), new InnerCallbackRest() {
            @Override
            public void action(Context context) {
                try {
                    callLoginRestInnerCallBack(activity);
                } catch (Exception e) {
                    ProgressBarUtil.hide(activity, progressBar);
                    e.printStackTrace();
                }
            }
        });
    }

    private static void callLoginRestInnerCallBack(Activity activity) throws Exception {


        Gson gson = GsonFormated.get();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_AUTH);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_AUTH_LOGIN);

        LoginRequestDTO t = new LoginRequestDTO();
        t.setUsername(SingletonValues.getInstance().getUsernameHash());
        t.setPassword(SingletonValues.getInstance().getPasswordHash());


        p.setObjectDTO(gson.toJson(t));


        RestExecute.doitPublic(activity, p, loginCallbackRest(activity)
                , SingletonLoginValues.getInstance().getAEStoUse(),
                SingletonLoginValues.getInstance().getAEStoSend()
        );
    }

    private static CallbackRest loginCallbackRest(Activity activity) {
        CallbackRest r = new CallbackRest() {

            @Override
            public void response(ResponseEntity<ProtocoloDTO> response) {






                Intent i = new Intent(activity, LoadingActivity.class);

                i.putExtra("protocoloDTO", response.getBody().getObjectDTO());
                i.putExtra("username", SingletonValues.getInstance().getUsernameNoHash());
                i.putExtra("password", SingletonValues.getInstance().getPasswordNoHash());

                activity.startActivity(i);



            }

            @Override
            public void onError(ResponseEntity<ProtocoloDTO> response) {

                //ProgressBarUtil.hide(activity, progressBar);
            }

            @Override
            public void beforeShowErrorMessage(String msg) {
                SimpleErrorDialog.errorDialog(activity,"error reconect", msg);
                //ProgressBarUtil.hide(ReconnectionActivity.this, progressBar);
                SimpleErrorDialog.errorDialog(activity,"reintentando", msg);
                //LineStatus.statusOffLine(activity);

            }
        };
        return r;
    }
}