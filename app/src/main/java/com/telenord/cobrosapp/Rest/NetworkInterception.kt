package com.telenord.cobrosapp.Rest

import android.content.Context
import android.net.ConnectivityManager
import com.telenord.cobrosapp.util.ConnectionCheck
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class NetworkInterception(private val context: Context): Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline()) {
            ConnectionCheck(context).isConnectingToInternet()
            throw NoConnectionException()
        }

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
