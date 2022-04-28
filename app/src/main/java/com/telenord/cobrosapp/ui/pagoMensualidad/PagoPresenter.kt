package com.telenord.cobrosapp.ui.pagoMensualidad

import android.content.Context
import android.util.Log
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.Rest.Rest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.DialogInterface
import android.os.Bundle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils


class PagoPresenter(private val mView: PagoContract.View, private val mCtx : Context): PagoContract.Listener {



    override fun Cobrar(cliente: Clientes, pago: Pago/*, tipo_OP:Pago.Tipo_OP, horario: String?, cant:Int?,descuento: Double?,porciento: Double?*/) {
        val contrato  = cliente.contrato!!
        mView.showLoading(true)
//        val  location = LocationService.mLastLocation
        pago.lat = 0.00
        pago.lng = 0.00
        val rest = Rest.getCentral(mCtx)
                if (rest!=null)rest.postPagos(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato
                 ,pago
                /*pago.monto!!,
                 pago.tipo_pago.toString().toUpperCase()
                ,pago.insert!!.toString(),imei!!,
                tipo_OP,horario,cant,descuento,porciento*/).enqueue(object : Callback<ResponseFactura> {
            override fun onResponse(call: Call<ResponseFactura>, response: Response<ResponseFactura>) {
               if (response.isSuccessful){

                   Log.e("Cobro", "el pago se hizo bien")
                   mView.showLoading(false)
                    mView.getFactura(response.body()!!)

               }
                else{
                   val g = response.errorBody()!!.string()
                   mView.showLoading(false)
                   try {

                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           Cobrar(cliente,pago)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
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
                ErrorUtils().showErrorApi(mCtx, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })else mView.showLoading(false)

    }

    override fun getAcuerdo(contrato: String) {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null) rest.getAcuerdo(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato).enqueue(object :Callback<Acuerdo>{

            override fun onResponse(call: Call<Acuerdo>, response: Response<Acuerdo>) {

               if (response.isSuccessful){
                   mView.showLoading(false)
                   mView.showAcuerdo(response.body()!!)
               }else{

                   val g = response.errorBody()!!.string()
                   mView.showLoading(false)
                   try {


                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           getAcuerdo(contrato)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           //FindLocalidad(localidad)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   }
               }
            }

            override fun onFailure(call: Call<Acuerdo>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }

        })

        else mView.showLoading(false)
    }

    override fun CobrarCardnet(factura: ResponseFactura) {
        mView.getFactura(factura)
    }

}