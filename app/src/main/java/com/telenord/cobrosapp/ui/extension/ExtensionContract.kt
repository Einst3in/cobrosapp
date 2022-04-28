package com.telenord.cobrosapp.ui.extension

import com.telenord.cobrosapp.models.Horario
import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.Precio

class ExtensionContract {
    interface Listener{
        fun getPrecio(tipo_OP: Pago.Tipo_OP,contrato: String)
        fun getHorarios()
    }
    interface View{
        fun showPrecio(precio: Precio)
        fun showHorarios(lista: List<Horario>)
        fun showLoading(t:Boolean)
    }
}