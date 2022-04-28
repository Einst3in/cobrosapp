package com.telenord.cobrosapp.ui.impresora.PariedDevices

import com.telenord.cobrosapp.models.Impresora

class PairedDevicesContract {
    interface View{
        fun showProgress(i:Boolean)
        fun showList(p:ArrayList<Impresora>)
    }
    interface Listener{
        fun getList()
        fun goSelected()
    }
}