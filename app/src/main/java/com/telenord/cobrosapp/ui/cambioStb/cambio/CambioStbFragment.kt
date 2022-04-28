package com.telenord.cobrosapp.ui.cambioStb.cambio

import android.app.Presentation
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.telenord.cobrosapp.models.*

import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.cambioStb.CambioStbActivity
import com.telenord.cobrosapp.ui.cambioStb.seleccion.SeleccionStbFragment
import com.telenord.cobrosapp.ui.cambioStb.seleccion.SeleccionStbFragment.Companion.mCliente
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import kotlinx.android.synthetic.main.fragment_cambio_stb.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import java.util.function.Predicate

class CambioStbFragment : androidx.fragment.app.Fragment(),CambioStbContract.View,adapterInteractionCambio {

    lateinit var tvPrecio:TextView
    lateinit var rvStb: androidx.recyclerview.widget.RecyclerView
    lateinit var spTipoCambio: Spinner
    lateinit var mPbar: ProgressBar
    lateinit var tvCantDisponible: TextView
    lateinit var mListener : CambioStbContract.Listener


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_cambio_stb, container, false)
        setHasOptionsMenu(true)
        initValues(v)
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListener = CambioStbPresenter(requireContext(),this)
        mPago.opc_cargo = Pago.Tipo_OP.CS
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListener.getPrecioStb(SeleccionStbFragment.mCliente!!.contrato!!)

    }

    override fun showPrecioStb(list: List<PrecioSTB>) {
        var lista = ArrayList<PrecioSTB>()
        val p = PrecioSTB().also { it.descripcion = "Seleccionar Cambio"}

        lista.add(0,p).also { lista.addAll(list.filter { s->s.cantidad>0 && s.stb_anterior == SeleccionStbFragment.tipoSelected}) }

        val spAdapter = ArrayAdapter<PrecioSTB>(requireContext(),android.R.layout.simple_spinner_dropdown_item,lista)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoCambio.adapter = spAdapter

        val adapter = CambioStbAdapter(requireContext(),SeleccionStbFragment.Stbs,this)
        val manager = androidx.recyclerview.widget.LinearLayoutManager(context)
        manager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL

        spTipoCambio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val precio = parent!!.getItemAtPosition(position) as PrecioSTB
                if (precio.codigo!=null){

                    SeleccionStbFragment.Stbs.map { s->
                        s.codigo_cambio = precio.codigo
                        s.desc_cambio =   precio.descripcion!!
                        s.tipo_cambio =   precio.tecnologia!!
                        s.precioCambio = precio.precio
                    }
                    adapter.notifyDataSetChanged()
                     showPrecio(precio)
                }

            }
        }


        rvStb.adapter = adapter
        rvStb.layoutManager = manager



     // toast("${SeleccionStbFragment.Stbs.sumByDouble{it.precioCambio}},${SeleccionStbFragment.Stbs.size},${SeleccionStbFragment.Stbs[0].precioCambio}")
    }

    fun initValues(view: View){
        rvStb = view.findViewById(R.id.rvStbCambio)
        tvPrecio= view.findViewById(R.id.tvPrecioCambioStb)
        spTipoCambio = view.findViewById(R.id.spCambioStb)
        mPbar= view.findViewById(R.id.pBarCambioStb)
        tvCantDisponible = view.findViewById(R.id.tvCantStbCambioDisponibles)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       return inflater!!.inflate(R.menu.menu_cobrar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            R.id.men_guardar ->{

                   if (mPago.plan == null || mPago.plan!! <=0){
                       alert("Debe seleccionar el tipo de Cambio","Atencion!!").show()
                   }else{
                       if (mPrecioSTB.cantidad <=0 || SeleccionStbFragment.Stbs.size >=(mPrecioSTB.cantidad - mPrecioSTB.margen)){
                           alert("No hay suficientes STB para hacer el cambio","Atencion!!").show()
                       }else{
                           mPago.cambio_stb = SeleccionStbFragment.Stbs
                           setFacturacion()
                           goPagos()
                       }

                   }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setFacturacion(){
      val lista =  SeleccionStbFragment.Stbs.groupingBy { it.desc_cambio }.eachCount()
        var msj = ""
        for((k,v) in lista){
            msj = msj + "$v $k "
        }
        mFacturacion.concepto = msj

    }

    override fun changeType(precio: PrecioSTB) {
        val list = SeleccionStbFragment.Stbs!!.filter { s->s.codigo_cambio > 0 }.size
        if (list == 1) {
            tipoCambio = precio.codigo
        }
        if (list == 0 ){
            tipoCambio = 0
        }
        toast("${tipoCambio}")
    }

    fun goPagos(){
        val activity = this.act as CambioStbActivity
        activity.goPagos(mCliente, mPago, mFacturacion)
    }
    fun showPrecio(precio: PrecioSTB) {
        mPrecioSTB = precio
        val total = SeleccionStbFragment.Stbs.sumByDouble{it.precioCambio}
        if (precio.cantidad <=0 || SeleccionStbFragment.Stbs.size >=(precio.cantidad - precio.margen) ){
            tvCantDisponible.setTextColor(requireContext().resources.getColor(R.color.desconectado))
        }else{
            tvCantDisponible.setTextColor(requireContext().resources.getColor(R.color.primary_dark))
        }
        tvCantDisponible.text = "${precio.cantidad}"
        mFacturacion.balance= total
        mFacturacion.monto = total
        mFacturacion.pagado = 0.0
        mPago.cargo = total
        mPago.monto=  total
        mPago.plan = precio.codigo
        mPago.cantidad =  SeleccionStbFragment.Stbs.size
        tvPrecio.text = "${total}"
    }

    override fun showLoading(t: Boolean) {
        mPbar.isIndeterminate = t
        if (t){
            mPbar.visibility = View.VISIBLE
        }else {
            mPbar.visibility = View.GONE
        }
    }

    companion object{
        var mPago:Pago = Pago()
        var mFacturacion = DetalleFacturacion()
        var mPrecioSTB = PrecioSTB()
        var tipoCambio = 0}

}
interface adapterInteractionCambio{
    fun changeType(precio: PrecioSTB)
}
