package com.telenord.cobrosapp.ui.ruta

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import com.telenord.cobrosapp.models.Concepto
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Ruta
import com.telenord.cobrosapp.models.RutaResponse
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.util.ErrorUtils
import com.telenord.cobrosapp.util.Memory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RutaPresenter (private val mView: RutaContract.View, private val mCtx: Context, private val adapt: RutaAdapter?): RutaContract.Listener {


    override fun getConceptos() {
        mView.ShowLoading(true)
    val rest = Rest.getCentral(mCtx)
        if (rest!=null) rest.getRutaConceptos().enqueue(object : Callback<ArrayList<Concepto>>{
            override fun onResponse(call: Call<ArrayList<Concepto>>, response: Response<ArrayList<Concepto>>) {

                if (response.isSuccessful){
                    mView.ShowLoading(false)
                    mView.showConceptos(response.body()!!)
                }
                else
                {
                    val g = response.errorBody()!!.string()
                    mView.isLoading(false)
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
                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getConceptos()
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }


                }
            }
            override fun onFailure(call: Call<ArrayList<Concepto>>, t: Throwable) {

                mView.isLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        })else  mView.isLoading(false)
    }

    override fun getRuta(tipo: Int,pagina: Int) {
        val page: Int
        if (pagina == 0) {
            mView.ShowLoading(true)
            page = pagina
        } else page = pagina * Memory.fields

        val rest = Rest.getCentral(mCtx)
        if (rest!=null)rest.getRuta(tipo,page).enqueue(object : Callback<RutaResponse>{

            @SuppressLint("UseSparseArrays")
            override fun onResponse(call: Call<RutaResponse>, response: Response<RutaResponse>) {
               mView.ShowLoading(false)

                if (response.isSuccessful){
                    val totalPages = getTotalPages(response.body()!!.count!!)
                    if (page == 0){
                        adapt!!.clear()


                        val results = response.body()!!.clientes
                        val p0 = results
                        val p4 = p0!!.distinctBy { s -> s.sector }
                        val sectores = p4.map { it.sector!! }

                        mView.showRuta(p0,sectores)

                        if (response.body()!!.count!! > Memory.fields){
                           if (pagina + 1 <= totalPages)adapt.addLoadingFooter()
                            else mView.isLastPage(true)
                        }else mView.isLastPage(true)
                    }
                    else{
                        adapt!!.removeLoadingFooter()
                        mView.isLoading(false)

                        var results = response.body()!!.clientes
                        adapt.addAll(results!!)
                        if (page + 1 != totalPages)adapt!!.addLoadingFooter()
                        else mView.isLastPage(true)
                    }
                    var c = adapt!!.itemCount - 1
                    if (c < 0) c = 0

                    mView.showCount(c, response.body()!!.count!!)
                }
                else
                {
                    val g = response.errorBody()!!.string()
                    mView.isLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            getRuta(tipo,pagina)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                     } catch (e: Exception) {
                       mView.isLoading(false)
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))

                        ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            getRuta(tipo,pagina)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }


                }
            }

            override fun onFailure(call: Call<RutaResponse>, t: Throwable) {
                mView.isLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }


        })else  mView.isLoading(false)
    }

    fun getTotalPages(count: Int): Int {
        var total: Int?
        if ((count % Memory.fields) != 0) {
            total = ((count - (count % Memory.fields)) / Memory.fields) + 1
        } else {
            total = count / Memory.fields
        }
        return total
    }

    companion object{
        val mList = ArrayList<Ruta>()
    }

}