package com.telenord.cobrosapp.Response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.telenord.cobrosapp.models.Clientes

import java.util.ArrayList

/**
 * Created by aneudy on 23/1/2017.
 */

class ClientesResponse {
    @SerializedName("clientes")
    @Expose
    var clientes : ArrayList<Clientes>? = null

    @SerializedName("count")
    @Expose
    var count : Int? = null

    constructor(clientes: ArrayList<Clientes>?,count: Int?){
        this.clientes = clientes
        this.count = count
    }
}
