package com.telenord.cobrosapp.ui.operaciones.tabs.operacionesTab

import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.Precio

class OpFragmentContract {

    interface View{
        fun  showAlert(precio: Precio)
        fun showLoading(t:Boolean)
    }

    interface Listener{
        fun getPrecio(tipo_OP: Pago.Tipo_OP,contrato: String)
        fun postM15(contrato: String)
    }
}