package com.telenord.cobrosapp.ui.operaciones

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.models.Balance
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.PagoDetalle
import com.telenord.cobrosapp.models.Precio
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.ui.operaciones.OperacionesContract
import com.telenord.cobrosapp.util.ErrorUtils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OperactionesPresenter(internal var mCtx: Context, internal var mView: OperacionesContract.View) : OperacionesContract.Listener {

    override fun getBalances(contrato: String) {
        mView.showLoading(true)
        val rest =
        Rest.getCentral(mCtx)
                if (rest!=null)rest.getBalance(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato).enqueue(object : Callback<PagoDetalle> {
            override fun onResponse(call: Call<PagoDetalle>, response: Response<PagoDetalle>) {
                mView.showLoading(false)
                if (response.isSuccessful) {

                    val balance : Balance = Balance(response.body()!!.balance!!,response.body()!!.mensualidad!!)
                    Log.e("balance",balance.toString())
                    mView.showBalance(response.body())

                } else {
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getBalances(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getBalances(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }


                }

            }

            override fun onFailure(call: Call<PagoDetalle>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        }) else mView.showLoading(false)
    }

    override fun getDetalleMens(Contrato: String) {

        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null) rest.getMensualidad(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,Contrato).enqueue(object : Callback<List<Precio>>{

            override fun onResponse(call: Call<List<Precio>>, response: Response<List<Precio>>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mView.showMensualidad(response.body()!!)
                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)

                    try {

                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getDetalleMens(Contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getDetalleMens(Contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }

            }

            override fun onFailure(call: Call<List<Precio>>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }


        }) else mView.showLoading(false)

    }
}
