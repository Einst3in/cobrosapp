package com.telenord.cobrosapp.ui.cambioStb.cambio

import com.telenord.cobrosapp.models.Horario
import com.telenord.cobrosapp.models.PrecioSTB

class CambioStbContract {

    interface Listener{
        fun getPrecioStb(contrato:String)
    }
    interface View{
        fun showLoading(t:Boolean)
        fun showPrecioStb(list: List<PrecioSTB>)
    }
}