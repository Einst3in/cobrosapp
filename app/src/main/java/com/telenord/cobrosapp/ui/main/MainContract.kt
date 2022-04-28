package com.telenord.cobrosapp.ui.main

import com.telenord.cobrosapp.models.Dispositivo

/**
 * Created by Aneudy on 10/5/2017.
 */

class MainContract {
    interface View {

            fun sendDispositivo(dispositivo: Dispositivo)
    }

    interface Listener{
        fun sendDispositivo(dispositivo: Dispositivo)
    }
}
