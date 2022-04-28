package com.telenord.cobrosapp.ui.cambioStb.seleccion

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.STB
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeleccionStbPresenter(val mCtx: Context, val mView: SeleccionStbContract.View) : SeleccionStbContract.Listener {

   /* override fun getTiposStb() {
        mView.showLoading(true)

            val rest = Rest.getCentral(mCtx)
            if (rest!=null)rest.getTiposCajas().enqueue(object : Callback<ArrayList<TipoEquipo>>{
                override fun onResponse(call: Call<ArrayList<TipoEquipo>>, response: Response<ArrayList<TipoEquipo>>) {
                    if (response.isSuccessful){
                        mView.showLoading(false)
                        mView.showTipos(response.body()!!)
                    }
                    else{
                        val g = response.errorBody()!!.string()
                        mView.showLoading(false)
                        try {

                            val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
                            Crashlytics.logException(Throwable(errorResponse.mensaje))
                            ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                                getTiposStb()
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Crashlytics.logException(e)
                            ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                                getTiposStb()
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<TipoEquipo>>, t: Throwable) {
                    mView.showLoading(false)
                    Crashlytics.logException(t)
                    ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                }
            })

        else {mView.showLoading(false)}
    }*/

    override fun getSTB(contrato: String) {
        mView.showLoading(true)

        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getSTBByContrato(contrato).enqueue(object : Callback<ArrayList<STB>>{
            override fun onResponse(call: Call<ArrayList<STB>>, response: Response<ArrayList<STB>>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mView.showStb(response.body()!!)
                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {

                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getSTB(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getSTB(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<STB>>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })

        else {mView.showLoading(false)}
    }


 /*   */
}

