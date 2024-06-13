package com.privacity.cliente.activity.userhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UserHelperUtil {

    public static void open(Activity activity, String URL_DOC){
        Intent i = new Intent(activity, UserHelperActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(UserHelperPagesContant.URL_DOC, URL_DOC);
        i.putExtras(bundle);
        activity.startActivity(i);
    }
}
