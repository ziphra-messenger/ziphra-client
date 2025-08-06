package com.privacity.cliente.singleton.sharedpreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.neovisionaries.i18n.LanguageCode;
import com.privacity.cliente.R;
import com.privacity.cliente.common.enumerators.ButtonSetupEnum;
import com.privacity.cliente.singleton.localconfiguration.SingletonTextSizeMessage;

public class SharedPreferencesUtil {

    private static final String PREFERENCE_FILE_KEY = "com.privacity.cliente.preference_file_key";
    public static final String CONSTANT_PROTOCOL_SEPARATOR = "://";
    public static final String CONSTANT_PORT_SEPARATOR = ":";

    public static void saveReconnectState(Activity activity, String value){
        save(activity,SharedPreferencesEnum.RECONNECT_STATE,value);
    }
    public static void saveReconnectLogState(Activity activity, String value){
        save(activity,SharedPreferencesEnum.RECONNECT_LOG_STATE,value);
    }
    public static void saveTextSizeMessageSize(Activity activity, String value){
        save(activity,SharedPreferencesEnum.TEXT_SIZE_MESSAGE__SIZE,value);
    }
    public static void saveTextSizeMessageViewStatus(Activity activity, String value){
        save(activity,SharedPreferencesEnum.TEXT_SIZE_MESSAGE__VIEW_STATUS,value);
    }
    public static void saveDeveloperMode(Activity activity, boolean value){
        save(activity,SharedPreferencesEnum.DEVELOPER_MODE,value);
    }
    public static void saveTimeMessage(Activity activity, String value) {
        save(activity,SharedPreferencesEnum.TIME_MESSAGE,value);
    }
    public static void saveWsServerProtocol(Activity activity, String value){
        save(activity,SharedPreferencesEnum.WS_SERVER_PROTOCOL, value);
    }
    public static void saveWsServerUrl(Activity activity, String value){
        save(activity,SharedPreferencesEnum.WS_SERVER_URL, value);
    }
    public static void saveWsServerPort(Activity activity, String value){
        save(activity,SharedPreferencesEnum.WS_SERVER_PORT, value);
    }

    public static void saveAppServerProtocol(Activity activity, String value){
        save(activity,SharedPreferencesEnum.APP_SERVER_PROTOCOL, value);
    }
    public static void saveAppServerUrl(Activity activity, String value){
        save(activity,SharedPreferencesEnum.APP_SERVER_URL, value);
    }
    public static void saveAppServerPort(Activity activity, String value){
        save(activity,SharedPreferencesEnum.APP_SERVER_PORT, value);
    }

    public static void saveLanguage(Activity activity, String value){
        save(activity,SharedPreferencesEnum.LANGUAGE, value);
    }
    public static String getTextSizeMessageSize(Activity activity){
        return get(activity, SharedPreferencesEnum.TEXT_SIZE_MESSAGE__SIZE, SingletonTextSizeMessage.CONSTANT__DEFAULT_VALUE__SIZE+"");
      }
    public static String getReconnectState(Activity activity){
        return get(activity, SharedPreferencesEnum.RECONNECT_STATE, SingletonTextSizeMessage.CONSTANT__RECONNECT_STATUS+"");
    }
    public static String getReconnectLogState(Activity activity){
        return get(activity, SharedPreferencesEnum.RECONNECT_LOG_STATE, SingletonTextSizeMessage.CONSTANT__RECONNECT_LOG_STATUS+"");
    }
    public static String getTextSizeMessageViewStatus(Activity activity){
        return get(activity, SharedPreferencesEnum.TEXT_SIZE_MESSAGE__VIEW_STATUS, SingletonTextSizeMessage.CONSTANT__DEFAULT_VALUE__VIEW_STATUS+"");
    }

    public static String getLanguage(Activity activity){
        return get(activity, SharedPreferencesEnum.LANGUAGE, LanguageCode.es.toString());
    }

    public static String getTimeMessage(Activity activity) {
        return get(activity, SharedPreferencesEnum.TIME_MESSAGE, 60+"");
    }
    public static void saveButtonSetup(Activity activity, String value){
        save(activity,SharedPreferencesEnum.BUTTON_SETUP, value);
    }
    public static String getButtonSetup(Activity activity){
        return get(activity, SharedPreferencesEnum.BUTTON_SETUP, ButtonSetupEnum.SHOW_ALL.name());
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

    public static String getAppServerToUse(Activity activity){
        return getAppServerProtocol(activity) + CONSTANT_PROTOCOL_SEPARATOR + getAppServerUrl(activity) + CONSTANT_PORT_SEPARATOR + getAppServerPort(activity);
    }
    public static String getWsServerToUse(Activity activity){
        return getWsServerProtocol(activity) + CONSTANT_PROTOCOL_SEPARATOR+ getWsServerUrl(activity) + CONSTANT_PORT_SEPARATOR + getWsServerPort(activity);
    }
    public static String getWsServerProtocol(Activity activity){
//        String wsServer = activity.getResources().getStringArray(R.array.main_configuration_ws_servers)[0];
        return get(activity, SharedPreferencesEnum.WS_SERVER_PROTOCOL, activity.getString(R.string.configuration_server__default__ws_protocol));
    }
    public static String getWsServerUrl(Activity activity){
//        String wsServer = activity.getResources().getStringArray(R.array.main_configuration_ws_servers)[0];
        return get(activity, SharedPreferencesEnum.WS_SERVER_URL, activity.getString(R.string.configuration_server__default__ws_url));
    }
    public static String getWsServerPort(Activity activity){
//        String wsServer = activity.getResources().getStringArray(R.array.main_configuration_ws_servers)[0];
        return get(activity, SharedPreferencesEnum.WS_SERVER_PORT, activity.getString(R.string.configuration_server__default__ws_port));
    }
    public static String getAppServerProtocol(Activity activity){
//        String wsServer = activity.getResources().getStringArray(R.array.main_configuration_ws_servers)[0];
        return get(activity, SharedPreferencesEnum.APP_SERVER_PROTOCOL, activity.getString(R.string.configuration_server__default__app_protocol));
    }
    public static String getAppServerUrl(Activity activity){
//        String wsServer = activity.getResources().getStringArray(R.array.main_configuration_ws_servers)[0];
        return get(activity, SharedPreferencesEnum.APP_SERVER_URL, activity.getString(R.string.configuration_server__default__app_url));
    }
    public static String getAppServerPort(Activity activity){
//        String wsServer = activity.getResources().getStringArray(R.array.main_configuration_ws_servers)[0];
        return get(activity, SharedPreferencesEnum.APP_SERVER_PORT, activity.getString(R.string.configuration_server__default__app_port));
    }

/*    public static boolean getDeveloperMode(Activity activity){
        boolean defaultValue = true;
        if ( getAppServer(activity).equals("http://34.75.84.243:8080")){
            defaultValue = false;
        }
        return get(activity, SharedPreferencesEnum.DEVELOPER_MODE, defaultValue);
    }*/

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
