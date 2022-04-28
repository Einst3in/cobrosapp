package com.telenord.cobrosapp.ui.pagoMensualidad.detalle

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.DetalleFacturacion
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleMensPresenter(private val mView: DetalleMensContract.View,private val mCtx : Context): DetalleMensContract.Listener{

    override fun getDetallesFacturacion(contrato: String) {
        mView.showLoading(true)

        val rest = Rest.getCentral(mCtx)
                if (rest!=null)rest.getDetalleFacturacion(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato).enqueue(object : Callback<ArrayList<DetalleFacturacion>> {
            override fun onResponse(call: Call<ArrayList<DetalleFacturacion>>, response: Response<ArrayList<DetalleFacturacion>>) {

                if(response.isSuccessful){
                    mView.showDetalles(response.body()!!)
                    mView.showLoading(false)

                }else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getDetallesFacturacion(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getDetallesFacturacion(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }

                }
            }

            override fun onFailure(call: Call<ArrayList<DetalleFacturacion>>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }

        })else  mView.showLoading(false)

    }
}