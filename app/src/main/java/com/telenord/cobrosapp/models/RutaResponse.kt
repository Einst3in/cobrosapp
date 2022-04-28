package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RutaResponse: Serializable {

    @SerializedName("clientes")
    @Expose
    var clientes: ArrayList<Ruta>? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null

    constructor(clientes: ArrayList<Ruta>?, count: Int?) {
        this.clientes = clientes
        this.count = count
    }

    constructor()

}