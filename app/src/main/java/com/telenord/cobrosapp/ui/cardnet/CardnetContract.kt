package com.telenord.cobrosapp.ui.cardnet

import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.Pago

class CardnetContract {

    interface View{
        fun showError(i: String, pago: Pago)
        fun showLoading(l: Boolean)
        fun goHome(bundle: Bundle)
        fun showErrorCardnet(i: String, pago: Pago)
    }

    interface Listener
    {
        fun success(bundle: Bundle)
//        fun retry(code:Int,mensaje :String, contrato: String, bundle: Bundle)
        fun retry(i: String, pago: Pago)
        fun reintentarPago(contrato: String, pago: Pago)
    }
}