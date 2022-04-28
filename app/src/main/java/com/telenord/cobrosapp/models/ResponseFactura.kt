package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseFactura : Serializable{

    @SerializedName("recibo")
    @Expose
    var recibo: ReciboFact? = null

    @SerializedName("documento")
    @Expose
    var documento: DocumentoFact? = null

    @SerializedName("boleto")
    @Expose
    var boleto: Boleto? = null

    var reimpresion: Int? = 0

    constructor(recibo: ReciboFact?,documento: DocumentoFact?){
        this.documento = documento
        this.recibo = recibo
    }


    enum class Tipo_Pago : Serializable{
        ef,ta,ck,card;
    }

    override fun toString(): String {
        return "ResponseFactura(recibo=$recibo, documento=$documento)"
    }


}

