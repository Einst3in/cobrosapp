package com.telenord.cobrosapp.ui.clientes

import android.content.Context
import android.content.DialogInterface
//import com.crashlytics.android.Crashlytics

import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.telenord.cobrosapp.Response.ClientesResponse
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.ErrorResponse

import com.telenord.cobrosapp.util.ErrorUtils
import com.telenord.cobrosapp.util.Memory.fields

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class Clientes_Presenter(private val mView: Clientes_Contract.View, private val mCtx: Context, private val adapt: PaginationAdapter) : Clientes_Contract.Listener, Clientes_Contract.Adapter {
    override fun showCount(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    init {

        FirebaseApp.initializeApp(mCtx)
    }


    override fun Find(ref: String, page: Int) {
        val pagina: Int
        if (page == 0) {
            mView.ShowLoading(true)
            pagina = page
        } else pagina = page * fields

        val rest = Rest.getCentral(mCtx)
        if (rest != null) rest.getClientes(ref, pagina, 10).enqueue(object : Callback<ClientesResponse> {
            override fun onResponse(call: Call<ClientesResponse>, response: Response<ClientesResponse>) {
                mView.ShowLoading(false)

                if (response.isSuccessful) {
                    val totalPages = getTotalPages(response.body()!!.count!!)
                    if (page == 0) {

                        adapt!!.clear()
                        /*  Log.e("Conteo", "${response.body()!!.count!!} paginas $totalPages")*/

                        var resultados = response.body()!!.clientes
                        adapt!!.addAll(resultados!!)

                        /*if (refreshLayout!!.isRefreshing)refreshLayout!!.isRefreshing = false*/

                        if (response!!.body()!!.count!! > fields) {
                            if (page + 1 <= totalPages!!) adapt!!.addLoadingFooter()
                            else mView.isLastPage(true)
                        } else mView.isLastPage(true)
                    } else {
                        adapt!!.removeLoadingFooter()
                        mView.isLoading(false)
                        var resultados = response.body()!!.clientes
                        adapt!!.addAll(response.body()!!.clientes!!)

                        if (page + 1 != totalPages) adapt!!.addLoadingFooter()
                        else mView.isLastPage(true)
                    }
                    var c = adapt!!.itemCount - 1
                    if (c < 0) c = 0

                    mView.showCount(c, response.body()!!.count!!)

                } else {
                    val g = response.errorBody()!!.string()
                    mView.ShowLoading(false)
                    try {


                        val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                        Crashlytics.logException(Throwable(errorResponse.mensaje))
                        FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                        ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                            Find(ref, page)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        Crashlytics.logException(e)
                        FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                        ErrorUtils().showErrorApi(mCtx,  response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                            Find(ref, page)
                            dialogInterface.dismiss()
                        }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    }
                }
            }

            override fun onFailure(call: Call<ClientesResponse>, t: Throwable) {
                FirebaseApp.initializeApp(mCtx)
                mView.ShowLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx, "Error Interno", 500, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            }
        }) else mView.ShowLoading(false)

    }

    fun getTotalPages(count: Int): Int {
        var total: Int?
        if ((count % fields) != 0) {
            total = ((count - (count % fields)) / fields) + 1
        } else {
            total = count / fields
        }
        return total
    }

    override fun showTelefonos(telefonos: ArrayList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMap(lat: Double, lng: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goDetalle(cliente: Clientes) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
