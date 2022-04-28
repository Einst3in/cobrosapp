package com.telenord.cobrosapp.util

import android.content.Context
import android.net.ConnectivityManager
import org.jetbrains.anko.alert

class ConnectionCheck(private val context: Context){
    fun isConnectingToInternet(): Boolean{
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        if (info!=null && info.isAvailable && info.isConnected)
        {
            return true
        }else
        {
            context.alert("Verifique su conexion a WiFi o Red Movil","No tiene Conexion a Internet",{
                positiveButton("Aceptar",{})
            }).show()
            return false
        }
    }
}