package com.telenord.cobrosapp.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.telenord.cobrosapp.authenticator.AuthConst;
import com.telenord.cobrosapp.authenticator.AuthenticatorActivity;
import com.telenord.cobrosapp.Rest.SharedPreferenceManager;



public class BaseActivity extends AppCompatActivity implements OnAccountsUpdateListener {

    private AccountManager mAccountManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountManager = AccountManager.get(this);
        mAccountManager.addOnAccountsUpdatedListener(this,null,true);


    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {
        for(Account account:accounts){
            if(account.type.equalsIgnoreCase(AuthConst.ACCOUNT_TYPE)){
                Log.e("tokennnn", SharedPreferenceManager.getInstance(getApplicationContext()).getToken());
                return;
            }
        }
        goLogin();
    }

    public void goLogin(){
        Account[] accounts = mAccountManager.getAccountsByType(AuthConst.ACCOUNT_TYPE);
        Intent intent = new Intent(this, AuthenticatorActivity.class);
        if(accounts.length>0){
            intent.putExtra(AuthConst.KEY_USER,accounts[0].name);
        }

        intent.putExtra(AuthConst.AUTH_TOKEN_TYPE,AuthConst.AUTH_TOKEN_TYPE);
        intent.putExtra(AuthConst.ARG_IS_ADDING_NEW_ACCOUNT,true);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAccountManager.removeOnAccountsUpdatedListener(this);
    }
}
