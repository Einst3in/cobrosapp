package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Calles : Serializable{

    @SerializedName("codigo")
    @Expose
    var codigo: Int? = null

    @SerializedName("nombre")
    @Expose
    var nombre: String? = null

    @SerializedName("zona")
    @Expose
    var zona : Int? = null

    override fun toString(): String {
        return "$nombre"
    }
}