package com.telenord.cobrosapp.ui.impresora.configPrinter

import com.telenord.cobrosapp.models.ImpresoraModelo

class ConfPrinterContract {

    interface View{
        fun showModels(models: ArrayList<ImpresoraModelo>)
        fun showAlert()
        fun showLoading(t: Boolean)
    }

    interface Listener{
        fun getModels()
        fun addPrinter(mac: String?,modelo: Int?)
        fun setDefault(mac: String?)
    }
}