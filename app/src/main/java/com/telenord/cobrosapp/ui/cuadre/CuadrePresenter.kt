package com.telenord.cobrosapp.ui.cuadre

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.Cuadre
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.impresora.PariedDevices.PairedDevices
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CuadrePresenter(private val mView: CuadreContract.View,private val mCtx: Context): CuadreContract.Listener
{
    val imei = SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!
    override fun getCuadre(fecha: String) {
        mView.showLoading(true)

        val rest = Rest.getCentral(mCtx)

                if (rest!=null)rest.getCuadres(SharedPreferenceManager.getInstance(mCtx).dispositivo.imei!!,fecha,imei).enqueue(object : Callback<Cuadre>{

            override fun onResponse(call: Call<Cuadre>, response: Response<Cuadre>) {
               if (response.isSuccessful){
                   mView.showCuadre(response.body()!!)
                   mView.showLoading(false)
               }
                else{
                   val g = response.errorBody()!!.string()
                   mView.showLoading(false)
                   try {


                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           getCuadre(fecha)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           getCuadre(fecha)
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   }
               }
            }

            override fun onFailure(call: Call<Cuadre>, t: Throwable) {
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }

        }) else mView.showLoading(false)
    }
    override fun hayDefaultPrinter(): Boolean {

        val imei  = SharedPreferenceManager.getInstance(mCtx).dispositivo.imei
        val rest = Rest.getCentral(mCtx)
        var resp = false
        if (rest!=null) rest.getImpresoras(imei!!,1).enqueue(object : Callback<ArrayList<Impresora>> {

            override fun onResponse(call: Call<ArrayList<Impresora>>, response: Response<ArrayList<Impresora>>) {

                if (response.isSuccessful && !response.body().isNullOrEmpty()){
                    SharedPreferenceManager.getInstance(mCtx).printer(response.body()!![0])
                    mView.showLoading(false)
                    resp = true
                } else{

                    try {
                        val g = response.errorBody()!!.string()

                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            hayDefaultPrinter()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(mCtx)
                        alertDialogBuilder.setTitle("No hay impresora predeterminada")
                        alertDialogBuilder.setMessage("Debe ir a configuracion y agregar una nueva impresora predeterminada.")
                        alertDialogBuilder.setCancelable(false)
                        alertDialogBuilder.setIcon(R.drawable.ic_cancel)

                        alertDialogBuilder.setPositiveButton("Configurar") { dialogInterface, i -> mCtx.startActivity<PairedDevices>()}

                        alertDialogBuilder.setNegativeButton("Salir") { dialogInterface, i -> dialogInterface.dismiss()
                            mView.showLoading(false)}

                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Impresora>>, t: Throwable) {

//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    mView.showLoading(false)
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss()
                    mView.showLoading(false)})
            }

        })else mView.showLoading(false)
        return resp


    }
}