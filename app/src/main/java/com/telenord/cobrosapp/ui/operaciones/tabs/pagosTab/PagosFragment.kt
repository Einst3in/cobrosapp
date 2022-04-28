package com.telenord.cobrosapp.ui.operaciones.tabs.pagosTab


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaRouter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.print.printUtils.PrinterOps
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import com.telenord.cobrosapp.ui.historial.HistorialActivity
import com.telenord.cobrosapp.ui.pagoAdelantado.PagoAdelantadoActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.pago.PagoMens
import kotlinx.android.synthetic.main.fragment_pago_mens.*
import kotlinx.android.synthetic.main.fragment_pagos.*
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class PagosFragment : androidx.fragment.app.Fragment(),PagosFragmentContract.View {


    var btnPagoMens : ImageView? = null
    var btnPagoPA : ImageView? = null
    var btnReimprimir : ImageView? = null
    var mListener : PagosFragmentContract.Listener? = null
    var mProgressDialog: ProgressDialog? = null
    var btnHistorial: ImageView? = null

    var flPagoMens : LinearLayout? = null
    var flPagoPA : LinearLayout? = null

    var mCliente : Clientes? = null

    private val REQUEST_RESULT = 200

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v : View = inflater.inflate(R.layout.fragment_pagos, container, false)
        mCliente = arguments!!.getSerializable("cliente") as Clientes
        mListener = PagosFragmentPresenter(this,context!!)
        Log.e(">>>>>>>>>>","Pagos")
        initViews(v)
        return v
    }



    companion object {
        fun newInstance(mCliente:Clientes): PagosFragment {
            val fragment: PagosFragment?= PagosFragment()
            val bundle:Bundle?= Bundle()
            bundle!!.putSerializable("cliente",mCliente)
            fragment!!.arguments = bundle
            return fragment
        }
        var mFactura : ResponseFactura? = null
    }

