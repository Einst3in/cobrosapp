package com.telenord.cobrosapp.authenticator;

public class AuthConst {
    private static String CLIENT_ID="tecnicos_api";
    private static String CLIENT_SECRET="tecnicos_api";
    public static String AUTH_TYPE_KEY="auth_type";
    public static String NOMBRE="nombre";
    public static String BRIGADA="brigada";
    public static String FLOTA ="flota";
    public static String FICHA ="ficha";
    public static String KEY_USER="user";
    public static String KEY_PASSWORD="password";
    public static String KEY_GRANT_TYPE="grant_type";
    public static String ACCOUNT_TYPE="com.telenord.cobros";
    public static String AUTH_TOKEN_TYPE="Bearer";
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";



    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "Supervision";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE = "Bearer";
    public static final String AUTHTOKEN_TYPE_LABEL = "Acceso para tecnicos";


    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getClientSecret() {
        return CLIENT_SECRET;
    }


}
