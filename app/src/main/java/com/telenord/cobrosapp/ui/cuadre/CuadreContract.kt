package com.telenord.cobrosapp.ui.cuadre

import com.telenord.cobrosapp.models.Cuadre

class CuadreContract {

    interface View{
    fun showLoading(t: Boolean)
    fun showCuadre(cuadre: Cuadre)
    }
    interface Listener{
    fun getCuadre(fecha: String)
        fun hayDefaultPrinter(): Boolean
    }
}