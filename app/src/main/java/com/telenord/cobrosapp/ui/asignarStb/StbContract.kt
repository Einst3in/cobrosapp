package com.telenord.cobrosapp.ui.asignarStb

import com.telenord.cobrosapp.models.Precio
import com.telenord.cobrosapp.models.STB
import com.telenord.cobrosapp.models.TipoEquipo

class StbContract {

interface Listener{
    fun getTipos(contrato: String)
    fun getCantidad(tipo: String)
    fun getPrecios(contrato: String,tipo: String,cant:Int)
    fun getPrecioExt(contrato: String)
    fun getStbContrato(contrato: String)

}
    interface View{
        fun showTipos(list:ArrayList<TipoEquipo>)
        fun showLoading(t: Boolean)
        fun showCantidad(c: Int,tipo: String)
        fun showPrecios(precio: Precio)
        fun showPrecioExt(precio: Precio)
        fun showStbContrato(stb: ArrayList<STB>)

    }


}