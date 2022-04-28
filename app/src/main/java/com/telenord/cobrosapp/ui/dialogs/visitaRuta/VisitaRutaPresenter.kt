package com.telenord.cobrosapp.ui.dialogs.visitaRuta

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
//import com.crashlytics.android.Crashlytics
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.telenord.cobrosapp.models.Concepto
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.util.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisitaRutaPresenter (private val mCtx: Context, private val mView: VisitaRutaContract.View): VisitaRutaContract.Listener{

    override fun getConceptos() {
        mView.showLoading(true)
        val rest = Rest.getCentral(mCtx)
        if (rest!=null) rest.getVisitasConceptos().enqueue(object : Callback<ArrayList<Concepto>>{

            override fun onResponse(call: Call<ArrayList<Concepto>>, response: Response<ArrayList<Concepto>>) {
               if (response.isSuccessful){
                   mView.showLoading(false)
                   mView.showConceptos(response.body()!!)
               }
                else{
                   val g = response.errorBody()!!.string()
                   mView.showLoading(false)
                   try {


                       val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                       Crashlytics.logException(Throwable(errorResponse.mensaje))
                       FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                       ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                           getConceptos()
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   } catch (e: Exception) {
                       e.printStackTrace()
//                       Crashlytics.logException(e)
                       FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                       ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                           getConceptos()
                           dialogInterface.dismiss()
                       }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                   }
               }
            }

            override fun onFailure(call: Call<ArrayList<Concepto>>, t: Throwable) {
               FirebaseApp.initializeApp(mCtx)
                mView.showLoading(false)
//                Crashlytics.logException(t)
                FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                ErrorUtils().showErrorApi(mCtx,"Error Interno",500,t.localizedMessage,DialogInterface.OnClickListener{ dialogInterface, i ->
                    dialogInterface.dismiss()},
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()})
            }


        }) else mView.showLoading(false)
    }

    override fun postVisita(contrato: String, concepto: Int) {
        lateinit var fusedLocationClient: FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mCtx)

        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                var mLocation = Location("")
                mLocation.longitude = 0.00
                mLocation.latitude = 0.00

                if (location != null) {
                    mLocation = location
                }

                mView.showLoading(true)
                val rest = Rest.getCentral(mCtx)
                if (rest != null){
                    rest.postVisitaRuta(contrato,concepto,mLocation.latitude,mLocation.longitude).enqueue(object : Callback<Void>{
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if(response.isSuccessful){
                                mView.showLoading(false)
                                mView.visitaPosteada()
                            }  else{
                                val g = response.errorBody()!!.string()
                                mView.showLoading(false)
                                try {


                                    val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                                    Crashlytics.logException(Throwable(errorResponse.mensaje))
                                    FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                                    ErrorUtils().showErrorApi(mCtx, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                                        postVisita(contrato, concepto)
                                        dialogInterface.dismiss()
                                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                                } catch (e: Exception) {
                                    e.printStackTrace()
//                                    Crashlytics.logException(e)
                                    FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                                    ErrorUtils().showErrorApi(mCtx, response.message(), response.code(), "$g", DialogInterface.OnClickListener { dialogInterface, i ->
                                        postVisita(contrato, concepto)
                                        dialogInterface.dismiss()
                                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                                }
                            }


                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            FirebaseApp.initializeApp(mCtx)
                            mView.showLoading(false)
//                            Crashlytics.logException(t)
                            FirebaseCrashlytics.getInstance().recordException(t)
                            ErrorUtils().showErrorApi(mCtx,"Error Interno",500,t.localizedMessage,DialogInterface.OnClickListener{ dialogInterface, i ->
                                dialogInterface.dismiss()},
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()})
                        }


                    })
                }

            }
    }
}