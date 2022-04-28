package com.telenord.cobrosapp.ui.cardnet

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoContract
import com.telenord.cobrosapp.util.ErrorUtils
import com.telenord.cobrosapp.util.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class JsInterface (private val mContext: Context,private val mCardenetListener: CardnetContract.Listener)  {

    protected var parentActivity: CardnetActivity? = null

    var retrofitInstance: Retrofit? = null
    var mProgressBar: ProgressBar? = null
    var mListener: PagoContract.Listener? = null
//    lateinit var mListenerCardnet : CardnetContract.Listener
    var mCliente: Clientes? = null
    var dialogValid: Int? = 0
    var mPago = Pago()
    var mProgress: ProgressDialog? = null



    /** error al momento del pago*/
    @JavascriptInterface
    fun er(response: String) {
        val pago = Pago()
        Log.d("in android-->", response)
        Log.d("error->", "error al momento de procesar pago en Telenord")


        val serverresponse = JSONObject(response)

        val serverinfo = JSONObject(serverresponse.get("serverInfo").toString())
        val men1 = JSONObject(serverinfo.getString("mensaje"))
        Log.d("mensaje->", men1.toString())
        //obteniendo
        val con1 = serverinfo.getString("contrato")
        val mon1 = serverinfo.getString("monto")
        val tipopago = serverinfo.getString("tipo_pago")
        val opc = serverinfo.get("opc_cargos")
        Log.d("contrato->", con1.toString())
        pago.opc_cargo = Pago.Tipo_OP.FC
        if(opc.toString().equals("")){
            pago.opc_cargo = null
        }
        showToast("Error al procesar el pago en nuestros servicios.")

        //Lenar objeto de pago para enviarlo a reintentar
        pago.cargo = 0.0
        pago.monto = mon1.toDouble()
        pago.tipo_pago = tipopago
        pago.imei = SharedPreferenceManager.getInstance(mContext).dispositivo.imei!!
        pago.horario = null
        pago.cantidad = null
        pago.descuento = null
        pago.porciento = null
        pago.tipo = null
        pago.cod_serv = null
        pago.desc_serv = null
        pago.plan = null
        pago.traslado = null
        pago.extension = 0
        pago.lat = 0.0
        pago.lng = 0.0
        pago.cambio_stb = null
        pago.facturacion = null

        val bundle = Bundle()
        bundle.putString("contrato", con1.toString())
        bundle.putString("monto", mon1.toString())
        bundle.putString("tipo_pago", tipopago)
//        mCardenetListener.retry(400, men1.toString(), con1.toString(), bundle)
        mCardenetListener.retry(con1.toString(), pago)
    }



    /** Function that get the response to from the api */
    @JavascriptInterface
    fun recibo(response: String) {
        Log.d("in android-->", response)
        //convert response to json
        val serverresponse = JSONObject(response)

        val serverStatus = JSONObject(serverresponse.get("statusCode").toString())
        val status = serverStatus.get("statusCode").toString()
        Log.d("serverStatus->", serverStatus.toString())
        successalert(response)
    }

    /** Show a confirmation toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    /** Print recibo of the payment */
    @JavascriptInterface
    fun printRecibo(message: String) {

        //get the response as string
        Log.e("Recibo ->", message)

        //Convert the response to Json
        val answer = JSONObject(message)
        val recibo = JSONObject(answer.get("recibo").toString())
        val documento = JSONObject(answer.get("documento").toString())
        val cardnet = JSONObject(answer.get("cardnet").toString())
        val boleto = JSONObject(answer.get("boleto").toString())
        Log.e("cardnet->", cardnet.get("authorization_code").toString())

        //recibo
        val tipoRecibo = ReciboFact()
        val tipoDocumento = DocumentoFact()
        val tipoBoleto = Boleto()

        // R E C I B O

        //fill the object recibo
        tipoRecibo.trn = recibo.get("trn").toString()
        tipoRecibo.conceptoPago = recibo.get("concepto_pago").toString()
        tipoRecibo.conceptoGenerado = recibo.get("concepto_generado").toString()
        tipoRecibo.fecha = recibo.get("fecha").toString()
        tipoRecibo.cobrador = recibo.get("cobrador").toString()
        tipoRecibo.tServ = recibo.get("t_serv").toString().toInt()
        tipoRecibo.balance = recibo.get("balance").toString().toDouble()
        tipoRecibo.balanceAnterior = recibo.get("balance_anterior").toString().toDouble()
        tipoRecibo.contrato = recibo.get("contrato").toString()
        tipoRecibo.monto = recibo.get("monto").toString().toDouble()
        tipoRecibo.tipo_pago = recibo.get("tipo_pago").toString()
        tipoRecibo.nombreCliente = recibo.get("nombre_cliente").toString()
        tipoRecibo.authorization_code = cardnet.get("authorization_code").toString()
        tipoRecibo.credit_card_number = cardnet.get("credit_card_number").toString()

        //fill the object documento
        tipoDocumento.companyName = documento.get("company_name").toString()
        tipoDocumento.companyAddress = documento.get("company_address").toString()
        tipoDocumento.companyCity = documento.get("company_city").toString()
        tipoDocumento.companyContact = documento.get("company_contact").toString()
        tipoDocumento.document = documento.get("document").toString()
        tipoDocumento.separador = documento.get("separador").toString()
        tipoDocumento.footer = documento.get("footer").toString()
        tipoDocumento.nota = documento.get("nota").toString()
        tipoDocumento.rnc = documento.get("rnc").toString()
        tipoDocumento.tipo_pago = documento.get("tipo_pago").toString()
        tipoDocumento.caracteresLinea = documento.get("caracteres_linea").toString().toInt()


        //fill the object boleto

        val response = ResponseFactura(tipoRecibo, tipoDocumento)
        val b = Boleto()
        if(boleto.length() != 0){
            tipoBoleto.descripcion
            tipoBoleto.ticket
        }
        response.boleto = b

        val bundle = Bundle()
        bundle.putSerializable("response", response)
        mCardenetListener.success(bundle)

    }

    fun successalert(message: String) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("EXITO")
        builder.setMessage("Pago realizado correctamente")
        builder.setIcon(R.drawable.ic_check_black_24dp)
        builder.setCancelable(false)
        //positive
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            printRecibo(message)
        }

        builder.show()
    }

    fun showLoading(l: Boolean) {
        if (l!!) {
            mProgress!!.show()
        } else {
            mProgress!!.dismiss()
        }
    }
}
