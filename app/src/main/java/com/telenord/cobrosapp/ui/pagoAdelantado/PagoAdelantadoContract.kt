package com.telenord.cobrosapp.ui.pagoAdelantado

import com.telenord.cobrosapp.models.PagoAdelantado

class PagoAdelantadoContract {

    interface View{
        fun showValor(pago : PagoAdelantado)
        fun showLoading(t: Boolean)
    }

    interface Listener{
        fun getValor(contrato: String,meses: Int)
    }
}