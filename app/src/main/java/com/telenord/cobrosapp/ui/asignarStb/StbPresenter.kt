package com.telenord.cobrosapp.ui.asignarStb

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StbPresenter(private val mView: StbContract.View,private val mCtx: Context): StbContract.Listener {


    override fun getTipos(contrato: String) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getTiposCajas(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato).enqueue(object : Callback<ArrayList<TipoEquipo>>{
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
//                    Crashlytics.logException(Throwable(errorResponse.mensaje))
                    FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                    ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                       getTipos(contrato)
                        dialogInterface.dismiss()
                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                } catch (e: Exception) {
                    e.printStackTrace()
//                    Crashlytics.logException(e)
                    FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                    ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                        getTipos(contrato)
                        dialogInterface.dismiss()
                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                }


            }
            }

            override fun onFailure(call: Call<ArrayList<TipoEquipo>>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }

        })else mView.showLoading(false)
    }

    override fun getCantidad(tipo: String) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getCantidadStb(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,tipo).enqueue(object : Callback<Cantidad>{
            override fun onResponse(call: Call<Cantidad>, response: Response<Cantidad>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mView.showCantidad(response.body()!!.cantidad!!,tipo)
                } else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getCantidad(tipo)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getCantidad(tipo)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }


                }
            }
            override fun onFailure(call: Call<Cantidad>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })else mView.showLoading(false)
    }

    override fun getPrecios(contrato: String, tipo: String, cant: Int) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getPreciosStb(contrato,tipo,cant).enqueue(object : Callback<Precio>{

            override fun onResponse(call: Call<Precio>, response: Response<Precio>) {
                if(response.isSuccessful){
                    mView.showLoading(false)
                    mView!!.showPrecios(response.body()!!)
                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {



                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getPrecios(contrato, tipo, cant)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getPrecios(contrato, tipo, cant)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }


                }
            }

            override fun onFailure(call: Call<Precio>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })else mView.showLoading(false)
    }

    override fun getPrecioExt(contrato: String) {
            mView.showLoading(true)
            val rest = Rest.getCentral(mCtx)
            if (rest!=null)rest.getPrecioServicios(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato,Pago.Tipo_OP.EX).enqueue(object : Callback<Precio>{

                override fun onResponse(call: Call<Precio>, response: Response<Precio>) {
                    if (response.isSuccessful){
                        mView.showLoading(false)
                        mView.showPrecioExt(response.body()!!)
                    }
                    else{
                        val g = response.errorBody()!!.string()
                        mView.showLoading(false)
                        try {


                            val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                            Crashlytics.logException(Throwable(errorResponse.mensaje))
                            FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                            ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                                getPrecioExt(contrato)
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        } catch (e: Exception) {
                            e.printStackTrace()
//                            Crashlytics.logException(e)
                            FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                            ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                                getPrecioExt(contrato)
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        }
                    }
                }

                override fun onFailure(call: Call<Precio>, t: Throwable) {
                    mCtx.toast(t.message!!)
//                    Crashlytics.logException(t)
                    FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                    ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                }


            })else mView.showLoading(false)

    }

    override fun getStbContrato(contrato: String) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if(rest!=null) rest.getSTBByContrato(contrato).enqueue(object : Callback<ArrayList<STB>>{

            override fun onResponse(call: Call<ArrayList<STB>>, response: Response<ArrayList<STB>>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mView.showStbContrato(response.body()!!)
                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getStbContrato(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getStbContrato(contrato)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<STB>>, t: Throwable) {
                mCtx.toast(t.message!!)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })else mView.showLoading(false)
    }
}