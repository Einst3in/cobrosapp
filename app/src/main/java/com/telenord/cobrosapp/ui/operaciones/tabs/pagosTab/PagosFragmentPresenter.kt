package com.telenord.cobrosapp.ui.operaciones.tabs.pagosTab

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.ResponseFactura
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PagosFragmentPresenter(private val mView : PagosFragmentContract.View,private val context: Context): PagosFragmentContract.Listener {

    override fun deletePagoPendiente(contrato: String) {
        Log.d("Contrato->", "a eliminar->" +contrato)
        val rest = Rest.getCentral(context)
        if(rest!=null)rest.deletePagopendiente(imei, contrato).enqueue(object : Callback<Void>{

            override fun onResponse(call: Call<Void>, response: Response<Void>){
                if(response.isSuccessful){
                    mView.showLoading(false)
                    val r = response.body()
                    Log.d("statusDB", r.toString())
                }else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try{
                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
                        ErrorUtils().showErrorApi(context, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }catch(e:Exception){
                        e.printStackTrace()
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(context, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(context, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })
    }

    val imei = SharedPreferenceManager.getInstance(context).dispositivo.imei!!
    override fun getReimprimir(contrato: Clientes) {

        mView.showLoading(true)
        val rest = Rest.getCentral(context)
        if (rest!=null)rest.getReimprimir(imei,contrato!!.contrato!!,imei).enqueue(object : Callback<ResponseFactura>{

            override fun onResponse(call: Call<ResponseFactura>, response: Response<ResponseFactura>) {
               if (response.isSuccessful && response.code() == 200){
                   var factura = response.body()!!
                   factura.reimpresion =1
                   mView!!.Reimprmir(factura)
                   mView.showLoading(false)
                   mView.reimpresionCompleta()
               }else if (response.code() == 204){
                mView.showError("No Hay Factura Realizada")
                   mView.showLoading(false)
               }else{
                   val g = response.errorBody()!!.string()
                   mView.showLoading(false)

                   try {


                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(context, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           //FindLocalidad(localidad)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(context, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           //FindLocalidad(localidad)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   }
               }
            }
            override fun onFailure(call: Call<ResponseFactura>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(context, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }

        }) else mView.showLoading(false)
    }

    override fun procesarPagoPendiente(cliente: Clientes, pago: Pago) {
        val contrato  = cliente.contrato!!
        mView.showLoading(true)

        pago.lat = 0.00
        pago.lng = 0.00
        val rest = Rest.getCentral(context)
        if (rest!=null)rest.postPagos(SharedPreferenceManager.getInstance(context).dispositivo.imei!!,contrato
            ,pago
            /*pago.monto!!,
             pago.tipo_pago.toString().toUpperCase()
            ,pago.insert!!.toString(),imei!!,
            tipo_OP,horario,cant,descuento,porciento*/).enqueue(object : Callback<ResponseFactura> {
            override fun onResponse(call: Call<ResponseFactura>, response: Response<ResponseFactura>) {
                if (response.isSuccessful){
                    Log.e("Cobro", "el pago se hizo bien")
                    mView.showLoading(false)
                    deletePagoPendiente(contrato)
                    mView.getFactura(response.body()!!)

                }
                else{
                    val g = response.errorBody()!!.string()
                    mView.showLoading(false)
                    try {

                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(context, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            procesarPagoPendiente(cliente,pago)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                       Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(context, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            //FindLocalidad(localidad)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }

                }

            }

            override fun onFailure(call: Call<ResponseFactura>, t: Throwable) {

                mView.showLoading(false)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(context, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })else mView.showLoading(false)
    }




}