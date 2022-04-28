package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Banco {

    @SerializedName("codigo")
    @Expose
    var codigo: Int? = null

    @SerializedName("nombre")
    @Expose
    var nombre: String? = null

    constructor(codigo: Int?, nombre: String?) {
        this.codigo = codigo
        this.nombre = nombre
    }

    override fun toString(): String {
        return nombre!!
    }

}