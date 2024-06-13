package com.privacity.cliente.singleton.sharedpreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.privacity.cliente.R;

public class SharedPreferencesUtil {

    private static final String PREFERENCE_FILE_KEY = "com.privacity.cliente.preference_file_key";

    public static void saveDeveloperMode(Activity activity, boolean value){
        save(activity,SharedPreferencesEnum.DEVELOPER_MODE,value);
    }

    public static void saveWsServer(Activity activity, String value){
        save(activity,SharedPreferencesEnum.WS_SERVER, value);
    }

    public static void saveAppServer(Activity activity, String value){
        save(activity,SharedPreferencesEnum.APP_SERVER, value);
    }

    private static void save(Activity activity, SharedPreferencesEnum pref, boolean value){
        SharedPreferences preferences = activity.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(pref.name(), value);
        editor.commit();
    }

    public static void save(Activity activity, SharedPreferencesEnum pref, String value){
        SharedPreferences preferences = activity.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(pref.name(), value);
        editor.commit();
    }

    public static String getWsServer(Activity activity){
        String wsServer = activity.getResources().getStringArray(R.array.main_configuration_ws_servers)[0];
        return get(activity, SharedPreferencesEnum.WS_SERVER, wsServer);
    }

    public static String getAppServer(Activity activity){
        String appServer = activity.getResources().getStringArray(R.array.main_configuration_app_servers)[0];
        return get(activity, SharedPreferencesEnum.APP_SERVER, appServer);
    }

    public static boolean getDeveloperMode(Activity activity){
        boolean defaultValue = true;
        if ( getAppServer(activity).equals("http://34.75.84.243:8080")){
            defaultValue = false;
        }
        return get(activity, SharedPreferencesEnum.DEVELOPER_MODE, defaultValue);
    }

    private static String get(Activity activity, SharedPreferencesEnum pref, String defaultValue){
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFERENCE_FILE_KEY,Context.MODE_PRIVATE);
        String r = sharedPref.getString(pref.name(), defaultValue);
        return r;
    }

    private static boolean get(Activity activity, SharedPreferencesEnum pref, boolean defaultValue){
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFERENCE_FILE_KEY,Context.MODE_PRIVATE);
        boolean r = sharedPref.getBoolean(pref.name(), defaultValue);
        return r;
    }

//    public static void save(Activity activity, SharedPreferencesEnum pref, String value){
//        SharedPreferences preferences = activity.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
//
//        //if (!preferences.contains(pref.name())){
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString(pref.name(), value);
//            editor.commit();
//
//        //}
//    }

    public static String getUserPass(Activity activity, SharedPreferencesEnum pref){
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFERENCE_FILE_KEY,Context.MODE_PRIVATE);
        String r = sharedPref.getString(pref.name(), null);
        return r;
    }

    public static void saveSharedPreferencesUserPass(Activity activity, String user, String pass){
        SharedPreferencesUtil.save(activity, SharedPreferencesEnum.USER, user);
        SharedPreferencesUtil.save(activity, SharedPreferencesEnum.PASSWORD, pass);
    }

    public static void deleteSharedPreferencesUserPass(Activity activity){
        SharedPreferencesUtil.delete(activity, SharedPreferencesEnum.USER);
        SharedPreferencesUtil.delete(activity, SharedPreferencesEnum.PASSWORD);
    }

    public static void delete(Activity activity, SharedPreferencesEnum pref){
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFERENCE_FILE_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(pref.name());
        editor.commit();
    }

}
