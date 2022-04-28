package com.telenord.cobrosapp.ui.pagoMensualidad.detalle

import com.telenord.cobrosapp.models.DetalleFacturacion

class DetalleMensContract{
    interface Listener{
        fun getDetallesFacturacion(contrato: String)
    }
    interface View{
        fun showDetalles(list: ArrayList<DetalleFacturacion>)
        fun showLoading(show: Boolean)

    }
}