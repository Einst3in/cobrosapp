package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Horario{
    @SerializedName("descripcion")
    @Expose
    var descripcion: String? = null

    constructor(descripcion: String?) {
        this.descripcion = descripcion
    }

    override fun toString(): String {
        return "$descripcion"
    }

}