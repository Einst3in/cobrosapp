package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlanInternet:Serializable {

    @SerializedName("codigo")
    @Expose
    var codigo: Int? = null

    @SerializedName("descripcion")
    @Expose
    var descripcion: String? = null

    @SerializedName("mensualidad")
    @Expose
    var mensualidad: Int? = null

    @SerializedName("instalacion")
    @Expose
    var instalacion: Double? = null

    @SerializedName("fecha")
    @Expose
    var fecha: String? = null

    @SerializedName("nueva_mensualidad")
    @Expose
    var nuevaMensualidad: Double? = null

    @SerializedName("concepto")
    @Expose
    var concepto: String? = null

    @SerializedName("existencia")
    @Expose
    var existencia: Int = 0

    constructor()

    fun stringFull(): String {
        return "PlanInternet(codigo=$codigo, descripcion=$descripcion, mensualidad=$mensualidad, instalacion=$instalacion)"
    }

    override fun toString(): String {
        return "$descripcion"
    }



}