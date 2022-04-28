package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PagosPendientes : Serializable {

    @SerializedName("Codigo")
    @Expose
    var codigo: Int? = null

    @SerializedName("CobradorId")
    @Expose
    var cobradorid: Int? = null

    @SerializedName("Contrato")
    @Expose
    var contrato: String? = null

    @SerializedName("Monto")
    @Expose
    var monto : Double? = 0.00

    @SerializedName("AprobacionCardnet")
    @Expose
    var aprobacionCardnet : Int? = null

    @SerializedName("Tarjeta")
    @Expose
    var tarjeta : Int? = null

    @SerializedName("FechaRegistro")
    @Expose
    var fechaRegistro : String? = null

    @SerializedName("Imei")
    @Expose
    var imei : String? = null

    @SerializedName("NombreCobrador")
    @Expose
    var nombreCobrador : String? = null

    constructor(codigo: Int?, cobradorid: Int?, contrato : String?, monto : Double?, aprobacionCardnet : Int?, tarjeta : Int?, fechaRegistro : String?, imei : String?, nombreCobrador : String?) {
        this.codigo = codigo
        this.cobradorid = cobradorid
        this.contrato = contrato
        this.monto = monto
        this.aprobacionCardnet = aprobacionCardnet
        this.tarjeta = tarjeta
        this.fechaRegistro = fechaRegistro
        this.imei = imei
        this.nombreCobrador = nombreCobrador
    }

    override fun toString(): String {
        return "PagosPendientes(codigo=$codigo, cobradorid=$cobradorid, contrato=$contrato,monto=$monto, aprobacionCardnet=$aprobacionCardnet, tarjeta=$tarjeta, fechaRegistro=$fechaRegistro, imei=$imei, nombreCobrador=$nombreCobrador)"
    }

}