package com.telenord.cobrosapp.Rest

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestAuth {

//    const val BASE_URL = "http://192.168.10.49:8090/"
//    const val BASE_URL = "http://192.168.10.200:8090/"
    const val BASE_URL = "https://lab.telenordcloud.com/"

    @JvmStatic
    fun getRefresh(context: Context?): Central {
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val hBuilder = OkHttpClient.Builder()
        hBuilder.connectionPool(ConnectionPool(100, 30, TimeUnit.SECONDS))
        hBuilder.connectTimeout(5, TimeUnit.MINUTES)
        hBuilder.readTimeout(15, TimeUnit.MINUTES)
        hBuilder.writeTimeout(15, TimeUnit.MINUTES)
        hBuilder.addNetworkInterceptor(logging)
        hBuilder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            Log.e("RESPONSE G=>>", "ENTROOO $response")
            if (response.code() == 500) {
            }

            if (response.code() == 502) {
                Log.e("entro", "502")
                val o = JSONObject()
                try {
                    o.put("estado", "502")
                    o.put("error", "Bad Gateway")
                    o.put("mensaje", "SERVICIO NO DISPONIBLE")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val contentType: MediaType? = response.body()!!.contentType()
                val body: ResponseBody? = ResponseBody.create(contentType, o.toString())
                return@Interceptor response.newBuilder().body(body).build()
            }
            response
        })

        val cliente = hBuilder.build()
        var retrofit: Retrofit? = null
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            Log.d("getClient, Rest", retrofit.toString())
        }
        return retrofit!!.create(Central::class.java)
    }
}