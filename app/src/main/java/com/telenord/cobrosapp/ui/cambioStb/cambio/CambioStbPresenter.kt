package com.telenord.cobrosapp.ui.cambioStb.cambio

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Horario
import com.telenord.cobrosapp.models.PrecioSTB
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CambioStbPresenter(val mCtx: Context, val mView:CambioStbContract.View): CambioStbContract.Listener{


    override  fun getPrecioStb(contrato:String) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getPreciosSTBCambio(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato).enqueue(object : Callback<ArrayList<PrecioSTB>> {
            override fun onResponse(call: Call<ArrayList<PrecioSTB>>, response: Response<ArrayList<PrecioSTB>>) {
                if (response.isSuccessful){
                    mView.showPrecioStb(response.body()!!)
                    mView.showLoading(false)
                }else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getPrecioStb(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getPrecioStb(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }

            }

            override fun onFailure(call: Call<ArrayList<PrecioSTB>>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })

            }
        })else mView.showLoading(false)
    }


}
