package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Ciudad : Serializable{

    @SerializedName("codigo")
    @Expose
    var codigo: Int? = null

    @SerializedName("nombre")
    @Expose
    var nombre: String? = null

    override fun toString(): String {
        return "$nombre"
    }
}