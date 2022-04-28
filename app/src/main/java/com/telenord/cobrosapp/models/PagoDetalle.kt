package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PagoDetalle {

    @SerializedName("balance")
    @Expose
    var balance: Double? = null

    @SerializedName("balance_caja")
    @Expose
    var balance_caja: Double? = null

    @SerializedName("ac")
    @Expose
    var ac: Int? = null

    @SerializedName("mensualidad")
    @Expose
    var mensualidad: Double? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("status_descripcion")
    @Expose
    var status_descripcion: String? = null

    constructor(balance: Double?, balance_caja: Double?, ac: Int?, mensualidad: Double?, status: Int?, status_descripcion: String?) {
        this.balance = balance
        this.balance_caja = balance_caja
        this.ac = ac
        this.mensualidad = mensualidad
        this.status = status
        this.status_descripcion = status_descripcion
    }
}
