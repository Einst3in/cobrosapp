package com.telenord.cobrosapp.authenticator;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.telenord.cobrosapp.Rest.RestAuth;
import com.telenord.cobrosapp.models.Token;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private String TAG = "Authenticator";
    private final Context mCtx;

    public AccountAuthenticator(Context context) {
        super(context);
        this.mCtx = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mCtx, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthConst.AUTH_TYPE_KEY,authTokenType);
        intent.putExtra(AuthConst.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.e("goo auth proc","|ok voyyyyyyyyyyyyyyyy");
        final Bundle bundle = new Bundle();

        if(!authTokenType.equals(AuthConst.AUTH_TOKEN_TYPE)){
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return bundle;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken
        final AccountManager accountManager = AccountManager.get(mCtx);
        // Password is storing the refresh token
        final String password = accountManager.getPassword(account);

        if (password != null) {

            try {
                Token accessToken = RestAuth.getRefresh(mCtx).getTokenByRefresh("refresh_token",AuthConst.getClientId(), AuthConst.getClientSecret(),password,"0").execute().body();
                if (accessToken!=null && !TextUtils.isEmpty(accessToken.getAccessToken())) {
                    bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, AuthConst.ACCOUNT_TYPE);
                    bundle.putString(AccountManager.KEY_AUTHTOKEN, accessToken.getAccessToken());
                    accountManager.setPassword(account, accessToken.getRefreshToken());
                    return bundle;
                }
            } catch (Exception e) {
                Log.e("error resfressh",e.getLocalizedMessage());
                //Timber.e(e, "Failed refreshing token.");
            }
        }

        // Otherwise... start the login intent
        //Timber.i("Starting login activity");
        final Intent intent = new Intent(mCtx, AuthenticatorActivity.class);
        intent.putExtra(AuthConst.KEY_USER, account.name);
        intent.putExtra(AuthConst.AUTH_TYPE_KEY, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;

    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return authTokenType.equals(AuthConst.AUTH_TOKEN_TYPE) ? authTokenType : null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mCtx,AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME,account.name);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN,
                authTokenType);
        intent.putExtra(AuthConst.PARAM_CONFIRMCREDENTIALS, false);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
   /* @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("Supervisor", TAG + "> addAccount");

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        Log.d("Supervisor", TAG + "> getAuthToken");

        //si se esta solicitando un token de un tipo no soportado entonces devuelve este error
        if (!authTokenType.equals(AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        //extraemos el usuario y la contrasena desde el account manager y preguntar al servidor por un token apropiado
        final AccountManager am = AccountManager.get(mContext);


        String authToken = am.peekAuthToken(account, authTokenType);

        Log.d("Supervisor", TAG + "> peekAuthToken returned - " + authToken);

        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    Log.d("Supervisor", TAG + "> re-authenticating with the existing password");
                    authToken = sServerAuthenticate.getTokenByRefresh(mContext, password);
                    Log.d("Nuevo Token",authToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }


    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE.equals(authTokenType))
            return AUTHTOKEN_TYPE_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }*/
//Finaliza uno
/*
    private String TAG = "Authenticator";
    private final Context mContext;

    public final static String PARAM_USER_PASS = "USER_PASS";
    public static final String AUTHTOKEN_TYPE = "tecnico";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public AccountAuthenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("Supervisor", TAG + "> addAccount");
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountGeneral.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountGeneral.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {


        //si se esta solicitando un token de un tipo no soportado entonces devuelve este error
        if (!authTokenType.equals(AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        //extraemos el usuario y la contrasena desde el account manager y preguntar al servidor por un token apropiado
        final AccountManager am = AccountManager.get(mContext);


        String authToken = am.peekAuthToken(account, authTokenType);

        Log.d("Supervisor", TAG + "> peekAuthToken returned - " + authToken);

        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    Log.d("Supervisor", TAG + "> re-authenticating with the existing password");
                    sServerAuthenticate.getTokenByRefresh(mContext,constants.getRefreshToken());
                    authToken = AccountGeneral.constants.getToken();
                    Log.d("Nuevo Token",authToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountGeneral.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AccountGeneral.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountGeneral.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }


    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE.equals(authTokenType))
            return AUTHTOKEN_TYPE_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
*/
}
