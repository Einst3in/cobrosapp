package com.telenord.cobrosapp.ui.pagoMensualidad

import android.os.Bundle
import com.telenord.cobrosapp.models.*

class PagoContract {

    interface View{

        fun getFactura(factura: ResponseFactura)
        fun showError(i : String)
        fun showLoading(l: Boolean)
        fun showAcuerdo(acuerdo: Acuerdo)
    }

    interface Listener
    {
        fun Cobrar(cliente: Clientes,pago: Pago)
        fun getAcuerdo(contrato:String)
        fun CobrarCardnet(factura: ResponseFactura)


    }
}