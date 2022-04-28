package com.telenord.cobrosapp.authenticator;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.telenord.cobrosapp.models.ErrorResponse;
import com.telenord.cobrosapp.models.InfoResponse;
import com.telenord.cobrosapp.models.Token;
import com.telenord.cobrosapp.R;
import com.telenord.cobrosapp.Rest.Rest;
import com.telenord.cobrosapp.Rest.RestAuth;
import com.telenord.cobrosapp.Rest.SharedPreferenceManager;
import com.telenord.cobrosapp.ui.main.MainActivity;
import com.telenord.cobrosapp.util.CryptUtils;
import com.telenord.cobrosapp.util.ErrorUtils;
import com.telenord.cobrosapp.util.Resolve;
import com.telenord.cobrosapp.util.Utils;

import org.json.JSONObject;

import java.text.Normalizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    private static final String TAG = AuthenticatorActivity.class.getSimpleName();

    public final static String ARG_ACCOUNT_TYPE = "com.telenord.supervisor";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    private Button btnLogin;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private Spinner inputDB;
 /*   private SessionManager session;
    private HelperTareas db;*/
    private Bundle bundle;
    private Account account;
    private Boolean mNewAccount=false;
    public static final String EXTRA_RESULTADO = "extra.resultado";
    private static final String EXTRA_MENSAJE = "extra.mensaje";
    private static final int ESTADO_PETICION_FALLIDA = 107;
    private static final int ESTADO_TIEMPO_ESPERA = 108;
    private static final int ESTADO_ERROR_PARSING = 109;
    private static final int ESTADO_ERROR_SERVIDOR = 110;
    private Gson gson = new Gson();
    private String mAuthTokenType;
    private AccountManager mAccountManager;
    private String mAuthtokenType;
    private String mUsername;
    private String mPassword;
    protected boolean mRequestNewAccount = false;
    private Boolean mConfirmCredentials = false;
    private TextView mMsj;
    private ProgressBar mProgress;
    private ConstraintLayout mLayout;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermission();
        prepareView();

        mAccountManager=AccountManager.get(getBaseContext());
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(AuthConst.KEY_USER);
        mAuthtokenType = intent.getStringExtra(AuthConst.AUTH_TOKEN_TYPE);

        mRequestNewAccount = mUsername == null;
        mNewAccount = intent.getBooleanExtra(AuthConst.ARG_IS_ADDING_NEW_ACCOUNT,false);
        mConfirmCredentials = intent.getBooleanExtra(AuthConst.PARAM_CONFIRMCREDENTIALS, false);

    }

    private void prepareView(){
        mLayout =(ConstraintLayout)findViewById(R.id.Layout);
        mProgress =(ProgressBar)findViewById(R.id.Progress);
        mProgress.setVisibility(View.GONE);

        inputEmail = (EditText) findViewById(R.id.etUsuario);
        inputPassword = (EditText) findViewById(R.id.etPassword);
        inputDB = (Spinner) findViewById(R.id.spinnerCompania);

        btnLogin = (Button) findViewById(R.id.botonEntrar);

        // Progress alertMateriale
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final String email = stripAccents(inputEmail.getText().toString().trim().toLowerCase());
                final String password = inputPassword.getText().toString().trim();
                final Integer db = inputDB.getSelectedItemPosition();

                if(Resolve.hayConexion(AuthenticatorActivity.this)){


                    // Check for empty data in the form
                    if (!email.isEmpty() && !password.isEmpty()) {
                        // login user

                        checkLogin(email, password,db);
                    } else {
                        if (email.isEmpty()){
                            inputEmail.setError("Debe Introducir Usuario");
                            inputEmail.requestFocus();
                        }
                        if (password.isEmpty()){
                            inputPassword.setError("Debe Introducir Contraseña");
                            inputPassword.requestFocus();
                        }
                        String mensaje = "FAVOR INTRODUCIR CREDENCIALES!";
                        // Prompt user to enter credentials
                        Toast.makeText(AuthenticatorActivity.this,mensaje, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Snackbar snackbar = Snackbar.make(mLayout,"NO HAY CONEXION A INTERNET",Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkLogin(email, password,db);
                        }
                    });
                    snackbar.show();
                }
            }

        });
    }

    public static String stripAccents(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private void showProgress(boolean t){
        if(mProgress!=null){
            if(t){
                mProgress.setVisibility(View.VISIBLE);
            }else {
                mProgress.setVisibility(View.GONE);
            }
        }
    }


    private void checkLogin(final String email, final String password,final Integer db) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        final Bundle data = new Bundle();

//        pDialog.setMessage("Entrando ...");
//        pDialog.show();
        /*showDialog();*/

        showProgress(true);

        RestAuth.getRefresh(this).Login(AuthConst.getClientId(),AuthConst.getClientSecret(),email, CryptUtils.convertPassMd5(password),"password",db.toString())
                .enqueue(new Callback<Token>() {

                    @Override
                    public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                        assert response.body() != null;

                        if(response.isSuccessful()){

                            final Token mToken = (Token) response.body();
                            assert mToken != null;
                            Account mAccount = addOrFindAccount(inputEmail.getText().toString(),mToken.getRefreshToken(),bundle);
                            mAccountManager.setAuthToken(mAccount,AuthConst.AUTH_TOKEN_TYPE,mToken.getAccessToken());
                            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
                            sharedPreferenceManager.userLogin(mAccountManager.peekAuthToken(mAccount,AuthConst.AUTH_TOKEN_TYPE));
                           // Log.e(TAG+" token addded",mAccountManager.peekAuthToken(mAccount,AuthConst.AUTH_TOKEN_TYPE));
                            Log.e(TAG,sharedPreferenceManager.getToken());

                            if (checkPermission()){
                                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                                assert telephonyManager != null;
                                final String imei = new Utils().getDeviceId(AuthenticatorActivity.this);
                                Log.e("Desde Authenticator",imei);
                                Rest.getCentral(getBaseContext()).getInfo(imei).enqueue(new Callback<InfoResponse>() {
                                    @Override
                                    public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                                        assert response.body() != null;
                                        if (response.isSuccessful()){
                                            Log.e("Desde Authenticator","ejecuto la vaina");
                                            final InfoResponse resp = (InfoResponse) response.body();
                                            if (resp.getEstado() == 1){
                                                SharedPreferenceManager sp = new SharedPreferenceManager(AuthenticatorActivity.this);
                                                sp.info(resp.getInfo());
                                                if (resp.getInfo().getImpresora() != null){
                                                    sp.printer(resp.getInfo().getImpresora());
                                                }
                                                finishAccountAdd(inputEmail.getText().toString(),mToken.getAccessToken(),mToken.getRefreshToken());
                                            }else if (resp.getEstado() == 0){
                                                showProgress(false);
                                                 Intent intent = new Intent(AuthenticatorActivity.this,DeviceValidator.class);
                                                 intent.putExtra("imei",imei);
                                                 startActivity(intent);
                                            }
                                        }else {
                                            ErrorUtils errorUtils = new ErrorUtils();
                                            try {

                                                String g =response.errorBody().string();

                                                ErrorResponse errorResponse = errorUtils.parseError(g);
//                                                Crashlytics.logException(new Throwable(errorResponse.getMensaje()));
                                                FirebaseCrashlytics.getInstance().recordException(new Throwable(errorResponse.getMensaje()));
                                                errorUtils.showErrorApi(AuthenticatorActivity.this, errorResponse.getError(), errorResponse.getEstado(), errorResponse.getMensaje(), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
//                                                Crashlytics.logException(e);
                                                FirebaseCrashlytics.getInstance().recordException(new Throwable(e));
                                                errorUtils.showErrorApi(AuthenticatorActivity.this, "Error", 0, e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<InfoResponse> call, Throwable t) {
                                        ErrorUtils errorUtils = new ErrorUtils();
                                        errorUtils.showErrorApi(AuthenticatorActivity.this, "Error", 0, t.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                    }
                                });
                            }

                        }else{
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(AuthenticatorActivity.this, jObjError.getString("mensaje"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(AuthenticatorActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            showProgress(false);



                        }

                    }
                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        pDialog.dismiss();
                        Log.e(TAG+"error",t.getMessage());
                        /*FirebaseCrash.log("Error Server To Login");
                        FirebaseCrash.report(t);*/
                        showProgress(false);
                    }
                });
    }

    private Account addOrFindAccount(String email, String password,Bundle userData) {
        Account[] accounts = mAccountManager.getAccountsByType(AuthConst.ACCOUNT_TYPE);
        Account account = /*accounts.length != 0 ? accounts[0] : */ new Account(email, AuthConst.ACCOUNT_TYPE);
        Log.e(TAG+" datos de cuenta", account.name +" "+ " "+account.type +" "+ password);
        if (accounts.length == 0) {

            mAccountManager.addAccountExplicitly(account, password,null);
        } else {
            mAccountManager.setPassword(accounts[0], password);
        }
        return account;
    }

    private void finishAccountAdd(final String accountName, final String authToken, final String password) {
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AuthConst.ACCOUNT_TYPE);
        if (authToken != null)
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
        intent.putExtra(AccountManager.KEY_PASSWORD, password);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAccountAuthenticatorResult(intent.getExtras());
                setResult(RESULT_OK,intent);
                finish();
                showProgress(false);
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        },1000);
    }

    public Boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
    if (result == PackageManager.PERMISSION_GRANTED){
        return true;
    }
        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_PHONE_STATE},100);
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}