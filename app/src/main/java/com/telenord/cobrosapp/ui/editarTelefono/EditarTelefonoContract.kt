package com.telenord.cobrosapp.ui.editarTelefono

import com.telenord.cobrosapp.models.Telefonos

class EditarTelefonoContract {

    interface View {

        fun showLoading(t: Boolean)
        fun showTelefono(telefono:Telefonos?)
        fun telefonoGuardado()
    }

    interface Listener {

        fun getTelefonos(contrato: String)
        fun postTelefono(contrato: String,telefono:String,celular:String,otroTelefono: String,email: String)
    }

}