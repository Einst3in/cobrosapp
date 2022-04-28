package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MovimientosResponse : Serializable {

    @SerializedName("cantidad")
    @Expose
    var cantidad: String? = ""

    @SerializedName("movimientos")
    @Expose
    var movimientos : ArrayList<Movimientos>? = null


}