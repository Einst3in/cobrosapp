package com.telenord.cobrosapp.ui.impresora.configPrinter

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.ImpresoraModelo
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConfPrinterPresenter(internal val mCtx: Context, internal val mView: ConfPrinterContract.View) : ConfPrinterContract.Listener {

val imei = SharedPreferenceManager.getInstance(mCtx).dispositivo.imei

    override fun getModels() {
        mView.showLoading(true)
       val rest = Rest.getCentral(mCtx)
               if (rest!= null) rest.getModelos().enqueue(object : Callback<ArrayList<ImpresoraModelo>>{

           override fun onResponse(call: Call<ArrayList<ImpresoraModelo>>, response: Response<ArrayList<ImpresoraModelo>>) {
               if (response.isSuccessful){
                   mView.showLoading(false)
                   mView.showModels(response.body()!!)
               }
               else{
                   val g = response.errorBody()!!.string()
                   mView.showLoading(false)
                   try {


                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           getModels()
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           getModels()
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   }
               }
           }

           override fun onFailure(call: Call<ArrayList<ImpresoraModelo>>, t: Throwable) {
               mView.showLoading(false)
//               Crashlytics.logException(t)
               FirebaseCrashlytics.getInstance().recordException(Throwable(t))
               ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                   dialogInterface.dismiss()
               }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
           }
       })else mView.showLoading(true)

    }

    override fun addPrinter(mac: String?, modelo: Int?) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
                if (rest!=null)rest.postImpresora(imei!!,mac!!,modelo!!).enqueue(object : Callback<Void>{

           override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mCtx.toast("Guardado Correctamente")
                    mView.showAlert()
                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            addPrinter(mac, modelo)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            addPrinter(mac, modelo)
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


        }) else mView.showLoading(false)
    }

    override fun setDefault(mac: String?) {
        val rest = Rest.getCentral(mCtx)
                if (rest!= null) rest.updateDefault(imei!!,mac!!).enqueue(object : Callback<Void>{

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mCtx.toast("Actualizado Correctamente")
                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            setDefault(mac)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            setDefault(mac)
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

        }) else mView.showLoading(false)
    }

}