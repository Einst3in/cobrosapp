package com.telenord.cobrosapp.ui.dialogs.visitaRuta

import com.telenord.cobrosapp.models.Concepto

class VisitaRutaContract {

    interface View{

        fun showConceptos(conceptos: ArrayList<Concepto>)
        fun showLoading(t: Boolean)
        fun visitaPosteada()


    }
    interface Listener {
        fun getConceptos()
        fun postVisita(contrato: String,concepto: Int)
    }
}