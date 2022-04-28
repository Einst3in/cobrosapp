package com.telenord.cobrosapp.ui.pagoMensualidad.pago

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.Banco
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PagoMensPresenter(private val mView: PagoMensContract.View, private val mCtx: Context):PagoMensContract.Listener{

    override fun getBancos() {
        mView.showLoading(true)

        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getBancos(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!).enqueue(object : Callback<List<Banco>> {

            override fun onResponse(call: Call<List<Banco>>, response: Response<List<Banco>>) {
                if (response.isSuccessful){
                    mView.showBancos(response.body() as ArrayList<Banco>)
                    mView.showLoading(false)

                }else{
                    val g = response.errorBody()!!.string()

                    mView.showLoading(false)
                    try {

                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getBancos()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getBancos()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }
            override fun onFailure(call: Call<List<Banco>>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        }) else mView.showLoading(false)
    }

    override fun getTipoPago() {

        var ArrayTipoPago: ArrayList<String>? = ArrayList()
        ArrayTipoPago!!.add("EFECTIVO")
        ArrayTipoPago!!.add("TARJETA")
        ArrayTipoPago!!.add("CHEQUE")
        ArrayTipoPago!!.add("CARDNET")

        mView.showTipoPago(ArrayTipoPago)
    }

    override fun getDatos() {
        getBancos()
        getTipoPago()
    }

}