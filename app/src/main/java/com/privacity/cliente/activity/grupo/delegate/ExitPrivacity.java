package com.privacity.cliente.activity.grupo.delegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.IntentCompat;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;

public class ExitPrivacity {
    public static void alertClose(GrupoActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton(activity.getString(R.string.grupo_activity__exit__cerrar_app), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SingletonSessionClosing.getInstance().setCloseApp(true);
                Singletons.resetAll();
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                /*
                SingletonSessionClosing.getInstance().setCloseApp(true);
                Singletons.resetAll();
                Singletons.resetAll();
                activity.finish();
*/


                //Runtime.getRuntime().exit(0);/*
                /*SingletonSessionClosing.getInstance().setCloseApp(true);
                Singletons.resetAll();
                IntentUtil.sendBroadcast(activity, BroadcastConstant.BROADCAST__FINISH_APPLICATION);*/
                /*
                IntentUtil.sendBroadcast(activity, BroadcastConstant.BROADCAST__FINISH_ACTIVITY_LOADING);

                activity.superOnBackPressed();
                Observers.message().dessuscribirse(activity);
                SingletonValues.getInstance().setLogout(false);
                IntentUtil.sendBroadcast(activity, BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES);*/
            }
        });

        builder.setNegativeButton(activity.getString(R.string.grupo_activity__exit__cerrar_session), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Singletons.resetAll();
                activity.finish();



                //restart(SingletonCurrentActivity.getInstance().getMainActivity());
/*                activity.superOnBackPressed();
                Observers.message().dessuscribirse(activity);
                SharedPreferencesUtil.deleteSharedPreferencesUserPass(activity);
                SingletonValues.getInstance().setLogout(true);
                {
                    Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES);
                    activity.sendBroadcast(intent);
                }
                {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                }*/

            }
        });


        builder.setNeutralButton(activity.getString(R.string.grupo_activity__exit__minimizar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.moveTaskToBack(true);
                //SharedPreferencesUtil.deleteSharedPreferencesUserPass(activity);
            }
        });
        //builder.setTitle("Si presiona OK cerrará la aplicacion");

        AlertDialog dialog = builder.create();

        dialog.show();

    }



    public static void restart(Context context){
        Intent mainIntent = IntentCompat.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(mainIntent);
        System.exit(0);
    }
}
