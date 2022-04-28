package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Cantidad {
    @SerializedName("cantidad")
    @Expose
    var cantidad:Int? = null
}