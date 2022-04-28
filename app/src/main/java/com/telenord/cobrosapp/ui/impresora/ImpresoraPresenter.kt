package com.telenord.cobrosapp.ui.impresora

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.impresora.ImpresoraContract
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImpresoraPresenter(private val mCtx:Context,private val mView: ImpresoraContract.View):ImpresoraContract.Listener{


    val imei  = SharedPreferenceManager.getInstance(mCtx).dispositivo.imei

    override fun getList() {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getImpresoras(imei!!,0).enqueue(object :Callback<ArrayList<Impresora>>{

            override fun onResponse(call: Call<ArrayList<Impresora>>, response: Response<ArrayList<Impresora>>) {
                if (response.isSuccessful){
                    mView.showLoading(false)
                    mView.showList(response!!.body()!!)}
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getList()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getList()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Impresora>>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }


        }) else mView.showLoading(false)
    }

    override fun getDefaultPrinter() {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getImpresoras(imei!!,1).enqueue(object :Callback<ArrayList<Impresora>>{

           override fun onResponse(call: Call<ArrayList<Impresora>>, response: Response<ArrayList<Impresora>>) {

               if (response.isSuccessful){

                   if (!response.body().isNullOrEmpty()){
                       SharedPreferenceManager.getInstance(mCtx).printer(response.body()!![0])
                       mView.showDefaultPrinter(response!!.body()!![0]) }
                   else{
                       SharedPreferenceManager.getInstance(mCtx).borrarImpresora()
                       mCtx.toast("no hay impresora")
                       mView.showLoading(false)
                   }
               }
               else{
                   val g = response.errorBody()!!.string()
                   try {

                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           getDefaultPrinter()
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(e)
                       ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           getDefaultPrinter()
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   }
               }
           }

           override fun onFailure(call: Call<ArrayList<Impresora>>, t: Throwable) {
               mView.showLoading(false)
//               Crashlytics.logException(t)
               FirebaseCrashlytics.getInstance().recordException(t)
               ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                   dialogInterface.dismiss()
               }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
           }


       })else mView.showLoading(false)

    }

    override fun deletePrinter(printer: Impresora) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.deleteImpresora(imei!!,printer.mac!!).enqueue(object : Callback<Void>{

           override fun onResponse(call: Call<Void>, response: Response<Void>) {
               if (response.isSuccessful){
                   mView.showLoading(false)
                   getList()
                   getDefaultPrinter()
               mView.showMsj("Se Borro")}
               else{
                   val g = response.errorBody()!!.string()
                   mView.showLoading(false)
                   try {


                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           deletePrinter(printer)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           deletePrinter(printer)
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
       })else mView.showLoading(false)
    }

    override fun setDefault(printer:Impresora) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.updateDefault(imei!!,printer.mac!!).enqueue(object : Callback<Void>{

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    getList()
                    getDefaultPrinter()
                    mView.showLoading(false)
                mView.showMsj("Impresora Predeterminada Actualizada")}
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            setDefault(printer)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            setDefault(printer)
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

        })else mView.showLoading(false)
    }
    override fun configurePrinter() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun downloadTemplate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connectPrinterBluetooth() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}