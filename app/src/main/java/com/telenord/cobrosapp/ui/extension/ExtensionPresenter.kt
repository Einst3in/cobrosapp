package com.telenord.cobrosapp.ui.extension

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Horario
import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.Precio
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExtensionPresenter (private val mView: ExtensionContract.View, private val mCtx: Context): ExtensionContract.Listener{
    override fun getPrecio(tipo_OP: Pago.Tipo_OP, contrato: String) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getPrecioServicios(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato,tipo_OP).enqueue(object : Callback<Precio>{

            override fun onResponse(call: Call<Precio>, response: Response<Precio>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mView.showPrecio(response.body()!!)
                }
                else{
                    mView.showLoading(false)
                    val g = response.errorBody()!!.string()
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!

//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getPrecio(tipo_OP,contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {

                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getPrecio(tipo_OP,contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }

            override fun onFailure(call: Call<Precio>, t: Throwable) {
                mCtx.toast(t.message!!)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }


        })else mView.showLoading(false)
    }

    override fun getHorarios() {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getHorarios().enqueue(object : Callback<ArrayList<Horario>>{

            override fun onResponse(call: Call<ArrayList<Horario>>, response: Response<ArrayList<Horario>>) {
                if (response.isSuccessful) {
                    mView.showLoading(false)
                    mView.showHorarios(response!!.body()!!)
                } else {
                    mView.showLoading(false)

                    val g = response.errorBody()!!.string()
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           getHorarios()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))

                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getHorarios()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Horario>>, t: Throwable) {
               mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }


        }) else mView.showLoading(false)
    }
}