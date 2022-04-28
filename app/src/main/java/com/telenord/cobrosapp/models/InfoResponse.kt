package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class InfoResponse: Serializable {

    @SerializedName("estado")
    @Expose
    var estado: Int? = null
    @SerializedName("info")
    @Expose
    var info: Info? = null
    @SerializedName("mensaje")
    @Expose
    var mensaje: String? = null


}