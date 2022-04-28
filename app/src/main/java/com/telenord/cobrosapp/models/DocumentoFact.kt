package com.telenord.cobrosapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DocumentoFact : Serializable {
    @SerializedName("company_name")
    @Expose
    var companyName: String? = null
    @SerializedName("company_address")
    @Expose
    var companyAddress: String? = null
    @SerializedName("company_city")
    @Expose
    var companyCity: String? = null
    @SerializedName("company_contact")
    @Expose
    var companyContact: String? = null
    @SerializedName("document")
    @Expose
    var document: String? = null
    @SerializedName("separador")
    @Expose
    var separador: String? = null
    @SerializedName("footer")
    @Expose
    var footer: String? = null
    @SerializedName("nota")
    @Expose
    var nota: String? = null
    @SerializedName("rnc")
    @Expose
    var rnc: String? = null

    @SerializedName("caracteres_linea")
    @Expose
    var caracteresLinea : Int? = null

    @SerializedName("tipo_pago")
    @Expose
    var tipo_pago: String? = null

    override fun toString(): String {
        return "DocumentoFact(companyName=$companyName, companyAddress=$companyAddress, companyCity=$companyCity, companyContact=$companyContact, document=$document, separador=$separador, footer=$footer, nota=$nota, rnc=$rnc, caracteresLinea=$caracteresLinea, tipo_pago=$tipo_pago)"
    }


}