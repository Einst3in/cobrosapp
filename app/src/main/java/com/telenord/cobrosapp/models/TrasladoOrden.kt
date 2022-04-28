package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TrasladoOrden: Serializable {

    @SerializedName("ciudad")
    @Expose
    var ciudad: Int? = null

    @SerializedName("sector")
    @Expose
    var sector: Int? = null

    @SerializedName("calle")
    @Expose
    var calle : Int? = null

    @SerializedName("edificio")
    @Expose
    var edificio: String? = "(NULL)"

    @SerializedName("esquina")
    @Expose
    var esquina: String? = "(NULL)"

    @SerializedName("zona")
    @Expose
    var zona: Int? = null

    @SerializedName("casa")
    @Expose
    var numero_casa : String? = null

    @SerializedName("apartamento")
    @Expose
    var numero_apartamento: String? = null

    @SerializedName("referencia")
    @Expose
    var referencia : String? = null


    constructor(ciudad: Int?, sector: Int?, calle: Int?, edificio: String?, esquina: String?, zona: Int?, numero_casa: String?, numero_apartamento: String?, referencia: String?) {
        this.ciudad = ciudad
        this.sector = sector
        this.calle = calle
        this.edificio = edificio
        this.esquina = esquina
        this.zona = zona
        this.numero_casa = numero_casa
        this.numero_apartamento = numero_apartamento
        this.referencia = referencia
    }

    constructor()

    override fun toString(): String {
        return "TrasladoOrden(ciudad=$ciudad, sector=$sector, calle=$calle, edificio=$edificio, esquina=$esquina, zona=$zona, numero_casa=$numero_casa, numero_apartamento=$numero_apartamento, referencia=$referencia)"
    }

}
