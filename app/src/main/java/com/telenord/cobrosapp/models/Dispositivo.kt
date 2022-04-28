package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Dispositivo {
    @SerializedName("id")
    @Expose
    var id : Int? = null

    @SerializedName("imei")
    @Expose
    var imei : String? = null

    @SerializedName("user")
    @Expose
    var user: String? = null

    constructor(imei: String?, user: String?) {
        this.imei = imei
        this.user = user
    }
}