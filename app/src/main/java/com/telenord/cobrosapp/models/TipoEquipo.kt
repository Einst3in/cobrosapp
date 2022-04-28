package com.telenord.cobrosapp.models
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TipoEquipo():Serializable{
        @SerializedName("codigo")
        @Expose
        var codigo:String? = null

        @SerializedName("descripcion")
        @Expose
        var descripcion:String? = null

        @SerializedName("precio")
        @Expose
        var precio:Double? = null

        @SerializedName("tipo")
        @Expose
        var tipo: String? = null



    override fun toString(): String {
        return "$descripcion"
    }


}

