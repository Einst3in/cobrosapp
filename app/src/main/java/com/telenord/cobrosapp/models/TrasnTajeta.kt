package com.telenord.cobrosapp.models

class TrasnTajeta{

    var num_tarjeta: String? = null

    var banco_id :Int? = null

    var monto : Double? = null

    var num_boucher : String? =  null

    var tipo_tarjeta : TipoTarjeta? = null

    constructor(num_tarjeta: String?, banco_id: Int?, monto: Double?, num_boucher: String?, tipo_tarjeta: TipoTarjeta?) {
        this.num_tarjeta = num_tarjeta
        this.banco_id = banco_id
        this.monto = monto
        this.num_boucher = num_boucher
        this.tipo_tarjeta = tipo_tarjeta
    }



    enum class TipoTarjeta{
        D,C
    }

    override fun toString(): String {
        return "$num_tarjeta,$banco_id,$monto,$num_boucher,'$tipo_tarjeta')"
    }
}