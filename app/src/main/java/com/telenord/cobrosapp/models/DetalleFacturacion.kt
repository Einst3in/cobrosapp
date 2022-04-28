package com.telenord.cobrosapp.models
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DetalleFacturacion: Serializable {

    @SerializedName("numero")
    @Expose
    var numero: String? = ""

    @SerializedName("fecha")
    @Expose
    var fecha: String? = ""

    @SerializedName("monto")
    @Expose
    var monto: Double? = 0.0

    @SerializedName("balance")
    @Expose
    var balance: Double? = 0.0

    @SerializedName("pagado")
    @Expose
    var pagado: Double? = 0.0

    @SerializedName("intereses")
    @Expose
    var intereses: Int? = 0

    @SerializedName("tipo")
    @Expose
    var tipo: String? = ""

    @SerializedName("fecha_vencimiento")
    @Expose
    var fechaVencimiento: String? = ""

    @SerializedName("concepto")
    @Expose
    var concepto: String? = ""

    constructor(numero: String?, fecha: String?, monto: Double?, balance: Double?, pagado: Double?, fechaVencimiento: String?, concepto: String?) {
        this.numero = numero
        this.fecha = fecha
        this.monto = monto
        this.balance = balance
        this.pagado = pagado
        this.intereses = intereses
        this.tipo = tipo
        this.fechaVencimiento = fechaVencimiento
        this.concepto = concepto
    }

    constructor()

    override fun toString(): String {
        return "DetalleFacturacion(numero=$numero, fecha=$fecha, monto=$monto, balance=$balance, pagado=$pagado, intereses=$intereses, tipo=$tipo, fechaVencimiento=$fechaVencimiento, concepto=$concepto)"
    }

//
//    override fun toString(): String {
//        return "DetalleFacturacion(numero=$numero, fecha=$fecha, monto=$monto, concepto=$concepto)"
//    }



}
