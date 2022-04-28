package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Movimientos: Serializable {

    @SerializedName("tipo")
    @Expose
    var tipo: String? = null

    @SerializedName("concepto")
    @Expose
    var concepto : String? = null

    @SerializedName("fecha")
    @Expose
    var fecha : String? = null

    @SerializedName("debito")
    @Expose
    var debito : Double ? = null

    @SerializedName("credito")
    @Expose
    var credito : Double ? = null

    @SerializedName("balance")
    @Expose
    var balance : Double ? = null


}