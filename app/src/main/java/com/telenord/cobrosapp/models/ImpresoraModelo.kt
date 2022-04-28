package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImpresoraModelo: Serializable {


    @SerializedName("id")
    @Expose
    var id : Int? = null

    @SerializedName("modelo")
    @Expose
    var modelo : String?= null

    @SerializedName("milimetros")
    @Expose
    var milimetros: String?= null

    @SerializedName("caracteres_linea")
    @Expose
    var caracteresLinea: Int? = null

    constructor(id: Int?, modelo: String?, milimetros: String?, caracteresLinea: Int?) {
        this.id = id
        this.modelo = modelo
        this.milimetros = milimetros
        this.caracteresLinea = caracteresLinea
    }

    override fun toString(): String {
        return "$modelo ${milimetros}mm"
    }


}