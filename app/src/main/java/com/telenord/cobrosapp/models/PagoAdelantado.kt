package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PagoAdelantado {

    @SerializedName("mensualidad")
    @Expose
    var mensualidad : Double? = null

    @SerializedName("descuento")
    @Expose
    var descuento: Double? = null

    @SerializedName("total")
    @Expose
    var total : Double? = null

    @SerializedName("neto")
    @Expose
    var neto : Double? = null

    @SerializedName("meses")
    @Expose
    var meses: Int? = null

    @SerializedName("porciento")
    @Expose
    var porciento : Double? = null

    @SerializedName("fecha")
    @Expose
    var fecha: String? = null

    constructor(mensualidad: Double?, descuento: Double?, total: Double?, neto: Double?, meses: Int?, porciento: Double?) {
        this.mensualidad = mensualidad
        this.descuento = descuento
        this.total = total
        this.neto = neto
        this.meses = meses
        this.porciento = porciento
    }

    constructor()

}