package com.telenord.cobrosapp.ui.operaciones.tabs.operacionesTab

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.print.printUtils.PrinterOps
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import com.telenord.cobrosapp.ui.operaciones.tabs.pagosTab.PagosFragment
import com.telenord.cobrosapp.ui.asignarInternet.InternetActivity
import com.telenord.cobrosapp.ui.extension.ExtensionActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import com.telenord.cobrosapp.ui.stbOps.StbOpsActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast


class OpFragment : androidx.fragment.app.Fragment(), OpFragmentContract.View {


    var flAverias: LinearLayout? = null
    var ibAveria: ImageView? = null
    var mCliente : Clientes? = null
    var ibReconexion: ImageView? = null
    //var ibExtension : ImageView? = null
    var mPago = Pago()
    var mListener: OpFragmentContract.Listener? = null
    var mProgress: ProgressDialog? = null
    var ibTraslado: ImageView? = null
    var ibInternet: ImageView? = null
    var ibStb: ImageView? = null
    private val REQUEST_RESULT = 200

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v  = inflater.inflate(R.layout.fragment_op, container, false)
        mCliente = requireArguments().getSerializable("cliente") as Clientes
        mListener = OpFragmentPresenter(this, requireContext())
        initViews(v)
        Log.e(">>>>>>>>>>", "Operaciones")
        return v
    }

    companion object {
        fun newInstance(mCliente: Clientes): OpFragment {
            val fragment: OpFragment? = OpFragment()
            val bundle: Bundle?= Bundle()
            bundle!!.putSerializable("cliente", mCliente)
            Log.e("contrato------", mCliente.contrato!!)
            fragment!!.arguments = bundle
            return fragment
        }
    }



    fun initViews(v: View)
    {
        mProgress = ProgressDialog(context)
        mProgress!!.setMessage("Cargando...")
        flAverias = v.find(R.id.flAverias)
        ibAveria = v.find(R.id.ibAverias)
        ibReconexion = v.find(R.id.ibReconexion)
        //ibExtension = v.find(R.id.ibExtension)
        ibTraslado = v.find(R.id.ibTraslado)
        ibStb = v.find(R.id.ibAddSTB)
        ibInternet = v.find(R.id.ibInternet)

    }

    override fun showLoading(t: Boolean) {
        if (t) {
            mProgress!!.show()
        } else {
            mProgress!!.dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        ibAveria!!.onClick {  if (checkTipo(Clientes.tipoStatus.PENDIENTE_DESCONECTAR) && checkTipo(Clientes.tipoStatus.PENDIENTE) && checkTipo(Clientes.tipoStatus.DESCONECTADO)) goAveria()  }

        ibReconexion!!.onClick {  if (checkTipo(Clientes.tipoStatus.PENDIENTE_DESCONECTAR) && checkTipo(Clientes.tipoStatus.PENDIENTE)) alertReconexion() }

//        ibExtension!!.onClick { if (checkTipo(Clientes.tipoStatus.PENDIENTE_DESCONECTAR) && checkTipo(Clientes.tipoStatus.PENDIENTE ) && checkTipo(Clientes.tipoStatus.DESCONECTADO))goExtension() }

        ibTraslado!!.onClick { if (checkTipo(Clientes.tipoStatus.PENDIENTE_DESCONECTAR) && checkTipo(Clientes.tipoStatus.PENDIENTE))goTraslado() }

        ibStb!!.onClick { if ( checkTipo(Clientes.tipoStatus.PENDIENTE_DESCONECTAR) && checkTipo(Clientes.tipoStatus.DESCONECTADO))goStb() }

        ibInternet!!.onClick { if (checkTipo(Clientes.tipoStatus.PENDIENTE_DESCONECTAR) && checkTipo(Clientes.tipoStatus.DESCONECTADO))goInternet() }
    }

    fun goExtension(){
        val intent = Intent(activity, ExtensionActivity::class.java)
        intent!!.putExtra("cliente", mCliente!!)
        startActivityForResult(intent, REQUEST_RESULT)
    }
    fun goTraslado(){
        //funcion que mustras el mensaje "Servicio NO disponible"
        servicioNoDisponible()
//        val intent = Intent(activity, TrasladoActivity::class.java)
//        intent!!.putExtra("cliente",mCliente!!)
//        startActivityForResult(intent,REQUEST_RESULT)
    }

    fun goStb(){
        val intent = Intent(activity, StbOpsActivity::class.java)
        intent!!.putExtra("cliente", mCliente!!)
        startActivityForResult(intent, REQUEST_RESULT)
    }
    fun goInternet(){
        val intent = Intent(activity, InternetActivity::class.java)
        intent!!.putExtra("cliente", mCliente!!)
        startActivityForResult(intent, REQUEST_RESULT)
    }

    fun goAveria(){
        //Funcion que lleva a Whatsapp
        gotoWhatsapp()
//       val intent = Intent(activity, AveriasActivity::class.java)
//       intent!!.putExtra("cliente",mCliente!!)
//       startActivity(intent)
//        alert("No puedes reportar averias, comunicate a Atencion al Cliente","Atencion!!!") {
//            positiveButton("Aceptar",{})
//        }.show()
    }




    override fun showAlert(precio: Precio) {
        val facturacion = DetalleFacturacion("", precio.fecha, precio.precio, precio.precio, 0.0, precio.fecha, "Reconexion")
        mPago.apply {
            cargo = precio.precio
            monto = precio.precio
            desc_serv = "Reconexion"
            opc_cargo = Pago.Tipo_OP.RC
        }
        alert("¿Desea Cargar ${precio.precio} de reconexion al contrato ${mCliente!!.contrato}?", "Atencion") {
            positiveButton("Aceptar") {
                val intent = Intent(activity, PagoActivity::class.java)
                val bundle: Bundle? = Bundle()
                bundle!!.putSerializable("cliente", mCliente!!)
                bundle!!.putSerializable("facturacion", facturacion!!)
                bundle!!.putSerializable("pago", mPago)
                intent!!.putExtras(bundle)
                startActivityForResult(intent, REQUEST_RESULT)
            }
            negativeButton("Cancelar", {})
        }.show()
    }

    fun alertReconexion(){
        if (mCliente!!.getTipoStatus() == Clientes.tipoStatus.DESCONECTADO && mCliente!!.balance > mCliente!!.mensualidad){
            alert("El cliente debe de saldar un minimo de $ ${mCliente!!.balance - mCliente!!.mensualidad}", "No se puede reconectar", {
                positiveButton("Aceptar", {})
            }).show()
        }else{
        when(mCliente!!.getTipoStatus())
        {
            Clientes.tipoStatus.CONECTADO -> {
                alert("¿Enviar Comando M15?", "Cliente Conectado", {
                    positiveButton("Aceptar", {
                        toast("Enviando M15....")
                        mListener!!.postM15(mCliente!!.contrato!!)
                    })
                    negativeButton("Cancelar", {})
                }).show()
            }
            Clientes.tipoStatus.DESCONECTADO -> {
                mListener!!.getPrecio(Pago.Tipo_OP.RC, mCliente!!.contrato!!)
            }
            Clientes.tipoStatus.PENDIENTE -> {
                alert("No se puede realizar, el cliente tiene orden pendiente", "Orden Pendiente", {
                    positiveButton("Aceptar", { toast("Pendiente....") })
                    negativeButton("Cancelar", {})
                }).show()
            }
            Clientes.tipoStatus.PENDIENTE_DESCONECTAR -> {
                alert("No se puede realizar, el cliente tiene orden pendiente", "Orden Pendiente", {
                    positiveButton("Aceptar", { toast("Pendiente....") })
                    negativeButton("Cancelar", {})
                }).show()
            }
            null -> {
                alert("No se puede realizar esta accion", "Atencion", {
                    positiveButton("Aceptar", { toast("Nulo....") })
                    negativeButton("Cancelar", {})
                }).show()
            }
        }

    }}

    fun checkTipo(tipo: Clientes.tipoStatus) : Boolean{
        if(mCliente!!.getTipoStatus() ==  tipo)
        {
            alert("el cliente está ${tipo.name.toLowerCase()}", "No se puede realizar esta acción"){
                positiveButton("Aceptar"){}
                negativeButton("Cancelar"){}
            }.show()
            return false
        }
        else
            return true
        }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RESULT && resultCode == Activity.RESULT_OK) {
            var factura = data!!.getSerializableExtra("factura") as ResponseFactura
            PrinterOps(requireContext()).ImprimirFactura(factura)
            PagosFragment.mFactura = factura
            (activity as OperacionesActivity).cargarDatos()
        }
    }

    //Funcion para avisar que el servicio no esta disponible
    fun servicioNoDisponible(){
        val alert = alert("SERVICIO NO DISPONIBLE", "LO SENTIMOS"){
            positiveButton("Aceptar"){
                Log.d("estado->", "SERVICIO DE TRANSLADO NO DISPONIBLE")
            }
        }
        alert.build()
        alert.show()
    }

    fun gotoWhatsapp(){
        var contrato = mCliente?.contrato
        var nombreCobrador = SharedPreferenceManager.getInstance(context).info.usuario
        Log.d("informacion ->", nombreCobrador!!)
        Log.d("informacion -> ", contrato!!)
        var mensaje = "Hola, soy el cobrador "+nombreCobrador+", quiero reportar una averia al contrato "+contrato+""
        var numero = "8097250808"
        var codigoPais = "+1" //+1 es el codigo de pais de republica dominicana

        var appInstalada = appInstaladacheck("com.whatsapp")
        var instalada =  appInstaladacheck("com.whatsapp");
        if(!instalada) {
            val alert = alert("WHATSAPP NO ESTA INSTALDA, POR FAVOR INSTALARLA PARA PODER USAR ESTA FUNCION") {
                positiveButton("aceptar") {
                    Log.d("estado->", "La aplicacion de whatsapp no esta instala en el dispositivo")
                }
            }
            alert.build()
            alert.show()
        }else{
            var intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone="+codigoPais+""+numero+"&text="+mensaje+""))
            startActivity(intent)
        }

    }

    private fun appInstaladacheck(url: String): Boolean {
        val pm = requireActivity().packageManager
        val app_installed: Boolean
        app_installed = try {
            pm.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }


}
