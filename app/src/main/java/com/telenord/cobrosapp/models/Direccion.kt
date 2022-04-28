package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Direccion {

    @SerializedName("direccion")
    @Expose
    var direccion : String? = ""
}