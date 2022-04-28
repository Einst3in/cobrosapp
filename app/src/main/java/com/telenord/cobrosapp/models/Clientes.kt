package com.telenord.cobrosapp.models
/**
 * Created by aneudy on 23/1/2017.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Clientes : Serializable {
    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("contrato")
    @Expose
    var contrato: String? = null
    @SerializedName("cedula")
    @Expose
    var cedula: String? = null
    @SerializedName("nombre_completo")
    @Expose
    var nombre: String? = null
    @SerializedName("direccion")
    @Expose
    var direccion: String? = null
    @SerializedName("codigo_estatus")
    @Expose
    var status: Int? = null
    @SerializedName("referencia")
    @Expose

    var referencia: String? = null
    @SerializedName("telefono")
    @Expose
    var telefonos: String? = null

    @SerializedName("celular")
    @Expose
    var celular: String? = null

    @SerializedName("geo")
    @Expose
    var geo: String? = null


    @SerializedName("Balance")
    @Expose
    var balance: Double = 0.toDouble()
    @SerializedName("BalanceCaja")
    @Expose
    var balanceCaja: Double = 0.toDouble()
    @SerializedName("AC")
    @Expose
    var ac: String? = null
    @SerializedName("Mensualidad")
    @Expose
    var mensualidad: Double = 0.toDouble()

    @SerializedName("ciudad")
    @Expose
    var ciudad: String? = null

    @SerializedName("sector")
    @Expose
    var sector: String? = null

    @SerializedName("estatus")
    @Expose
    var statusDescripcion: String? = null

    enum class tipoStatus{
        CONECTADO,PENDIENTE,DESCONECTADO,PENDIENTE_DESCONECTAR
    }

    fun  getTipoStatus(): tipoStatus?{
        when(status!!){
            1,7 -> return tipoStatus.PENDIENTE
            3,10 -> return tipoStatus.PENDIENTE_DESCONECTAR
            4,5,11,12,13 -> return tipoStatus.DESCONECTADO
            2,9,16,20,21 -> return tipoStatus.CONECTADO
        }
        return null
    }
}