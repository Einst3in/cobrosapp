package com.telenord.cobrosapp.ui.historial

import com.telenord.cobrosapp.models.MovimientosResponse

class HistorialContract
{
    interface View{
        fun showMovimientos(movimientos: MovimientosResponse)
        fun showLoading(t: Boolean)
    }
    interface Listener{
        fun getMovimientos(contrato: String)
    }
}