package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Impresora : Serializable{

    @SerializedName("mac")
    @Expose
    var mac: String? = null

    @SerializedName("modelo")
    @Expose
    var modelo: String? = null

    @SerializedName("milimetros")
    @Expose
    var milimetros : Int? = null

    @SerializedName("caracteres_linea")
    @Expose
    var caracteresLinea : Int? = null

    @SerializedName("predeterminado")
    @Expose
    var predeterminado : Int? = null

    @SerializedName("imei")
    @Expose
    var imei: String? = null

    @SerializedName("propietario")
    @Expose
    var propietario: String? = null

    @SerializedName ("modelo_id")
    @Expose
    var modelo_id : Int? = null

    constructor(mac: String?, modelo: String?, milimetros: Int?, caracteresLinea: Int?, predeterminado: Int?, imei: String?, propietario: String?) {
        this.mac = mac
        this.modelo = modelo
        this.milimetros = milimetros
        this.caracteresLinea = caracteresLinea
        this.predeterminado = predeterminado
        this.imei = imei
        this.propietario = propietario
    }

    constructor(mac: String?, modelo: String?) {
        this.modelo = modelo
        this.mac = mac
    }

    fun isFuncional(): Boolean{
        return this != null && !this.mac.isNullOrEmpty() && this.modelo_id != null
    }


    override fun toString(): String {
        return "Impresora(mac=$mac, modelo=$modelo, milimetros=$milimetros, caracteresLinea=$caracteresLinea, predeterminado=$predeterminado, imei=$imei, propietario=$propietario)"
    }


}