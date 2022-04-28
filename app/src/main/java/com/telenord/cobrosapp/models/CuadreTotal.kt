package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CuadreTotal : Serializable{
    @SerializedName("cantidad")
    @Expose
    var cantidad: String?= null

    @SerializedName("monto")
    @Expose
    var monto : String? = null

    @SerializedName("nombre")
    @Expose
    var nombre: String? = null

    @SerializedName("fecha")
    @Expose
    var fecha: String? = null
}