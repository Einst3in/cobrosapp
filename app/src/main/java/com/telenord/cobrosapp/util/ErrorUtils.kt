package com.telenord.cobrosapp.util


import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics
import org.json.JSONException
import org.json.JSONObject
import com.google.gson.Gson
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.R


class ErrorUtils {

    fun showErrorApi(ctx: Context, Error: String, Code: Int, mensaje: String?, retry: DialogInterface.OnClickListener, salir: DialogInterface.OnClickListener) {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(ctx)
        alertDialogBuilder.setTitle("$Code=>$Error")
        alertDialogBuilder.setMessage("${mensaje}")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setIcon(R.drawable.ic_cancel)

        alertDialogBuilder.setPositiveButton("Reintentar", retry)

        alertDialogBuilder.setNegativeButton("Salir", salir)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun parseError(f: String): ErrorResponse? {
        var error = ErrorResponse()
            try {

            val gson = Gson()
            var j: JSONObject? = null
            Log.e("json error=>>", f)
            j = JSONObject(f)
                Log.e("Json Built","${j}")
            error = gson.fromJson(j.toString(), ErrorResponse::class.java)

            Log.e("error=>>", "$error")



        } catch (e: JSONException) {
            e.printStackTrace()
//            Crashlytics.logException(e)
                FirebaseCrashlytics.getInstance().recordException(Throwable(e))
        }

        //Gson gson = new Gson();

        Log.e("ErrorUtils",error.toString())
        return error
    }




}