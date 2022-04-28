package com.telenord.cobrosapp.ui.pagoAdelantado

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.PagoAdelantado
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PagoAdelantadoPresenter(private val mView: PagoAdelantadoContract.View, private val mCtx: Context): PagoAdelantadoContract.Listener  {



    override fun getValor(contrato: String, meses: Int) {

        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
                if (rest !=null) rest.getValorDescuento(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato,meses).enqueue(object : Callback<PagoAdelantado>{

            override fun onResponse(call: Call<PagoAdelantado>, response: Response<PagoAdelantado>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mView.showValor(response.body()!!)
                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getValor(contrato,meses)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getValor(contrato,meses)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }
            override fun onFailure(call: Call<PagoAdelantado>, t: Throwable) {

                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })else mView.showLoading(false)
    }
}