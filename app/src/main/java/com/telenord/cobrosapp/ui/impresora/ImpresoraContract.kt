package com.telenord.cobrosapp.ui.impresora


import com.telenord.cobrosapp.models.Impresora

class ImpresoraContract{
    interface View{
        fun showList(list:ArrayList<Impresora>)
        fun showDefaultPrinter(p:Impresora)
        fun showBluetoothPanel()
        fun showLoading(t: Boolean)
        fun showAlertDelete(printer: Impresora)
        fun setDefault(printer: Impresora)
        fun goConfigureActivity()
        fun showError(error:String,msj:String,retry:Int)
        fun showMsj(i:String)

    }
    interface Listener{
        fun getList()
        fun getDefaultPrinter()
        fun configurePrinter()
        fun downloadTemplate()
        fun connectPrinterBluetooth()
         fun deletePrinter(printer: Impresora)
        fun setDefault(printer: Impresora)

    }
}