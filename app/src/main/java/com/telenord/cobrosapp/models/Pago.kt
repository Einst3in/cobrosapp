package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Pago: Serializable {

    @SerializedName("cargo")
    @Expose
    var cargo : Double? = null

    @SerializedName("monto")
    @Expose
    var monto : Double? = null

    @SerializedName("tipo_pago")
    @Expose
    var tipo_pago : String? = null

    @SerializedName("insert")
    @Expose
    var insert : String? = null

    @SerializedName("imei")
    @Expose
    var imei: String? = null

    @SerializedName("opc_cargos")
    @Expose
    var opc_cargo:Pago.Tipo_OP? = null

    @SerializedName("horario")
    @Expose
    var horario:String? = null

    @SerializedName("cantidad")
    @Expose
    var cantidad: Int? = null

    @SerializedName("descuento")
    @Expose
    var descuento : Double? = null

    @SerializedName("porciento")
    @Expose
    var porciento : Double? = null

    @SerializedName("tipo")
    @Expose
    var tipo: String? = null

    @SerializedName("cod_serv")
    @Expose
    var cod_serv: String? = null

    @SerializedName("desc_serv")
    @Expose
    var desc_serv : String?  = null

    @SerializedName("plan")
    @Expose
    var plan: Int? = null

    @SerializedName("traslado")
    @Expose
    var traslado : TrasladoOrden? = null

    @SerializedName("extension")
    @Expose
    var extension = 0

    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0

    @SerializedName("lng")
    @Expose
    var lng : Double= 0.0


    @SerializedName("cambio_stb")
    @Expose
     var cambio_stb : ArrayList<STB>? = null




    var facturacion: DetalleFacturacion? = null

    constructor()




    enum class Tipo_OP{
        RC,EX,FC,TR,SB,PA,CM,CS
    }

    override fun toString(): String {
        return "Pago(cargo=$cargo, monto=$monto, tipo_pago=$tipo_pago, insert=$insert, imei=$imei, opc_cargo=$opc_cargo, horario=$horario, cantidad=$cantidad, descuento=$descuento, porciento=$porciento, tipo=$tipo, cod_serv=$cod_serv, desc_serv=$desc_serv, plan=$plan, traslado=$traslado, extension=$extension, lat=$lat, lng=$lng, cambio_stb=$cambio_stb, facturacion=$facturacion)"
    }


}