package com.telenord.cobrosapp.ui.averias

import com.telenord.cobrosapp.models.Averia
import com.telenord.cobrosapp.models.Concepto
import com.telenord.cobrosapp.models.Contacto

class AveriaContract {

    interface View{
        fun fillSpinner(conceptos : ArrayList<Concepto>)
        fun showProgress(t : Boolean)
        fun showContacto(contacto: Contacto)
        fun finish(t:Boolean)

    }
    interface Listener{
        fun getConceptos()
        fun getContacto()
        fun postAveria(averia: Averia,contacto: Contacto)
    }
}