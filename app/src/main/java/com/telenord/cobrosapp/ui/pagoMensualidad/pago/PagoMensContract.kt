package com.telenord.cobrosapp.ui.pagoMensualidad.pago

import android.os.Bundle
import com.telenord.cobrosapp.models.Banco

class PagoMensContract {

    interface Listener {
        fun getDatos()
        fun getBancos()
        fun getTipoPago()
    }

    interface View{
        fun showBancos(list : ArrayList<Banco>)
        fun showTipoPago(list : ArrayList<String>)
        fun showLoading(t: Boolean)
    }
}