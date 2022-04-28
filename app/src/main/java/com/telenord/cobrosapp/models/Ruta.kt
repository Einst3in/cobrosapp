package com.telenord.cobrosapp.models

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Ruta : Serializable {

    @SerializedName("contrato")
    @Expose
    var contrato: String? = null

    @SerializedName("nombre")
    @Expose
    var nombre: String? = null

    @SerializedName("direccion")
    @Expose
    var direccion: String? = null

    @SerializedName("fecha_ultimo_pago")
    @Expose
    var fechaUltimoPago: String? = null

    @SerializedName("geo")
    @Expose
    var geo: String? = null

    @SerializedName("telefono")
    @Expose
    var telefono: String? = null

    @SerializedName("celular")
    @Expose
    var celular: String? = null

    @SerializedName("monto_ultimo_pago")
    @Expose
    var montoUltimoPago: Double? = null

    @SerializedName("balance")
    @Expose
    var balance: Double? = null

    @SerializedName("mensualidad")
    @Expose
    var mensualidad: Double? = null

    @SerializedName("is_corte")
    @Expose
    var isCorte: Int? = null

    @SerializedName("check_visita")
    @Expose
    var checkVisita: Int? = null

    @SerializedName("ultima_visita")
    @Expose
    var ultimaVisita: String? = null

    @SerializedName("cedula")
    @Expose
    var cedula: String? = null

    @SerializedName("sector")
    @Expose
    var sector: String? = null


    constructor()

    constructor(contrato: String?, nombre: String?, direccion: String?, fechaUltimoPago: String?, geo: String?, telefono: String?, celular: String?, montoUltimoPago: Double?, balance: Double?, mensualidad: Double?, isCorte: Int?, checkVisita: Int?, ultimaVisita: String?, cedula: String?) {
        this.contrato = contrato
        this.nombre = nombre
        this.direccion = direccion
        this.fechaUltimoPago = fechaUltimoPago
        this.geo = geo
        this.telefono = telefono
        this.celular = celular
        this.montoUltimoPago = montoUltimoPago
        this.balance = balance
        this.mensualidad = mensualidad
        this.isCorte = isCorte
        this.checkVisita = checkVisita
        this.ultimaVisita = ultimaVisita
        this.cedula = cedula
    }


    override fun toString(): String {
        return "Ruta(contrato=$contrato, nombre=$nombre, direccion=$direccion, fechaUltimoPago=$fechaUltimoPago, geo=$geo, telefono=$telefono, celular=$celular, montoUltimoPago=$montoUltimoPago, balance=$balance, mensualidad=$mensualidad, isCorte=$isCorte, checkVisita=$checkVisita, ultimaVisita=$ultimaVisita)"
    }


}