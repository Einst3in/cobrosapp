package com.telenord.cobrosapp.print.printUtils.templates

import com.telenord.cobrosapp.print.printUtils.PrinterCommands
import java.io.OutputStream

class MTP2 (private val outputStream: OutputStream){

    fun imprimir (string: String){
        val stringa= saltar(string)
        outputStream!!.write(byteArrayOf(0x1B, 0x21, 0x08))
        outputStream.write(PrinterCommands.ESC_ALIGN_CENTER)
        outputStream.write(stringa.toByteArray())
    }

    fun saltar(string:String):String{
//        return string.replace("\n",System.getProperty("line.separator"))
        return string.replace("\n",System.lineSeparator())
    }
}