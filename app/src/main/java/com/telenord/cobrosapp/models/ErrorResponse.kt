package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ErrorResponse : Serializable{
    @SerializedName("estado")
    @Expose
    var estado: Int = 0
    @SerializedName("error")
    @Expose
    var error: String? = null
    @SerializedName("mensaje")
    @Expose
    var mensaje: String? = null

    override fun toString(): String {
        return "ErrorResponse(estado=$estado, error=$error, mensaje=$mensaje)"
    }


}