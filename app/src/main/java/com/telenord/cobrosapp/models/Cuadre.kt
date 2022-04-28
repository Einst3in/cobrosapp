package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Cuadre: Serializable {

    @SerializedName("cuadre")
    @Expose
    var cuadre: ArrayList<CuadreDetalle>? = null

    @SerializedName("total")
    @Expose
    var total: CuadreTotal? = null

    @SerializedName("documento")
    @Expose
    var documento: DocumentoFact? = null


    constructor()
}