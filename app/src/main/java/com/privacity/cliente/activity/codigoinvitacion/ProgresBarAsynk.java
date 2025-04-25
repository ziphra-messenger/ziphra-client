package com.privacity.cliente.activity.codigoinvitacion;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;

public class ProgresBarAsynk extends AsyncTask<Void, Void, Void> {

    private final ProgressBar progressBar;
    private CodigoInvitacionActivity context;

    public ProgresBarAsynk(CodigoInvitacionActivity context, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.context = context;
    }
    
    @Override
    protected Void doInBackground(Void... voids) {
        //ProgressBarUtil.show(context, progressBar);

        new Thread() {
            public void run() {

                    try {
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                               // EncryptKeysDTO ek = null;
                                try {
                                    while (context.getEk() == null || context.isErrorGenerandoCodigoInvitacion()){}

                                    //ek = EncryptUtil.invitationCodeEncryptKeysGenerator(SingletonValues.getInstance().getPersonalAEStoUse());
                                 //   context.setEk(ek);
                                    context.callGenerator();
                                    ProgressBarUtil.hide(context, progressBar);
                                }catch( Exception e){
                                    e.printStackTrace();
                                    context.setErrorGenerandoCodigoInvitacion(true);
                                    ProgressBarUtil.hide(context, progressBar);
                                    //ProgressBarUtil.hide(context, progressBar);
                                    SimpleErrorDialog.errorDialog(context, context.getString(R.string.acodigoinvitacion_activity__alert__error_generated_title), e.getMessage());

                                }

                            }
                        });

                    }catch( Exception e){
                        e.printStackTrace();
                        context.setErrorGenerandoCodigoInvitacion(true);
                        ProgressBarUtil.hide(context, progressBar);
                        //ProgressBarUtil.hide(context, progressBar);
                        SimpleErrorDialog.errorDialog(context, context.getString(R.string.acodigoinvitacion_activity__alert__error_generated_title), e.getMessage());

                    }
                }

        }.start();


//            new Thread(()->{
////    Do all your long process here
//                try {
//                    EncryptKeysDTO ek = EncryptUtil.invitationCodeEncryptKeysGenerator(SingletonValues.getInstance().getPersonalAEStoUse());
//                    context.setEk(ek);
//                    context.callGenerator();
//                    ProgressBarUtil.hide(context, progressBar);
//                }catch( Exception e){
//                    e.printStackTrace();
//                    context.setErrorGenerandoCodigoInvitacion(true);
//                    ProgressBarUtil.hide(context, progressBar);
//                    //ProgressBarUtil.hide(context, progressBar);
//                    SimpleErrorDialog.errorDialog(context, context.getString(R.string.acodigoinvitacion_activity__alert__error_generated_title), e.getMessage());
//
//                }
//
//
//            }).start();

            //context.setEk(ek);
            //context.callGenerator();
           // ProgressBarUtil.hide(context, progressBar);

        return null;
    }
}
