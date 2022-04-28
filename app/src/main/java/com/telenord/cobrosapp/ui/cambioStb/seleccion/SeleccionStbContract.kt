package com.telenord.cobrosapp.ui.cambioStb.seleccion

import com.telenord.cobrosapp.models.STB

class SeleccionStbContract{
interface View{

  fun showLoading(t:Boolean)
  fun showStb(list: List<STB>)
}
interface Listener{

    fun getSTB(contrato:String)

}
}