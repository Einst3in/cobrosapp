package com.telenord.cobrosapp.ui.clientes

import com.telenord.cobrosapp.models.Clientes

import java.util.ArrayList

/**
 * Created by aneudy on 10/28/2017.
 */


class Clientes_Contract {
    interface View {
        fun ShowList(clientes: ArrayList<Clientes>, count : Int)
        fun ShowLoading(t: Boolean?)
        fun ShowMsj(msj: String)
        fun showCount(c: Int,t:Int)
        fun AddItems(cliente: ArrayList<Clientes>,count : Int)
        fun isLastPage(isLast : Boolean)
        fun isLoading(c: Boolean)
    }


    interface Listener {
        fun Find(ref: String, page : Int)
    }

    interface Adapter {
        fun showTelefonos(telefonos: ArrayList<String>)
        fun showMap(lat: Double, lng: Double)
        fun goDetalle(cliente: Clientes)
        fun showCount(show: Boolean)

    }


}
