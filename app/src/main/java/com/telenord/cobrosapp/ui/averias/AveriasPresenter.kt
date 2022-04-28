package com.telenord.cobrosapp.ui.averias

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AveriasPresenter(private val mView: AveriaContract.View, private val mCtx: Context) : AveriaContract.Listener {

    override fun getConceptos() {
        mView.showProgress(true)
        val rest = Rest.getCentral(mCtx)
        if (rest != null) rest.getConceptos(1).enqueue(object : Callback<ArrayList<Concepto>> {

            override fun onResponse(call: Call<ArrayList<Concepto>>, response: Response<ArrayList<Concepto>>) {
                if (response.isSuccessful) {

                    mView.fillSpinner(response.body()!!)
                    mView.showProgress(false)
                } else {
                    val g = response.errorBody()!!.string()
                    mView.showProgress(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getConceptos()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getConceptos()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }

            }

            override fun onFailure(call: Call<ArrayList<Concepto>>, t: Throwable) {
                mView.showProgress(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }


        }) else mView.showProgress(false)
    }

    override fun getContacto() {
        mView.showProgress(true)
        val rest = Rest.getCentral(mCtx)
        if (rest != null) rest.getContacto().enqueue(object : Callback<Contacto> {

            override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                if (response.isSuccessful) {
                    mView.showContacto(response.body()!!)
                    mView.showProgress(false)
                } else {
                    val g = response.errorBody()!!.string()
                    mView.showProgress(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getContacto()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getContacto()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }

            }

            override fun onFailure(call: Call<Contacto>, t: Throwable) {
                mView.showProgress(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }


        }) else mView.showProgress(false)
    }




    override fun postAveria(averia: Averia,contacto: Contacto) {
        mView.showProgress(true)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                "http://api.whatsapp.com/send?phone=${contacto.whatsapp}&text=Favor darle asistencia al siguiente cliente: \n" +
                        "Contrato ${averia.contrato} \n" +
                        "Concepto: ${averia.concepto} \n" +
                        "Observacion: ${averia.observacion} \n" +
                        "Usuario: ${contacto.remitente}"
            ))

            mCtx.startActivity(intent)
            ((mCtx) as AveriasActivity).finish()
        }catch (e: Exception){
            e.printStackTrace()
//            Crashlytics.logException(e)
            FirebaseCrashlytics.getInstance().recordException(Throwable(e))
            ErrorUtils().showErrorApi(mCtx,  "${e.localizedMessage}", 0, "${e.message}", DialogInterface.OnClickListener { dialogInterface, i ->

                dialogInterface.dismiss()
            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
        }finally {
            mView.showProgress(false)
        }

    }
}