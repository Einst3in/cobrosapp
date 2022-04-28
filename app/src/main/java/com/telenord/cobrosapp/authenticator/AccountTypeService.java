package com.telenord.cobrosapp.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccountTypeService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder(); }

}
