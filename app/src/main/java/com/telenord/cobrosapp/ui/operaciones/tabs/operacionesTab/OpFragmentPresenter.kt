package com.telenord.cobrosapp.ui.operaciones.tabs.operacionesTab

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.Precio
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpFragmentPresenter(private val mView: OpFragmentContract.View, private val mCtx : Context): OpFragmentContract.Listener {

    override fun getPrecio(tipo_OP: Pago.Tipo_OP,contrato: String)
    {

                mView.showLoading(true)
                val rest = Rest.getCentral(mCtx)
                if (rest!=null)rest.getPrecioServicios(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato,tipo_OP).enqueue(object : Callback<Precio>{

                    override fun onResponse(p0: Call<Precio>, p1: Response<Precio>) {
                        if (p1.isSuccessful){
                            mView.showLoading(false)
                            mView.showAlert(p1.body()!!)
                        }
                        else {
                            val g = p1.errorBody()!!.string()
                            mView.showLoading(false)
                            try {


                            val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                            Crashlytics.logException(Throwable(errorResponse.mensaje))
                                FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                            ErrorUtils().showErrorApi(mCtx, p1.message(),p1.code(), errorResponse.mensaje, DialogInterface.OnClickListener { dialogInterface, i ->
                                getPrecio(tipo_OP,contrato)
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        } catch (e: Throwable) {
                            e.printStackTrace()
//                            Crashlytics.logException(e)
                                FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                            ErrorUtils().showErrorApi(mCtx,p1.message(), p1.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                                getPrecio(tipo_OP,contrato)
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        }}
                    }
                    override fun onFailure(p0: Call<Precio>, p1: Throwable) {
                        mView.showLoading(false)
//                        Crashlytics.logException(p1)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(p1))
                        ErrorUtils().showErrorApi(mCtx, "Error", 0, p1.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }) else mView.showLoading(false)


    }

    override fun postM15(contrato: String) {

        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.postM15(contrato).enqueue(object : Callback<Void>{


            override fun onResponse(p0: Call<Void>, p1: Response<Void>)
            {
                if (p1.isSuccessful)
                {
                    mView.showLoading(false)
                    mCtx.toast("M15 enviado correctamente")

                }else
                {
                    val g = p1.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            postM15(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, p1.message(), p1.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            postM15(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }

            }
            override fun onFailure(p0: Call<Void>, p1: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(p1)
                FirebaseCrashlytics.getInstance().recordException(Throwable(p1))
                ErrorUtils().showErrorApi(mCtx, "Error", 0, p1.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        }) else mView.showLoading(false)
    }


}