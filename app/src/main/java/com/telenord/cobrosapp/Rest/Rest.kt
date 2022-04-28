package com.telenord.cobrosapp.Rest

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.google.gson.GsonBuilder
import com.telenord.cobrosapp.util.ErrorUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Rest {
    //public static final String CENTRAL = "http://143.137.83.146:8090/";
    const val CENTRAL = "http://192.168.10.49:8090/"
//    const val CENTRAL = "https://central.telenordcloud.com/"
    var retrofitInstance: Retrofit? = null
        private set


    fun getPadronUrl(cedula: String):String = "${CENTRAL}clientes/${cedula}/foto"


    @JvmStatic
    fun getCentral(mCtx: Context?): Central? {
        val interception = NetworkInterception(mCtx!!)

        return if (interception.isOnline()) {
            val gson = GsonBuilder()
                    .setLenient()
                    .create()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okBuilder = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .connectionPool(ConnectionPool(100, 30, TimeUnit.SECONDS))
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(Interceptor { chain ->
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
                    val contentType:MediaType? = response.body()!!.contentType()
                    val body: ResponseBody? = ResponseBody.create(contentType, o.toString())
                    return@Interceptor response.newBuilder().body(body).build()
                }
                response
            })
                    .addInterceptor(Interceptor(mCtx))
                    .authenticator(Authenticator(mCtx))
            val client = okBuilder.build()
            if (retrofitInstance == null) {
                retrofitInstance = Retrofit.Builder()
                        .baseUrl(CENTRAL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                Log.d("getClient", retrofitInstance.toString())
            }
            retrofitInstance!!.create(Central::class.java)
        } else {
            Log.e("No tiene internet", "Rest")
            val errorUtils = ErrorUtils()
            errorUtils.showErrorApi(mCtx, "No hay conexion a Internet", 0, "Compruebe que este conectado a WI-FI o a una red movil", DialogInterface.OnClickListener { dialogInterface, i ->
                getCentral(mCtx)
                dialogInterface.dismiss()
            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            null
        }
    }
}