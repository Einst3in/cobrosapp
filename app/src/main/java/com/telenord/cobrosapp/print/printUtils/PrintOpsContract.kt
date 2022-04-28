package com.telenord.cobrosapp.print.printUtils

class PrintOpsContract {
    interface Ops{
        fun imprimirLinea()
        fun imprimirTexto(msg: String)
        fun imprimirTexto(msg: ByteArray)
        fun imprimirFoto(img: Int)
        fun imprimirUnicode()
        fun imprimirCustom(msg: String, size: Int, align: Int)
        fun leftRightAlign(str1 : String, str2: String): String
        fun isConected(): Boolean
        fun imprimirConceptos(str1: String,str2: String,n: Int)
        fun imprimirConceptos(str1: String,n: Int)
        fun hasPrinter(): Boolean
    }
}