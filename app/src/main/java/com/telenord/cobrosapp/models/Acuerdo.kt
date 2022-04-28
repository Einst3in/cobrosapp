package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Acuerdo : Serializable{

    @SerializedName("acuerdo")
    @Expose
    var acuerdo:String? =""

    @SerializedName("cuotas_vencidas")
    @Expose
    var cuotas_vencidas: Int? = 0
}