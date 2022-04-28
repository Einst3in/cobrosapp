package com.telenord.cobrosapp.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.telenord.cobrosapp.authenticator.AuthConst;
import com.telenord.cobrosapp.authenticator.AuthenticatorActivity;
import com.telenord.cobrosapp.print.PRTAndroidPrint;
import com.telenord.cobrosapp.Rest.SharedPreferenceManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Resolve {


    static ProgressDialog pDialog;

    @SuppressWarnings("finally")

    public static AccountManager mAccountManager  = null;
    public static String mToken = null;

    public static void logoutUser(Context context){


        if (mAccountManager == null){
            mAccountManager = AccountManager.get(context);}

        Account[] accounts = mAccountManager.getAccountsByType(AuthConst.ACCOUNT_TYPE);
        String token = mAccountManager.peekAuthToken(accounts[0],AuthConst.AUTH_TOKEN_TYPE);

        mAccountManager.invalidateAuthToken(AuthConst.ACCOUNT_TYPE,
                token);
        SharedPreferenceManager.getInstance(context).logout();
        for ( int i = 0;  i < accounts.length; i++ ){
            mAccountManager.removeAccount(accounts[i],null,null);
        }

        Intent activity = new Intent(context, AuthenticatorActivity.class);
        context.startActivity(activity);
    }


    public static Boolean hayConexion(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static int tryParseInt(String valor)
    {
        int respuesta = 0;
        try { respuesta = Integer.parseInt(valor); }
        catch(NumberFormatException ex){ respuesta = 0; }
        finally{return respuesta;}
    }

    /**
     * Convierte un valor de String a Doble
     * @param Valor
     * @return Double
     */
    @SuppressWarnings("finally")
    public static Double DoubleTryParse(String Valor){
        Double respuesta = 0.00;
        try { respuesta = Double.parseDouble(Valor); }
        catch (NumberFormatException ex) { respuesta = 0.00;}
        finally {return respuesta;}
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNullOrWhitespace(String s) {
        return s == null || isWhitespace(s);

    }

    public static boolean isWhitespace(String s) {
        int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static String[] DevuelveLineaImpresion(String Texto, int Maximo, String Delimitador){
        String[] Resultado = SeparaLineas(AsignaDivisiones(Texto,Maximo, Delimitador), Delimitador);
        Resultado[Resultado.length -1] = CompletaPool(Resultado[Resultado.length-1], Maximo);
        return Resultado;
    }

    private static String AsignaDivisiones(String Texto, int cantidadDivisor, String Delimitador){
        int idx = cantidadDivisor;
        StringBuilder text = new StringBuilder(Texto);

        while (idx < Texto.length())
        {
            text.insert(idx, Delimitador);
            idx += cantidadDivisor;
        }

        return text.toString();
    }

    public static String[] SeparaLineas(String Texto, String Delimitador){
        return Texto.split(Delimitador);
    }

    public static String CompletaPool(String Texto, int Maximo){
        StringBuilder SB = new StringBuilder();
        for (int intActual = Texto.length(); intActual < Maximo; intActual++) {
            SB.append(" ");
        }
        return Texto + SB;
    }

    public static String CompletaAmbosLadosPool(String Texto, int Maximo){
        StringBuilder SB = new StringBuilder(Texto);
        Boolean boolean_posicion = false;

        for (int intActual = Texto.length(); intActual < Maximo; intActual++) {
            if (false == boolean_posicion){
                SB.append(" ");
                boolean_posicion = true;
            }
            else {
                SB.insert(0, " ");
                boolean_posicion = true;
            }
        }

        return SB.toString();
    }

    public static void presentaPDialog (Context contexto, String Texto , boolean boolCancelable){
        pDialog = new ProgressDialog(contexto);
        pDialog.setMessage(Texto);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(boolCancelable);
        pDialog.show();
    }

    public static void escondePDialog()
    {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

 /*   public static String Imei(Context Contexto){
        TelephonyManager telephonyManager;
        telephonyManager = ( TelephonyManager )Contexto.getSystemService( Context.TELEPHONY_SERVICE );
        return telephonyManager.getDeviceId();

    }*/

    public static boolean Connect(String deviceName,PRTAndroidPrint mobileprint) {
        ArrayList<String> strList = mobileprint.PRTGetBondedDevices();
        for (int i = 0; i < strList.size(); i++) {
            if (strList.get(i).equals(deviceName)) {
                if (mobileprint.PRTConnectDevices(strList, i+1)) {
                    // connect succeed
                    return true;
                }
            }
        }
        return false;
    }



    public static String alinea_centro(String Texto, int Maximo){

        StringBuilder SB = new StringBuilder(Texto);
        Maximo = Math.round((Maximo - Texto.length()) / 2);

        for (Integer x = 0 ; x < Maximo ; x++ ) {
            SB.insert(0, " ");
        }

        return SB.toString();
    }

    public static String alinea_derecha(String Texto, Integer Maximo){

        StringBuilder SB = new StringBuilder(Texto);
        Maximo = Maximo - Texto.length();

        for (Integer x = 0 ; x < Maximo ; x++ ) {
            SB.insert(0, " ");
        }

        return SB.toString();
    }

    public static String dos_columna(String Texto, Integer Maximo, String Texto_dos){

        StringBuilder SB = new StringBuilder(Texto);
        Integer cantidad = Maximo - Texto.length() - Texto_dos.length();

        if (cantidad > 0) for (Integer x = 0; x < cantidad; x++) SB.append(" ");

        SB.append(Texto_dos);

        return SB.toString();

    }


    public static String md5_2(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
            static public void main(String[] args) {

              customFormat("###,###.###", 123456.789);
              customFormat("###.##", 123456.789);
              customFormat("000000.000", 123.78);
              customFormat("$###,###.###", 12345.67);
           }
        }

        The output is:

        123456.789  ###,###.###  123,456.789
        123456.789  ###.##  123456.79
        123.78  000000.000  000123.780
        12345.67  $###,###.###  $12,345.67

     */

    static public String customFormat(String pattern, double value ) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
       return output;
    }

    static public String getSystemDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(c.getTime());
    }



}


