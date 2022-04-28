package com.telenord.cobrosapp.ui.stbOps

import com.telenord.cobrosapp.models.STB

class StbOpsContract {

    interface View{
        fun showStbs(list: List<STB>)
        fun showLoading(t: Boolean)
    }

    interface Listener{
    fun getStbs(contrato: String)
    }

}