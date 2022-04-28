package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class STB(
        @SerializedName("serie") @Expose var serie:String,
        @SerializedName("tarjeta") @Expose var tarjeta:String,
        @SerializedName("nueva") @Expose var nueva:Int,
        @SerializedName("tipo") @Expose var tipo:String,
        @SerializedName("fecha") @Expose var fecha:String,
        @SerializedName("cambio_pendiente") @Expose var cambio_pendiente: Int,
        @SerializedName("marcada") @Expose var marcada: Boolean = false,
        @SerializedName("codigo_cambio") @Expose var codigo_cambio:Int,
        @SerializedName("descripcion_cambio") @Expose var desc_cambio:String,
        @SerializedName("tipo_cambio") @Expose var tipo_cambio: String,
        var precioCambio:Double = 0.0



):Serializable {
    override fun toString(): String {
        return "STB(serie='$serie', tarjeta='$tarjeta', tipo='$tipo', marcada=$marcada)"
    }


}

