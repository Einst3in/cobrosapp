package com.telenord.cobrosapp.Rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.telenord.cobrosapp.models.Dispositivo;
import com.telenord.cobrosapp.models.Impresora;
import com.telenord.cobrosapp.models.Info;


public class SharedPreferenceManager {

    private static SharedPreferenceManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "cobros";


    private static final String KEY_USER_TOKEN_ACCESS = "keyusertokenaccess";
    private static final String KEY_USER_HASH = null;
    private static final String KEY_DEVICE_IMEI ="keydeviceimei";
    private static final String KEY_DEVICE_USER ="keydeviceuser";
    private static final String KEY_PRINTER_DEFAULT = "keyprinterdefault";
    public static final String KEY_USER_INFO = "keyuserinfo";



    public SharedPreferenceManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferenceManager(context);
        }
        return mInstance;
    }

    public boolean userLogin(String user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_TOKEN_ACCESS, user);
        editor.apply();
        return true;
    }
    public boolean info(Info info){
        SharedPreferences sharedPreferences =mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson =new Gson();
        String json = gson.toJson(info);
        editor.putString(KEY_USER_INFO,json);
        editor.apply();
        return true;
    }
    public boolean printer(Impresora impresora){
        SharedPreferences sharedPreferences =mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson =new Gson();
        String json = gson.toJson(impresora);
        Log.e("Printer SharedPreferen",json);
        editor.putString(KEY_PRINTER_DEFAULT,json);
        editor.apply();


        return true;
    }

    public boolean Device(String imei, String user){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DEVICE_IMEI,imei);
        editor.putString(KEY_DEVICE_USER,user);

        editor.apply();
        return true;
    }

    public boolean userHash(String hash){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_HASH ,hash);
        editor.apply();
        return true;
    }



    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USER_TOKEN_ACCESS, null) != null)
            return true;
        return false;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return (sharedPreferences.getString(KEY_USER_TOKEN_ACCESS, "0"));}

        public String getHash(){
        SharedPreferences sharedPreferences= mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return (sharedPreferences.getString(KEY_USER_HASH,"0"));}

        public Dispositivo getDispositivo(){
            SharedPreferences sharedPreferences= mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

            return new Dispositivo(sharedPreferences.getString(KEY_DEVICE_IMEI,"0"),sharedPreferences.getString(KEY_DEVICE_USER,"0"));
        }

        public Impresora getImpresora(){
            SharedPreferences sharedPreferences= mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            Gson gson =new Gson();
            String json = sharedPreferences.getString(KEY_PRINTER_DEFAULT,null);
            Impresora impresora = gson.fromJson(json,Impresora.class);
            return impresora;
        }
        public Info getInfo(){
            SharedPreferences sharedPreferences= mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            Gson gson =new Gson();
            String json = sharedPreferences.getString(KEY_USER_INFO,null);
            Info info = gson.fromJson(json,Info.class);
            return info;
        }

        public boolean borrarImpresora(){
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(KEY_PRINTER_DEFAULT);
            editor.apply();
        return true;
        }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }


}
