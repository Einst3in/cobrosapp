package com.telenord.cobrosapp.ui.cambioStb.seleccion


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.STB

import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.cambioStb.CambioStbActivity
import com.telenord.cobrosapp.ui.cambioStb.cambio.CambioStbFragment
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

/**
 * A simple [Fragment] subclass.
 */
class SeleccionStbFragment : androidx.fragment.app.Fragment() , SeleccionStbContract.View,adapterInteraction {


    lateinit var rvStb: androidx.recyclerview.widget.RecyclerView
    lateinit var mListener : SeleccionStbContract.Listener
    lateinit var tvCantStb : TextView
    lateinit var mPbar: ProgressBar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val v= inflater.inflate(R.layout.fragment_seleccion_stb, container, false)
        initValues(v)
        return v
    }

    fun initValues(view:View){
        rvStb = view.findViewById(R.id.rvStbSeleccion)
        tvCantStb = view.findViewById(R.id.tvCantStbCambio)
        mPbar = view.findViewById(R.id.pBarSelectStb)

        if (mCliente!=null){
            mListener.getSTB(mCliente!!.contrato!!)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Stbs.removeAll(Stbs)
        tipoSelected= ""
        mListener = SeleccionStbPresenter(requireContext(), this)
    }


    override fun showCantStb(cant:Int) {
        tvCantStb.text = "${cant}"
    }

    override fun showLoading(t: Boolean) {
        mPbar.isIndeterminate = t
        if (t){
            mPbar.visibility = View.VISIBLE
        }else {
            mPbar.visibility = View.GONE
        }
    }

    override fun showStb(stbs: List<STB>) {
        Log.e("Las Cajas",stbs.toString())
        stbs.map { t->t.marcada = false }
        val stbsFilter = stbs.filter { stb -> stb.cambio_pendiente == 0 }as ArrayList<STB>
        val adapter = SeleccionStbAdapter(requireContext(), stbsFilter ,this,View.VISIBLE)
        val manager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        manager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        rvStb.layoutManager = manager
        rvStb.adapter =adapter


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater!!.inflate(R.menu.config_printer,menu)
    }

    override fun changeType() {
        if (Stbs.size ==1){
            tipoSelected = Stbs[0].tipo
        }
        if (Stbs.size ==0){
            tipoSelected = ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.btnGuardarPrinter->{
                if (Stbs.size >0){
                    val activity: CambioStbActivity = this.act as CambioStbActivity
                    CambioStbActivity.fragmentLoaded = CambioStbActivity.fCambio
                    activity.MostrarFragment(CambioStbFragment())
                }else if (Stbs.size <=0){
                    alert("Debe agregar al menos una STB","Atencion!!",{positiveButton("Aceptar",{})}).show()
                }

            }
        }
        return true
    }

    companion object{
        var mCliente:Clientes? = null
        var tipoSelected = ""
        var Stbs = ArrayList<STB>()
    }



}

interface adapterInteraction{
fun showCantStb(cant:Int)
fun changeType()
}
