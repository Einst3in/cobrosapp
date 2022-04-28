package com.telenord.cobrosapp.models

class TransCheque{

    var numero_cheque : String? = null

    var banco_id : Int? = null

    var monto: Double? = null

    var cuenta_cheque: String? = null

    constructor(num_cheque: String?, banco_id: Int?, monto: Double?, cuenta_cheque: String?) {
        this.numero_cheque = num_cheque
        this.banco_id = banco_id
        this.monto = monto
        this.cuenta_cheque = cuenta_cheque
    }

    override fun toString(): String {
        return "${numero_cheque},$banco_id,$monto,$cuenta_cheque)"
    }


}