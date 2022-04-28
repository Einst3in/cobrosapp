package com.telenord.cobrosapp.ui.asignarInternet

import com.telenord.cobrosapp.models.PlanInternet
import com.telenord.cobrosapp.models.TipoEquipo

class InternetContract{

    interface View{
        fun showTipos(list: ArrayList<TipoEquipo>)
        fun showLoading(t: Boolean)
        fun showPlanes(list: ArrayList<PlanInternet>)
    }

    interface Listener{
        fun getTipos(contrato: String)
        fun getPlanes(contrato: String,tipoEquipo: String)

    }



}