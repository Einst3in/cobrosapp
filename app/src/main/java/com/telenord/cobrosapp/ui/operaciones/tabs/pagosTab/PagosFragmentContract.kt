package com.telenord.cobrosapp.ui.operaciones.tabs.pagosTab

import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.ResponseFactura

class PagosFragmentContract{

    interface Listener{
        fun getReimprimir(contrato: Clientes)
        fun procesarPagoPendiente(cliente: Clientes, pago: Pago)
        fun deletePagoPendiente(contrato: String)
    }
    interface View{
        fun Reimprmir(factura: ResponseFactura)
        fun showError(e: String)
        fun showLoading(t: Boolean)
        fun reimpresionCompleta()
        fun getFactura(factura: ResponseFactura)
        fun endalert()
    }
}