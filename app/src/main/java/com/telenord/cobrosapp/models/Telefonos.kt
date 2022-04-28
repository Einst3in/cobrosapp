package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Telefonos (
        @SerializedName("telefono")
        @Expose
        var telefono: String,

        @SerializedName("celular")
        @Expose
        var celular : String,

        @SerializedName("otroTelefono")
        @Expose
        var otroTelefono : String,

        @SerializedName("email")
        @Expose
        var email: String



   ) {
    override fun toString(): String {
        return "Telefonos(telefono='$telefono', celular='$celular', otroTelefono='$otroTelefono', email='$email')"
    }
}

