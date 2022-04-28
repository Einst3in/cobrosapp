package com.telenord.cobrosapp.ui.ruta

import com.telenord.cobrosapp.models.Concepto
import com.telenord.cobrosapp.models.Ruta
import java.util.ArrayList

class RutaContract {

    interface View {
        fun showConceptos(c: ArrayList<Concepto>)

        fun ShowLoading(t: Boolean?)
        fun showRuta(p0 : List<Ruta>?,p1 : List<String>?)
        fun isLastPage(isLast : Boolean)
        fun isLoading(c: Boolean)
        fun showCount(c: Int,t:Int)

        /*fun ShowList(clientes: ArrayList<Clientes>, count : Int)

        fun ShowMsj(msj: String)
        fun showCount(c: Int,t:Int)
        fun AddItems(cliente: ArrayList<Clientes>, count : Int)
        fun isLastPage(isLast : Boolean)
        fun isLoading(c: Boolean)*/
    }


    interface Listener {
        fun getConceptos()
        fun getRuta(tipo: Int,pagina: Int)

       /* fun Find(ref: String, page : Int)*/
    }

    interface Adapter {
        fun showTelefonos(telefonos: ArrayList<String>)
        fun showMap(geo: String?)
        fun goDetalle(cliente: Ruta)
        fun showCount(show: Boolean)
        fun showConceptosVisitas(ruta: Ruta)


    }
}