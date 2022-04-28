package com.telenord.cobrosapp.ui.operaciones


import com.telenord.cobrosapp.models.PagoDetalle
import com.telenord.cobrosapp.models.Precio

class OperacionesContract {

    interface View {
        fun showLoading(t: Boolean?)
        fun showMsj(msj: String)
        fun showBalance(cliente: PagoDetalle?)
        fun showMensualidad(items: List<Precio>)
    }

    interface Listener {
        fun getBalances(Contrato: String)
        fun getDetalleMens(Contrato: String)
    }


}
