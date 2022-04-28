package com.telenord.cobrosapp.print.printUtils

import android.app.ProgressDialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
//import com.crashlytics.android.Crashlytics
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.telenord.cobrosapp.models.Cuadre
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.models.ResponseFactura
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.print.BTOperator
import com.telenord.cobrosapp.print.BluetoothHandler
import com.telenord.cobrosapp.print.BluetoothService
import com.telenord.cobrosapp.ui.impresora.PariedDevices.PairedDevices
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import com.telenord.cobrosapp.util.ErrorUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.*

class PrinterOps(private val context: Context): PrintOpsContract.Ops {

    companion object {
        var socket: BluetoothSocket? = null
        var outputStream : OutputStream? = null
        var mService : BluetoothService? = null
        val mBto = BTOperator()
    }

    var mPConectando: ProgressDialog? = null
    var mPConectado: ProgressDialog? = null

    inner class Conectar(val ops: OpsInterface?) :  BluetoothHandler.HandlerInterface{

        private fun detectarDispositivo(): BluetoothDevice? {

            mBto.BTO_EnableBluetooth()

            val strList = mBto.BTO_GetBondedDevice()

            val iterator = strList.iterator()

            var devicetmp : BluetoothDevice? = null
            val impresora = SharedPreferenceManager.getInstance(context).impresora
            while (iterator.hasNext()) {
                val device = iterator.next() as BluetoothDevice
                if (device.address == impresora.mac) devicetmp = device }
            if (devicetmp!=null){
                return devicetmp
            }else return null
        }

        fun connect(){
            val device = detectarDispositivo()
            if (device!=null){
                context.toast("Conectando a " + device.name + "," + device.address)
                mService = BluetoothService(context, BluetoothHandler(this))
                mService!!.connect(device)
            }
            else{
                context.alert("Hay un problema con la impresora, favor revisar o comunicarse con Soporte Tecnico", "ATENCION!")
            }

        }

        override fun onDeviceConnected() {
            if (mPConectando!=null && mPConectando!!.isShowing){
                mPConectando!!.dismiss()
                mPConectando = null
            }

            Log.e("socket connected", "${mService!!.state}")

            mPConectado = ProgressDialog(context)
            mPConectado!!.setMessage("Imprimiendo...")
            mPConectado!!.setCancelable(false)
            mPConectado!!.isIndeterminate
            mPConectado!!.show()
            ops!!.outStream(mService!!.socket()!!.outputStream!!)
        }

        override fun onDeviceConnecting() {
            mPConectando = ProgressDialog(context)
            mPConectando!!.setMessage("Conectando...")
            mPConectando!!.setCancelable(false)
            mPConectando!!.isIndeterminate
            mPConectando!!.show()
        }

        override fun onDeviceConnectionLost() {
            context.toast("Conexion cerrada")
            //Log.e("socket Lost", "${mService!!.socket}")
            Log.e("socket Lost", "${mService!!.state}")
            Log.e("El maldito Socket", "${mService!!.socket()}")
        }

        override fun onDeviceUnableToConnect() {
            if (mPConectando!=null && mPConectando!!.isShowing){
                mPConectando!!.dismiss()
                mPConectando = null
            }

            context.toast("No se puede conectar a la impresora")
            // Log.e("socket Unable", "${mService!!.socket}")
            Log.e("socket Unable", "${mService!!.state}")

        }

        fun terminar(){
            mService!!.stop()
        }

    }

    interface OpsInterface{
        fun outStream(mOutStream: OutputStream)
    }

    inner class ImprimirPrueba:OpsInterface{
        init {
            Conectar(this).connect()
        }

        override fun outStream(mOutStream: OutputStream) {
            val msg = "Impresion de Prueba"
            mOutStream.write(PrinterCommands.ESC_ALIGN_CENTER)
            mOutStream.write(msg.toByteArray())
            mOutStream.write(PrinterCommands.ESC_ENTER)
            Thread.sleep(1000)
            if (mPConectado!=null && mPConectado!!.isShowing){
                mPConectado!!.dismiss()
                mPConectado = null
            }
            Conectar(this).terminar()
        }

    }

