package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Contacto {
    @SerializedName("whatsapp")
    @Expose
    var whatsapp : String = ""

    @SerializedName("remitente")
    @Expose
    var remitente : String = ""



    override fun toString(): String {
        return "Contacto(whatsapp='$whatsapp', remitente='$remitente')"
    }


}