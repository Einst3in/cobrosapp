package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PrecioSTB {
    @SerializedName("descripcion")
    @Expose
    var descripcion:String? = null
    @SerializedName("precio")
    @Expose
    var precio: Double = 0.0
    @SerializedName("codigo")
    @Expose
    var codigo: Int = 0

    @SerializedName("tecnologia")
    @Expose
    var tecnologia: String? = ""

    @SerializedName("stb_anterior")
    @Expose
    var stb_anterior: String? = null

    @SerializedName("cant")
    @Expose
    var cantidad:Int = 0

    @SerializedName("margen")
    @Expose
    var margen: Int = 0

    constructor()




     fun precioString(): String {
        return "PrecioSTB(descripcion=$descripcion, precio=$precio, codigo=$codigo, tecnologia=$tecnologia, stb_anterior=$stb_anterior, cantidad=$cantidad)"
    }

    override fun toString(): String {
        return "$descripcion"
    }

}






