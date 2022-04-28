package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Boleto : Serializable {

    @SerializedName("descripcion")
    @Expose
    var descripcion: String? = null

    @SerializedName("ticket")
    @Expose
    var ticket: String? = null
}