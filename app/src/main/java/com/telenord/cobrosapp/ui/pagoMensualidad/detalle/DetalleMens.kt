package com.telenord.cobrosapp.ui.pagoMensualidad.detalle

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.DetalleFacturacion

import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.pago.PagoMens
import kotlinx.android.synthetic.main.fragment_detalle_mens.view.*
import org.jetbrains.anko.support.v4.act


private const val listaParams = "lista"
private const val TAG = "DetalleMens"

class DetalleMens : androidx.fragment.app.Fragment(),DetalleMensContract.View {

    private var listener: OnFragmentInteractionListener? = null
    var rvDetalleFact: androidx.recyclerview.widget.RecyclerView? = null
    var mListener : DetalleMensContract.Listener? = null
    var mProgressBar : ProgressBar? = null

    var diferente : Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_detalle_mens, container, false)
        rvDetalleFact = v.rvDetalleFact
        mProgressBar = v.progressBar
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"OnCreate")

        setHasOptionsMenu(true)
        if (mListener == null){
            mListener = DetalleMensPresenter(this,requireContext())
        }
        arguments?.let {
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.setTitle("Detalle Fact.")
    }

    override fun showDetalles(list: ArrayList<DetalleFacturacion>) {
        val lista= ArrayList<DetalleFacturacion>()
        if (mFacturacion != null){
            lista.add(0, mFacturacion!!)
        }

        for (l in list){
            lista.add(l)
        }
        val adapter = DetalleFacturaAdapter(diferente!!, lista)
        val manager = androidx.recyclerview.widget.LinearLayoutManager(diferente!!)
        manager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        rvDetalleFact!!.layoutManager = manager
        rvDetalleFact!!.adapter = adapter
        showLoading(false)
    }



    override fun showLoading(show: Boolean) {
       if (show)mProgressBar!!.visibility = View.VISIBLE
        else mProgressBar!!.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            diferente = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener") as Throwable
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListener!!.getDetallesFacturacion(mClientes!!.contrato!!)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       return inflater!!.inflate(R.menu.config_printer,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId)
        {
            R.id.btnGuardarPrinter ->{
                val activity : PagoActivity = this.act as PagoActivity
                PagoActivity.fragmentLoaded = PagoActivity.fPago
                activity.MostrarFragment(PagoMens())

            }
        }


        return true
    }

    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

companion object {
    var mClientes : Clientes? = null
    var mFacturacion: DetalleFacturacion? = null
    fun listaFactura(cliente: Clientes)=
            DetalleMens().apply {
                arguments = Bundle().apply {
                    putSerializable(listaParams,cliente)

                }


            }
}
}
