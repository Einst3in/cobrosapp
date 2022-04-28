package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ReciboFact : Serializable {
    @SerializedName("monto")
    @Expose
    var monto: Double? = 0.00
    @SerializedName("usuario")
    @Expose
    var usuario: String? = null
    @SerializedName("nombre_cliente")
    @Expose
    var nombreCliente: String? = null
    @SerializedName("complex_android_string")
    @Expose
    var complexAndroidString: String? = null
    @SerializedName("contrato")
    @Expose
    var contrato: String? = null
    @SerializedName("trn")
    @Expose
    var trn: String? = null
    @SerializedName("concepto_pago")
    @Expose
    var conceptoPago: String? = null
    @SerializedName("concepto_generado")
    @Expose
    var conceptoGenerado: String? = null
    @SerializedName("fecha")
    @Expose
    var fecha: String? = null
    @SerializedName("cobrador")
    @Expose
    var cobrador: String? = null
    @SerializedName("t_serv")
    @Expose
    var tServ: Int? = null
    @SerializedName("balance")
    @Expose
    var balance: Double ? = 0.00
    @SerializedName("balance_anterior")
    @Expose
    var balanceAnterior: Double? = 0.00
    @SerializedName("tipo_pago")
    @Expose
    var tipo_pago: String? = null
    @SerializedName("authorization_code")
    @Expose
    var authorization_code: String? = null
    @SerializedName("credit_card_number")
    @Expose
    var credit_card_number: String? = null

    override fun toString(): String {
        return "ReciboFact(monto=$monto, usuario=$usuario, nombreCliente=$nombreCliente, complexAndroidString=$complexAndroidString, contrato=$contrato, trn=$trn, conceptoPago=$conceptoPago, conceptoGenerado=$conceptoGenerado, fecha=$fecha, cobrador=$cobrador, tServ=$tServ, balance=$balance, balanceAnterior=$balanceAnterior, authorization_code=$authorization_code, credit_card_number=$credit_card_number)"
    }


}