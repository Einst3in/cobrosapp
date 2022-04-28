package com.telenord.cobrosapp.ui.editarTelefono

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Telefonos
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarTelefonoPresenter(val  mCtx: Context,val mView : EditarTelefonoContract.View) : EditarTelefonoContract.Listener {

    override fun getTelefonos(contrato: String) {
        mView.showLoading(true)
       val rest = Rest.getCentral(mCtx)
        if(rest!= null) rest.getTelefonos(contrato).enqueue(object : Callback<Telefonos>{

            override fun onResponse(call: Call<Telefonos>, response: Response<Telefonos>) {

                if (response.isSuccessful){
                    mView.showTelefono(response.body())
                    mView.showLoading(false)
                }else{

                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {
                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getTelefonos(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })

                    }catch (e:Exception){
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getTelefonos(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }


            override fun onFailure(call: Call<Telefonos>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })
        else mView.showLoading(false)
    }

    override fun postTelefono(contrato: String, telefono: String, celular: String,otroTelefono: String,email: String) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if(rest!= null) rest.postTelefonos(contrato,telefono,celular,otroTelefono,email).enqueue(object : Callback<Void>{

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful){
                    mView.telefonoGuardado()
                    mView.showLoading(false)
                }else{

                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {
                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            postTelefono(contrato,telefono,celular,otroTelefono,email)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })

                    }catch (e:Exception){
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            postTelefono(contrato,telefono,celular,otroTelefono,email)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }


            override fun onFailure(call: Call<Void>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })
        else mView.showLoading(false)
    }
}