//    override fun onAttach(context: Context?) {
////        super.onAttach(context)
////        arguments?.get("cliente")?.let {
////            mCliente =
////        }
////    }
override fun Reimprmir(factura: ResponseFactura) {
    PrinterOps(this@PagosFragment.context!!).ImprimirFactura(factura)
}

    override fun showLoading(t: Boolean) {
        if (t){
            mProgressDialog!!.isIndeterminate = true
            mProgressDialog!!.show()
        }else if(!t){
           mProgressDialog!!.dismiss()
        }
    }

    override fun showError(e: String) {
        toast(e)
    }

    fun initViews(v: View){
        btnPagoMens = v.find(R.id.ibPagoMens)
        flPagoMens = v.find(R.id.flPagoMens)
        btnPagoPA = v.find(R.id.ibPagoPA)
        btnHistorial = v.find(R.id.ibHistorial)
        flPagoPA = v.find(R.id.flPagoPA)
        btnReimprimir = v.find(R.id.ibReimprimir)

        mProgressDialog = ProgressDialog(context)
        mProgressDialog!!.setMessage("Reimprimiendo")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        flPagoMens!!.onClick { goPagos("Pago Mensualidad") }
        btnPagoMens!!.onClick { goPagos(1) }
//        flPagoCaja!!.onClick { goPagos("Pago Caja Digital") }
        btnPagoPA!!.onClick { goPagosAdelantado() }
//        flPagoPA!!.onClick { startActivity<PagoAdelantadoActivity>() }

        btnReimprimir!!.onClick{
            if (PrinterOps(this@PagosFragment.context!!).hasPrinter())mListener!!.getReimprimir(mCliente!!)
          }
        ibHistorial!!.onClick {

            //toast("Servicio no disponible")
            goHistorial()
        }
    }
    /**
     * tipo de pago
     * 1- mensualidad
     * 2- pago de caja
     * 3- pago adelantado
     *
     * */

    fun goPagos(tipo: Int){
        val intent = Intent(activity,PagoActivity::class.java)
        val bundle: Bundle? = Bundle()
        bundle!!.putSerializable("cliente",mCliente!!)
        bundle!!.putInt("tipo_pago",tipo)
        intent!!.putExtras(bundle)
        intent!!.putExtra("tipo_op",Pago.Tipo_OP.FC)
        validacionPagosPendientes(intent)
    }

    fun showalertpagospendientes(mensaje : String){
        //check net first
        var mensaje = "CONTRATO TIENE PAGOS PENDIENTES POR REALIZAR"
        alert (mensaje) {

            positiveButton("PROCESAR") {

                //*****************************************
                val rest = Rest.getCentral(context!!)
                val contt = (activity as OperacionesActivity).mContrato?.text.toString()
                if(rest != null)rest.getpagosPendientes(SharedPreferenceManager.getInstance(context!!).dispositivo.imei!!, SharedPreferenceManager.getInstance(context!!).info.cobradorid!!, contt).enqueue(object : Callback<List<PagosPendientes>>{
                    override fun onResponse(call: Call<List<PagosPendientes>>, response: Response<List<PagosPendientes>>) {
                        if(response.isSuccessful){
                           val r = (response.body() as ArrayList<PagosPendientes>)
                            r.get(0).contrato
                            r.get(0).codigo
                            r.get(0).cobradorid
                            r.get(0).nombreCobrador

                            Log.d("nombreCobrador-->", r.get(0).nombreCobrador!!)
                            Log.d("fdfs","hola")
                            val pago = Pago()
                            pago.cargo = 0.0
                            pago.monto = r.get(0).monto //get from pagospensientes table
                            pago.tipo_pago = "TA" //get from TA
                            pago.imei = r.get(0).imei
                            pago.opc_cargo = Pago.Tipo_OP.FC
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

//                            val insert: String =
//                                "${etNoTarjeta!!.text},${idBanco},$monto,${etBoucher.text},'$tipoTarjeta'"

                            val insert: String =
                                "'${r.get(0).tarjeta}',0,${r.get(0).monto},${r.get(0).aprobacionCardnet},'C'"
                            pago.insert = insert

                            //validacion para que el cobrador que realizo el pago pendiente sea quien cobre el mismo pago pendiente no otro
                            Log.d("cobradorId->", "android--->"+SharedPreferenceManager.getInstance(context!!).info.cobradorid)
                            Log.d("cobradorId->", "api--->"+r.get(0).cobradorid)

                            if(SharedPreferenceManager.getInstance(context!!).info.cobradorid == r.get(0).cobradorid){
                                mListener?.procesarPagoPendiente(mCliente!!, pago)

                            }else{
                                showalertpagospendientes("USTED NO ES EL COBRADOR QUE GENERO EL PAGO PENDIENTE")
                                Toast.makeText(context, "USTED NO ES EL COBRADOR QUE GENERO EL PAGO PENDIENTE", Toast.LENGTH_LONG).show()
                            }



                        }else{
                            val g = response.errorBody()!!.string()
                            Log.d("pagospendientes", "err->>>" + g)
                        }
                    }

                    override fun onFailure(call: Call<List<PagosPendientes>>, t: Throwable) {
                        Log.e("pagospendientes", "paso algo rarooooo")
                    }
                })
                //*****************************************



            }

            negativeButton("CANCELAR") {
                Log.d("pagospendientes", "se cancelo el alert")
            }
        }.show()
    }

    fun validacionPagosPendientes(intent : Intent){
        val rest = Rest.getCentral(context!!)
        val contt = (activity as OperacionesActivity).mContrato?.text.toString()
        if(rest != null)rest.getpagosPendientes(SharedPreferenceManager.getInstance(context!!).dispositivo.imei!!, SharedPreferenceManager.getInstance(context!!).info.cobradorid!!, contt).enqueue(object : Callback<List<PagosPendientes>>{
            override fun onResponse(call: Call<List<PagosPendientes>>, response: Response<List<PagosPendientes>>) {
                if(response.isSuccessful){

                    val r = (response.body() as ArrayList<PagosPendientes>)
                    val arr : Array<PagosPendientes> = r.toTypedArray()
                    if(arr.size == 0){
                        Log.d("pagospendientes", "Cobrador no tiene pagos pendientes")
                        val nommm = (activity as OperacionesActivity).mNombre?.text.toString()
                        intent.putExtra(nommm, "nombreCliente")
                        startActivityForResult(intent,REQUEST_RESULT)
                    }else{
                        Log.d("pagospendientes", "cobrador tiene pagos pendientes")
                        showalertpagospendientes("CONTRATO TIENE PAGOS PENDIENTES POR REALIZAR")
                    }
//                    arr.forEach {
//                        it.
//                    }

                }else{
                    val g = response.errorBody()!!.string()
                    Log.d("pagospendientes", "err->>>" + g)
                }
            }

            override fun onFailure(call: Call<List<PagosPendientes>>, t: Throwable) {
                Log.e("pagospendientes", "paso algo rarooooo")
            }
        })
    }






    fun goHistorial(){
        startActivity<HistorialActivity>("cliente" to mCliente!!)
        //startActivity<CambioStbActivity>("cliente" to mCliente!!)
    }
    fun goPagosAdelantado(){

        if (mCliente!!.getTipoStatus() == Clientes.tipoStatus.CONECTADO || mCliente!!.getTipoStatus() == Clientes.tipoStatus.PENDIENTE) {

            val intent = Intent(activity, PagoAdelantadoActivity::class.java)
            intent!!.putExtra("cliente", mCliente!!)
            startActivityForResult(intent, REQUEST_RESULT)
        }
            else{
            alert("El cliente no está conectado","No se puede hacer el pago"){
                positiveButton("Aceptar"){}
                negativeButton("Cancelar"){}
            }.show()
        }


    }

    override fun reimpresionCompleta() {
        (activity as OperacionesActivity).cargarDatos()
    }

    override fun getFactura(factura: ResponseFactura) {
        PrinterOps(context!!).ImprimirFactura(factura)
        mFactura = factura
        (activity as OperacionesActivity).cargarDatos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Activity Result", " se inicio la vaina")
        if (requestCode == REQUEST_RESULT && resultCode == Activity.RESULT_OK) {
            var factura = data!!.getSerializableExtra("factura") as ResponseFactura
            PrinterOps(context!!).ImprimirFactura(factura)
            mFactura = factura
            (activity as OperacionesActivity).cargarDatos()
        }
    }

    override fun endalert() {
        alert("klk con el pendiente"){
            positiveButton("Aceptar"){
                mListener?.deletePagoPendiente("A0023301A")
            }
        }
    }

}
