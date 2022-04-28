package com.telenord.cobrosapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.provider.Settings
import androidx.core.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.security.Security

class Utils {

    @SuppressLint("MissingPermission", "NewApi", "HardwareIds")
    fun getDeviceId(activity: Activity): String{
        val deviceId: String
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P){

            deviceId= Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
        }else{
            val manager =  activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            deviceId= manager.imei
        }
        return deviceId
    }
}