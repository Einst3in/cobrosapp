package com.telenord.cobrosapp.ui.cardnet

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.ResponseFactura
import com.telenord.cobrosapp.print.printUtils.PrinterOps
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.ErrorUtils
import kotlinx.android.synthetic.main.activity_cardnet.*
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CardnetPresenter (var mCtx: Context, val mView : CardnetContract.View): CardnetContract.Listener{

    override fun success(bundle: Bundle) {
        mView.goHome(bundle)
    }

    override fun retry(contrato: String, pago : Pago) {
        mView.showErrorCardnet(contrato, pago)
    }

    override fun reintentarPago(contrato: String, pago : Pago) {
        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.postPagosCardnet(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,contrato
            ,pago
            /*pago.monto!!,
             pago.tipo_pago.toString().toUpperCase()
            ,pago.insert!!.toString(),imei!!,
            tipo_OP,horario,cant,descuento,porciento*/).enqueue(object : Callback<ResponseFactura> {
            override fun onResponse(call: Call<ResponseFactura>, response: Response<ResponseFactura>) {
                if (response.isSuccessful){
                    val g = response.body()

                    Log.e("Cobro", "el pago se hizo bien")
                    //mView.getFactura(response.body()!!)
                }
                else{
                    val g = response.errorBody()!!.string()
//                    mView.showLoading(false)
                    mView.showErrorCardnet(contrato, pago)
                    try {

                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
//                            Cobrar(cliente,pago)
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


}