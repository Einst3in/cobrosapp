package com.telenord.cobrosapp.Rest

import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import com.telenord.cobrosapp.authenticator.AuthConst
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class Interceptor(private val mCtx: Context) : Interceptor {
    private val TAG = this.javaClass.name
    @Throws(IOException::class)

    override fun intercept(chain: Interceptor.Chain): Response? {


        val am = AccountManager.get(mCtx)
        val accounts = am.getAccountsByType(AuthConst.ACCOUNT_TYPE)
        var token : String? =null
        if (accounts.isNotEmpty()){
        token = am.peekAuthToken(accounts[0], AuthConst.AUTH_TOKEN_TYPE)
        if (token.isNullOrEmpty()){
            Log.e("token null",SharedPreferenceManager.getInstance(mCtx).token)
            token = SharedPreferenceManager.getInstance(mCtx).token

            Log.e(TAG + " token-------------->",token)
            Log.e(TAG + " Refresh account",am.getPassword(accounts[0]))

        }}
        Log.e("Token Access","$token")


        var request = chain.request()
        request = request.newBuilder()
                .addHeader("Authorization", token)
                .build()
        return chain.proceed(request)
    }
}
