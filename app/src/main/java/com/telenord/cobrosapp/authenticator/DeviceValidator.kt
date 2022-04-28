package com.telenord.cobrosapp.authenticator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.telenord.cobrosapp.R
import kotlinx.android.synthetic.main.activity_device_validator.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class DeviceValidator : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_validator)

        val imei = intent.getStringExtra("imei")
        Imei.text = "${imei}"

        Aceptar.onClick { finish() }
    }
}
