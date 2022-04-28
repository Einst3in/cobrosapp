package com.telenord.cobrosapp.ui.traslado

import com.telenord.cobrosapp.models.*

class TrasladoContract {

    interface Listener{
        fun getDireccionActual(contrato: String)
        fun getCiudades()
        fun getSectores(ciudad: Int)
        fun getCalles(sector: Int)
        fun getEdificio()
        fun getEsquina(ciudad: Int)
        fun getHorario()
        fun getPrecioTraslado(contrato: String,tipo: Pago.Tipo_OP)
    }
    interface View{
        fun showDireccionActual(direccion: String)
        fun showCiudades(list: ArrayList<Ciudad>)
        fun showLoading(t: Boolean)
        fun showSectores(list: ArrayList<Sector>)
        fun showCalles(list: ArrayList<Calles>)
        fun showEdificios(list : ArrayList<Edificio>)
        fun showEsquina(list: ArrayList<Calles>)
        fun showHorario(list: ArrayList<Horario>)
        fun showPrecio(precio: Precio)

    }
}