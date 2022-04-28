package com.telenord.cobrosapp.ui.main

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.Dispositivo
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Aneudy on 10/5/2017.
 */

class MainPresenter(private val mCtx: Context, private val mView: MainContract.View) : MainContract.Listener
{
    override fun sendDispositivo(dispositivo: Dispositivo) {
        val rest =
        Rest.getCentral(mCtx)
                if(rest!=null)rest.postDispositivo(dispositivo.imei!!,dispositivo.user!!).enqueue(object : Callback<Void>{
            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
               if(response.isSuccessful){
                   mView.sendDispositivo(dispositivo)
               }else{
                   val g = response.errorBody()!!.string()
                   try {


                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           sendDispositivo(dispositivo)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           sendDispositivo(dispositivo)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   }
               }
            }
        })
    }
}