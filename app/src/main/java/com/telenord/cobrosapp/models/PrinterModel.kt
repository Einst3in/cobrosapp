package com.telenord.cobrosapp.models

class PrinterModel

{

    var id : Int? = 0
    var model : String? = null
    var template: String? = null

    constructor(model: String?, template: String?) {
        this.model = model
        this.template = template
    }

    constructor(id: Int?, model: String?, template: String?) {
        this.id = id
        this.model = model
        this.template = template
    }


    override fun toString(): String {
        return model!!
    }

}