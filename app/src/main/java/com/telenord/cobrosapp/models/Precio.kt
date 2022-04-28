package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Precio {
    @SerializedName("precio")
    @Expose
    var precio: Double? = null

    @SerializedName("cantidad")
    @Expose
    var cantidad: Int? = null

    @SerializedName("monto")
    @Expose
    var monto: Double? = null

    @SerializedName("fecha")
    @Expose
    var fecha: String? = null

    @SerializedName("descripcion")
    @Expose
    var descripcion: String? = null

    fun calcularMonto():Double{
        return precio!! * cantidad!!
    }

    constructor()


    override fun toString(): String {
        return "Precio(${descripcion}precio=$precio, cantidad=$cantidad, monto=$monto)"
    }


}