    inner class ImprimirFactura(val factura: ResponseFactura): OpsInterface{
        init {
            Conectar(this).connect()
        }
        override fun outStream(mOutStream: OutputStream) {

            outputStream = mOutStream

            val printformat = byteArrayOf(0x1B, 0x21, 0x03)
            outputStream!!.write(printformat)

            outputStream!!.write(0x1b);
            outputStream!!.write(0x74);
            outputStream!!.write(0x12);


            val concepto = factura!!.recibo!!.conceptoPago!!.split(";")
            val n =  factura.documento!!.caracteresLinea!!
            outputStream!!.write(byteArrayOf(0x1B, 0x40))//inicializar mtp56
            imprimirLinea()
            imprimirLinea()
            imprimirCustom(factura.documento!!.companyName!!, 1, 1)
            imprimirCustom(factura.documento!!.companyAddress!!, 1, 1)
            imprimirCustom(factura.documento!!.companyCity!!, 1, 1)
            imprimirCustom(factura.documento!!.companyContact!!, 1, 1)
            imprimirConceptos("RNC:${factura.documento!!.rnc!!}", n)
            imprimirCustom(factura.documento!!.document!!.toUpperCase(), 1, 1)
            if (factura.reimpresion == 1){

                imprimirCustom("REIMPRESION", 2, 1)
                imprimirLinea()

            }
            imprimirConceptos("Numero: ${factura.recibo!!.trn}", n)
            imprimirConceptos("Fecha: ${factura.recibo!!.fecha}", n)
            imprimirConceptos("${factura.documento!!.tipo_pago}: ${factura.recibo!!.tipo_pago}", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirConceptos("Contrato: ${factura.recibo?.contrato}", n)
            imprimirConceptos(factura.recibo!!.nombreCliente!!, n)
            imprimirConceptos("Balance Anterior: ${factura.recibo!!.balanceAnterior!!.toDouble()}", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirConceptos("Concepto", "Monto", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            for (a in 0.until(concepto.size / 2)){
                val b = a*2
                imprimirConceptos(concepto[b].trim(), concepto[b + 1].trim(), n)
            }
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirConceptos("Total Pagado", factura.recibo!!.monto!!.toDouble().toString(), n)
            imprimirConceptos("Nuevo Balance", factura.recibo!!.balance!!.toDouble().toString(), n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirCustom(factura.documento!!.nota!!, 1, 1)
            imprimirLinea()
            imprimirLinea()
            imprimirLinea()
            imprimirCustom(String(CharArray((n * 0.6).toInt())).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirCustom(factura.recibo!!.cobrador!!.toUpperCase(), 1, 1)
            imprimirFooter(factura.documento!!.footer!!, n)
            imprimirCustom("                        ", 1, 1)
            imprimirLinea()
            if (factura.boleto!!.ticket != null){
                imprimirFooter(factura.boleto!!.descripcion!!, n)
                imprimirCustom(factura.boleto!!.ticket!!, 1, 1)
                imprimirCustom("                        ", 1, 1)
                imprimirLinea()
            }
            outputStream!!.write(byteArrayOf(27, 74, 127))//feed paper
            outputStream!!.flush()

            Thread.sleep(1000)

            if (mPConectado!=null && mPConectado!!.isShowing){
                mPConectado!!.dismiss()
                mPConectado = null
            }
            Conectar(this).terminar()
        }
    }

    inner class ImprimirFacturaVoucher(val factura: ResponseFactura): OpsInterface{
        init {
            Conectar(this).connect()
        }
        override fun outStream(mOutStream: OutputStream) {

            outputStream = mOutStream

            val printformat = byteArrayOf(0x1B, 0x21, 0x03)
            outputStream!!.write(printformat)

            outputStream!!.write(0x1b);
            outputStream!!.write(0x74);
            outputStream!!.write(0x12);


            val concepto = factura!!.recibo!!.conceptoPago!!.split(";")
            val n =  factura.documento!!.caracteresLinea!!
            outputStream!!.write(byteArrayOf(0x1B, 0x40))//inicializar mtp56

            // V O U C H E R
            imprimirCustom(factura.documento!!.companyName!!, 1, 1)
            imprimirCustom(factura.documento!!.companyAddress!!, 1, 1)
            imprimirCustom(factura.documento!!.companyCity!!, 1, 1)
            imprimirLinea()
            imprimirCustom(" Compronbante de Pago de Tarjeta ", 1, 1)
            imprimirLinea()
            imprimirConceptos("Fecha: ${factura.recibo!!.fecha}", n)
            imprimirLinea()
            imprimirConceptos("Aprobacion", factura.recibo!!.authorization_code!!, n)
            imprimirConceptos("Tarjeta", factura.recibo!!.credit_card_number!!, n)
            imprimirLinea()
            imprimirConceptos("Total", factura.recibo!!.monto!!.toDouble().toString(), n)
            imprimirLinea()
            imprimirLinea()
            imprimirCustom("ACEPTO PAGAR ESTE MONTO SUJETO A LOS",0,1)
            imprimirCustom("TERMINOS DEL CONTRATO CELEBRADO CON",0,1)
            imprimirCustom("EL EMISOR DE LA TARJETA",0,1)
            imprimirLinea()
            imprimirLinea()
            imprimirLinea()
            imprimirCustom("--------------------------------",1,1)




            // R E C I B O
            imprimirLinea()
            imprimirLinea()
            imprimirCustom(factura.documento!!.companyName!!, 1, 1)
            imprimirCustom(factura.documento!!.companyAddress!!, 1, 1)
            imprimirCustom(factura.documento!!.companyCity!!, 1, 1)
            imprimirCustom(factura.documento!!.companyContact!!, 1, 1)
            imprimirConceptos("RNC:${factura.documento!!.rnc!!}", n)
            imprimirCustom(factura.documento!!.document!!.toUpperCase(), 1, 1)
            if (factura.reimpresion == 1){

                imprimirCustom("REIMPRESION", 2, 1)
                imprimirLinea()

            }
            imprimirConceptos("Numero: ${factura.recibo!!.trn}", n)
            imprimirConceptos("Fecha: ${factura.recibo!!.fecha}", n)
            imprimirConceptos("${factura.documento!!.tipo_pago}: ${factura.recibo!!.tipo_pago}", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirConceptos("Contrato: ${factura.recibo?.contrato}", n)
            imprimirConceptos(factura.recibo!!.nombreCliente!!, n)
            imprimirConceptos("Balance Anterior: ${factura.recibo!!.balanceAnterior!!.toDouble()}", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirConceptos("Concepto", "Monto", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            for (a in 0.until(concepto.size / 2)){
                val b = a*2
                imprimirConceptos(concepto[b].trim(), concepto[b + 1].trim(), n)
            }
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirConceptos("Total Pagado", factura.recibo!!.monto!!.toDouble().toString(), n)
            imprimirConceptos("Nuevo Balance", factura.recibo!!.balance!!.toDouble().toString(), n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirCustom(factura.documento!!.nota!!, 1, 1)
            imprimirLinea()
            imprimirLinea()
            imprimirLinea()
            imprimirCustom(String(CharArray((n * 0.6).toInt())).replace("\u0000", factura.documento!!.separador!!), 1, 1)
            imprimirCustom(factura.recibo!!.cobrador!!.toUpperCase(), 1, 1)
            imprimirFooter(factura.documento!!.footer!!, n)
            imprimirCustom("                        ", 1, 1)
            imprimirLinea()
            outputStream!!.write(byteArrayOf(27, 74, 127))//feed paper
            outputStream!!.flush()

            Thread.sleep(1000)

            if (mPConectado!=null && mPConectado!!.isShowing){
                mPConectado!!.dismiss()
                mPConectado = null
            }
            Conectar(this).terminar()
        }
    }

    inner class ImprimirCuadre(val cuadre: Cuadre): OpsInterface{
        init {
            Conectar(this).connect()
        }
        override fun outStream(mOutStream: OutputStream) {

            outputStream = mOutStream

            val printformat = byteArrayOf(0x1B, 0x21, 0x03)
            outputStream!!.write(printformat)


            val n =  cuadre.documento!!.caracteresLinea!!
            outputStream!!.write(byteArrayOf(0x1B, 0x40))//inicializar mtp56
            imprimirLinea()
            imprimirLinea()
            imprimirCustom(cuadre.documento!!.companyName!!, 1, 1)
            imprimirCustom(cuadre.documento!!.companyAddress!!, 1, 1)
            imprimirCustom(cuadre.documento!!.companyCity!!, 1, 1)
            imprimirCustom(cuadre.documento!!.companyContact!!, 1, 1)
            imprimirConceptos("RNC:${cuadre.documento!!.rnc!!}", n)
            imprimirCustom(cuadre.documento!!.document!!.toUpperCase(), 1, 1)
            imprimirCustom(String(CharArray(n)).replace("\u0000", cuadre.documento!!.separador!!), 1, 1)
            imprimirCustom(cuadre.total!!.nombre!!.toUpperCase(), 1, 1)
            imprimirConceptos("Cantidad de Cobros:${cuadre.total!!.cantidad}", n)
            imprimirConceptos("Fecha:${cuadre.total!!.fecha}", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", cuadre.documento!!.separador!!), 1, 1)
            imprimirConceptos("Contrato", "Monto", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", cuadre.documento!!.separador!!), 1, 1)
            for (a in cuadre.cuadre!!){
                imprimirConceptos(a.contrato!!.trim(), "${a.monto!!.toDouble()}".trim(), n)
            }
            imprimirCustom(String(CharArray(n)).replace("\u0000", cuadre.documento!!.separador!!), 1, 1)
            imprimirConceptos("Total Cobrado", "${cuadre.total!!.monto!!.toDouble()}", n)
            imprimirCustom(String(CharArray(n)).replace("\u0000", cuadre.documento!!.separador!!), 1, 1)
            imprimirCustom("                        ", 1, 1)
            imprimirLinea()
            outputStream!!.write(byteArrayOf(27, 74, 127))//feed paper
            outputStream!!.flush()

            Thread.sleep(1000)

            if (mPConectado!=null && mPConectado!!.isShowing){
                mPConectado!!.dismiss()
                mPConectado = null
            }
            Conectar(this).terminar()
        }
    }

    fun imprimirFooter(footer: String, n: Int){
        val l = n+2
        if(footer.length < l){
            imprimirCustom(footer, 1, 1)
        }else if (footer.length == l){imprimirCustom(footer, 1, 0)}
        else if (footer.length > l){ imprimirConceptos(footer, n)}

    }

    override fun imprimirConceptos(str1: String, n: Int) {

        if (str1.length <=n){
            val l = n - str1.length
            imprimirCustom(str1 + String(CharArray(l)).replace("\u0000", " "), 1, 1)
        }else{
            val strs = splitToNChar(str1, n)
            for (s in strs){
                imprimirConceptos(s, n)
            }
        }
    }

    override fun imprimirConceptos(str1: String, str2: String, n: Int){
        var ans = str1 + str2
        val l = (n*0.6).toInt()//referencia de la longitud del primer e
        val m = n - ans.length
        if (str1.length <=l) {

            imprimirCustom(str1 + String(CharArray(m)).replace("\u0000", " ") + str2, 1, 1)
        }else{
            val l = l
            val strs = splitToNChar(str1, l)
            imprimirCustom(strs[0] + String(CharArray(n - (strs[0].length + str2.length))).replace("\u0000", " ") + str2, 1, 1)
            for (s in 1.until(strs.size)){
                val a = n - strs[s].length
                imprimirCustom(strs[s] + String(CharArray(a)).replace("\u0000", " "), 1, 1)
            }
        }

    }

    private fun splitToNChar(text: String, size: Int): Array<String> {
        val parts = ArrayList<String>()

        val length = text.length
        var i = 0
        while (i < length) {
            parts.add(text.substring(i, Math.min(length, i + size)))
            i += size
        }
        return parts.toTypedArray()
    }

    override fun imprimirLinea() {
        try {
            outputStream!!.write(PrinterCommands.FEED_LINE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun imprimirTexto(msg: String) {
        try {
            // Print normal text
            outputStream!!.write(msg.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun imprimirTexto(msg: ByteArray) {
        try {
            // Print normal text
            outputStream!!.write(msg!!)
            imprimirLinea()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun imprimirFoto(img: Int) {
        try {
            val bmp = BitmapFactory.decodeResource(context!!.resources,
                    img)
            if (bmp != null) {
                val command = Utils.decodeBitmap(bmp)
                outputStream!!.write(PrinterCommands.ESC_ALIGN_CENTER)
                imprimirTexto(command)
            } else {
                Log.e("Print Photo error", "the file isn't exists")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PrintTools", "the file isn't exists")
        }
    }

    override fun imprimirUnicode() {
        try {
            outputStream!!.write(PrinterCommands.ESC_ALIGN_CENTER)
            imprimirTexto(Utils.UNICODE_TEXT)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun imprimirCustom(msg: String, size: Int, align: Int) {
        //Print config "mode"
        val cc = byteArrayOf(0x1B, 0x21, 0x03)  // 0- normal size text
        val ccd =  byteArrayOf(0x1B, 0x21, 0x47);  // 0- normal size text
        val bb = byteArrayOf(0x1B, 0x21, 0x08)  // 1- only bold text
        val bb2 = byteArrayOf(0x1B, 0x21, 0x20) // 2- bold with medium text
        val bb3 = byteArrayOf(0x1B, 0x21, 0x10) // 3- bold with large text
        try {
            when (size) {
                0 -> outputStream!!.write(cc)
                1 -> outputStream!!.write(bb)
                2 -> outputStream!!.write(bb2)
                3 -> outputStream!!.write(bb3)
                4 -> outputStream!!.write(ccd)
            }

            when (align) {
                0 ->
                    //left align
                    outputStream!!.write(PrinterCommands.ESC_ALIGN_LEFT)
                1 ->
                    //center align
                    outputStream!!.write(PrinterCommands.ESC_ALIGN_CENTER)
                2 ->
                    //right align
                    outputStream!!.write(PrinterCommands.ESC_ALIGN_RIGHT)
            }
            outputStream!!.write(msg.toByteArray())
            outputStream!!.write(PrinterCommands.LF.toInt())
            //outputStream.write(cc);
            //printNewLine();
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun leftRightAlign(str1: String, str2: String): String {
        var ans = str1 + str2
        if (ans.length < 30) {
            val n = 30 - ans.length
            ans = str1 + String(CharArray(n)).replace("\u0000", " ") + str2
        }
        return ans
    }

    override fun isConected(): Boolean {
        if (socket!=null && socket!!.isConnected)return true
        else return false
    }

    override fun hasPrinter(): Boolean {

        if (SharedPreferenceManager.getInstance(context).impresora != null) return true
        else {
            var result : Boolean = false
            mPConectando = ProgressDialog(context)
            mPConectando!!.setMessage("Buscando Impresora...")
            mPConectando!!.setCancelable(false)
            mPConectando!!.isIndeterminate
            mPConectando!!.show()
            val imei  = SharedPreferenceManager.getInstance(context).dispositivo.imei
            val rest = Rest.getCentral(context)
            if (rest!=null)rest.getImpresoras(imei!!, 1).enqueue(object : Callback<ArrayList<Impresora>> {

                override fun onResponse(call: Call<ArrayList<Impresora>>, response: Response<ArrayList<Impresora>>) {

                    if (response.isSuccessful) {

                        if (!response.body().isNullOrEmpty()) {
                            SharedPreferenceManager.getInstance(context).printer(response.body()!![0])
                            if (mPConectando != null && mPConectando!!.isShowing) {
                                mPConectando!!.dismiss()
                                mPConectando = null
                            }
                            result = true
                        } else {
                            SharedPreferenceManager.getInstance(context).borrarImpresora()
                            if (mPConectando != null && mPConectando!!.isShowing) {
                                mPConectando!!.dismiss()
                                mPConectando = null
                            }
                            context.toast("no hay impresora")
                            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
                            alertDialogBuilder.setTitle("No hay impresora predeterminada")
                            alertDialogBuilder.setMessage("Debe agregar una impresora predeterminada ")
                            alertDialogBuilder.setCancelable(false)
                            alertDialogBuilder.setIcon(R.drawable.ic_cancel)

                            alertDialogBuilder.setPositiveButton("Configuracion", { dialogInterface, i ->
                                context.startActivity<PairedDevices>()
                                dialogInterface.dismiss()
                            })

                            alertDialogBuilder.setNegativeButton("Salir", { dialogInterface, i ->
                                dialogInterface.dismiss()
                            })

                            val alertDialog = alertDialogBuilder.create()
                            alertDialog.show()
                        }
                    } else {
                        if (mPConectando != null && mPConectando!!.isShowing) {
                            mPConectando!!.dismiss()
                            mPConectando = null
                        }
                        try {
                            val g = response.errorBody()!!.string()
                            val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                            Crashlytics.logException(Throwable(errorResponse.mensaje))
                            FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                            ErrorUtils().showErrorApi(context, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                                hasPrinter()
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        } catch (e: Exception) {
                            e.printStackTrace()
//                            Crashlytics.logException(e)
                            FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                            ErrorUtils().showErrorApi(context, "Error", 0, "${e.localizedMessage}", DialogInterface.OnClickListener { dialogInterface, i ->
                                hasPrinter()
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<Impresora>>, t: Throwable) {
                    if (mPConectando != null && mPConectando!!.isShowing) {
                        mPConectando!!.dismiss()
                        mPConectando = null
                    }
//                    Crashlytics.logException(t)
                    FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                    ErrorUtils().showErrorApi(context, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                }


            })else if (mPConectando!=null && mPConectando!!.isShowing){
                mPConectando!!.dismiss()
                mPConectando = null
            }
            return result}
    }

    //i created a second function of hasPrinter to redirect
    //specifically to the user, this way there is not interruption in the stack of views
     fun hasPrinterToUser(): Boolean {

        if (SharedPreferenceManager.getInstance(context).impresora != null) return true
        else {
            var result : Boolean = false
            mPConectando = ProgressDialog(context)
            mPConectando!!.setMessage("Buscando Impresora...")
            mPConectando!!.setCancelable(false)
            mPConectando!!.isIndeterminate
            mPConectando!!.show()
            val imei  = SharedPreferenceManager.getInstance(context).dispositivo.imei
            val rest = Rest.getCentral(context)
            if (rest!=null)rest.getImpresoras(imei!!, 1).enqueue(object : Callback<ArrayList<Impresora>> {

                override fun onResponse(call: Call<ArrayList<Impresora>>, response: Response<ArrayList<Impresora>>) {

                    if (response.isSuccessful) {

                        if (!response.body().isNullOrEmpty()) {
                            SharedPreferenceManager.getInstance(context).printer(response.body()!![0])
                            if (mPConectando != null && mPConectando!!.isShowing) {
                                mPConectando!!.dismiss()
                                mPConectando = null
                            }
                            result = true
                        } else {
                            SharedPreferenceManager.getInstance(context).borrarImpresora()
                            if (mPConectando != null && mPConectando!!.isShowing) {
                                mPConectando!!.dismiss()
                                mPConectando = null
                            }
                            context.toast("no hay impresora")
                            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
                            alertDialogBuilder.setTitle("No hay impresora predeterminada")
                            alertDialogBuilder.setMessage("Debe agregar una impresora predeterminada ")
                            alertDialogBuilder.setCancelable(false)
                            alertDialogBuilder.setIcon(R.drawable.ic_cancel)

                            alertDialogBuilder.setPositiveButton("Configuracion", { dialogInterface, i ->
                                context.startActivity<PairedDevices>()
                                dialogInterface.dismiss()
                            })

                            alertDialogBuilder.setNegativeButton("Salir", { dialogInterface, i ->
                                context.startActivity<OperacionesActivity>()
                                dialogInterface.dismiss()
                            })

                            val alertDialog = alertDialogBuilder.create()
                            alertDialog.show()
                        }
                    } else {
                        if (mPConectando != null && mPConectando!!.isShowing) {
                            mPConectando!!.dismiss()
                            mPConectando = null
                        }
                        try {
                            val g = response.errorBody()!!.string()
                            val errorResponse: ErrorResponse = ErrorUtils().parseError(g)!!
//                            Crashlytics.logException(Throwable(errorResponse.mensaje))
                            FirebaseCrashlytics.getInstance().recordException(Throwable(errorResponse.mensaje))
                            ErrorUtils().showErrorApi(context, errorResponse.error!!, errorResponse.estado!!, errorResponse.mensaje!!, DialogInterface.OnClickListener { dialogInterface, i ->
                                hasPrinter()
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        } catch (e: Exception) {
                            e.printStackTrace()
//                            Crashlytics.logException(e)
                            FirebaseCrashlytics.getInstance().recordException(Throwable(e))
                            ErrorUtils().showErrorApi(context, "Error", 0, "${e.localizedMessage}", DialogInterface.OnClickListener { dialogInterface, i ->
                                hasPrinter()
                                dialogInterface.dismiss()
                            }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<Impresora>>, t: Throwable) {
                    if (mPConectando != null && mPConectando!!.isShowing) {
                        mPConectando!!.dismiss()
                        mPConectando = null
                    }
//                    Crashlytics.logException(t)
                    FirebaseCrashlytics.getInstance().recordException(Throwable(t))
                    ErrorUtils().showErrorApi(context, "Error", 0, t.localizedMessage, DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                }


            })else if (mPConectando!=null && mPConectando!!.isShowing){
                mPConectando!!.dismiss()
                mPConectando = null
            }
            return result}
    }

}