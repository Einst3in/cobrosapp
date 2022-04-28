package com.telenord.cobrosapp.ui.cuadre


import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import android.widget.ProgressBar
import com.telenord.cobrosapp.models.Cuadre
import com.telenord.cobrosapp.print.printUtils.PrinterOps

import com.telenord.cobrosapp.R
import kotlinx.android.synthetic.main.fragment_cuadre.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import java.util.*


class CuadreFragment : androidx.fragment.app.Fragment(),CuadreContract.View {

    var mYear: Int? = null
    var mMonth: Int? = null
    var mDay: Int? = null
    var mProgressBar: ProgressBar? = null
    var mListener: CuadreContract.Listener? = null
    var mCuadre = Cuadre()
    var refreshLayout : androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListener = CuadrePresenter(this,this.requireContext())
        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Cuadre"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v =  inflater.inflate(R.layout.fragment_cuadre, container, false)
        initViews(v)
        return v
    }
    fun initViews(v: View){
      mProgressBar = v.find(R.id.pBarCuadre)
        refreshLayout = v.findViewById<View>(R.id.SwipeCuadre) as androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        refreshLayout!!.setOnRefreshListener {
                if (PrinterOps(requireContext()).hasPrinter())mListener!!.getCuadre(tvFechaCuadre.text as String)
                refreshLayout!!.isRefreshing = false
        }
    }

    override fun showCuadre(cuadre: Cuadre) {

        mCuadre = cuadre
        val adapter = CuadreAdapter(cuadre.cuadre!!)
        val lmanager = androidx.recyclerview.widget.LinearLayoutManager(requireView().context)
        lmanager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        rvCuadre.layoutManager = lmanager
        rvCuadre.adapter = adapter
        tvCantCuadre.text = cuadre.total!!.cantidad
        val m = cuadre.total!!.monto?.toDouble()
        var monto = if (m !=null ) m else 0.0
        tvMontoCuadre.text = "${monto}"

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater!!.inflate(R.menu.menu_imprimir,menu)
    }

    override fun showLoading(t: Boolean) {

      mProgressBar!!.isIndeterminate = t
        if (t) mProgressBar!!.visibility = View.VISIBLE
        else {mProgressBar!!.visibility = View.GONE
            refreshLayout!!.isRefreshing = false}
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val c = Calendar.getInstance();
         mYear = c.get(Calendar.YEAR);
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);
        tvFechaCuadre.text = "$mYear/${mMonth!! +1}/$mDay"

        ivCalendar.onClick {
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                tvFechaCuadre.text = "$year/${monthOfYear + 1}/$dayOfMonth"
                if (PrinterOps(requireContext()).hasPrinter())mListener!!.getCuadre(tvFechaCuadre.text as String)
            }, mYear!!, mMonth!!, mDay!!)

            dpd.show()
        }
        if (PrinterOps(requireContext()).hasPrinter())mListener!!.getCuadre(tvFechaCuadre.text as String)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            R.id.btnImprimir ->{
                alert("¿Desea Imprimir el total de cobros?","¡Atencion!"){
                    positiveButton("Aceptar"){
                        if (checkCuadre())PrinterOps(requireContext()).ImprimirCuadre(mCuadre)}
                   negativeButton("Cancelar"){} }.show()
                }

            }

        return true
    }

    fun checkCuadre(): Boolean{
        if (mCuadre.cuadre?.size == null || mCuadre.cuadre!!.size <= 0){
            alert("No hay cobros disponibles en esta fecha","¡No se puede imprimir el cuadre!"){
                positiveButton("Aceptar"){}
                negativeButton("Cancelar"){}
            }.show()
            return false
        }
        else return true
    }

}
