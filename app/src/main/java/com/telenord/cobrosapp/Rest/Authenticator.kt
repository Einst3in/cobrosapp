package com.telenord.cobrosapp.Rest

import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import com.telenord.cobrosapp.authenticator.AuthConst
import com.telenord.cobrosapp.util.Resolve
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class Authenticator(private val mCtx: Context) : okhttp3.Authenticator {
    private val mAccountManager: AccountManager

    init {
        this.mAccountManager = AccountManager.get(mCtx)
    }


    @Throws(IOException::class)
    override fun authenticate(route: Route, response: Response): Request? {

        if (responseCount(response) >= 2) {
            return null
        }

        val accounts = mAccountManager.getAccountsByType(AuthConst.ACCOUNT_TYPE)
        val ac = accounts[0]
        val refresh = mAccountManager.getPassword(ac)


        val call = RestAuth.getRefresh(mCtx).getTokenByRefresh(AuthConst.getClientId(),
                AuthConst.getClientSecret(),refresh,
                "refresh_token",
                 "0")
        val res = call.execute()
        if (res.isSuccessful) {

            val newToken = res.body()!!

            val sharedPreference = SharedPreferenceManager(mCtx)
            Log.e("Authenticator.kt","${newToken.accessToken}")
            sharedPreference.userLogin(newToken.accessToken)
            mAccountManager.setAuthToken(ac, AuthConst.AUTH_TOKEN_TYPE, newToken.accessToken)
            mAccountManager.setPassword(ac, newToken.refreshToken)

            Log.e("Access Token ",newToken.accessToken)
            Log.e("newTOken fm Auth", newToken.refreshToken)
            Log.e("password de account",mAccountManager.getPassword(ac))

            return response.request().newBuilder()
                    .header("Authorization", newToken.accessToken!!)
                    .build()
        } else {
            Log.e("error Refreshing auth", res.errorBody()!!.string())
            Resolve.logoutUser(mCtx)
            return null
        }
    }


    private fun responseCount(response: Response?): Int {
        var response = response
        var result = 1
        while ((response == response!!.priorResponse()) && response != null) {
            result++
        }
        return result
    }

}
