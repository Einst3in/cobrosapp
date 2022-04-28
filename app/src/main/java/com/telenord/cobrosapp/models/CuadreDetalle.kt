package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CuadreDetalle : Serializable {

    @SerializedName("recibo")
    @Expose
    var recibo: String? = null

    @SerializedName("contrato")
    @Expose
    var contrato : String? = null

    @SerializedName("fecha")
    @Expose
    var fecha: String? = null

    @SerializedName("monto")
    @Expose
    var monto : String? = null

}