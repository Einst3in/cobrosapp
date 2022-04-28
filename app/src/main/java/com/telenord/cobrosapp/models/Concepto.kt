package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Concepto : Serializable {

    @SerializedName("Codigo")
    @Expose
    var codigo: Int? = null
    @SerializedName("Descripcion")
    @Expose
    var descripcion: String? = null

    constructor(codigo: Int?, descripcion: String?) {
        this.codigo = codigo
        this.descripcion = descripcion
    }

    override fun toString(): String {
        return "$descripcion"
    }

}
