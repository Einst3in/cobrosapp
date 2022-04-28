package com.telenord.cobrosapp.models

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Info: Serializable{


    @SerializedName("usuario")
    @Expose
    var usuario: String? = null
    @SerializedName("oficina")
    @Expose
    var oficina: String? = null
    @SerializedName("almacen")
    @Expose
    var almacen: Int? = null
    @SerializedName("ciudad")
    @Expose
    var ciudad: String? = null
    @SerializedName("impresora")
    @Expose
    var impresora: Impresora? = null

    //test codigo cobrador
    @SerializedName("cobradorid")
    @Expose
    var cobradorid : Int? = null

    constructor()

    constructor(usuario: String?, oficina: String?, almacen: Int?, ciudad: String?, cobradorid : Int?) {
        this.usuario = usuario
        this.oficina = oficina
        this.almacen = almacen
        this.ciudad = ciudad
        this.cobradorid = cobradorid
    }

    override fun toString(): String {
        return "Info(usuario=$usuario, oficina=$oficina, almacen=$almacen, ciudad=$ciudad, impresora=$impresora, cobradorid=$cobradorid)"
    }


}
