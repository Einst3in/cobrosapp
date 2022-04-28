package com.telenord.cobrosapp.ui.impresora

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.impresora.PariedDevices.PairedDevices
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import com.telenord.cobrosapp.print.printUtils.PrinterOps
import com.telenord.cobrosapp.ui.main.MainActivity


class ImpresoraFragment: androidx.fragment.app.Fragment(), ImpresoraContract.View {

    override fun showError(error: String, msj: String, retry: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMsj(i: String) {
        Log.e("MSJ>>>>>>",i)
        toast(i)
    }

    val REQUEST_CODE = 100
    var mDefualtPrinterView: View?= null
    var mDefualtPrinterName:TextView?=null
    var mDefaultPrinterMac: TextView? = null
    var mDefualtPrinterTest:ImageView?=null
    var mList: androidx.recyclerview.widget.RecyclerView?=null
    var mListener:ImpresoraContract.Listener?=null
    var mNoPrinterView: View? = null
    var mProgressBar: ProgressBar? = null
    var printers =  ArrayList<Impresora>()
    var refreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    var mListAdapter:ImpresoraAdapter?=null
    var mNewPrinter: FloatingActionButton?=null
    var mCtx : Context? = null


    companion object {
        var mPrinterOps : PrinterOps? = null
        fun newInstance(): ImpresoraFragment {
            val fragment: ImpresoraFragment? = ImpresoraFragment()
            val bundle:Bundle?= Bundle()
            fragment!!.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            mCtx = context as Activity
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCtx = null

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mCtx as MainActivity).supportActionBar!!.title = "Config. Impresora"
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v  = inflater.inflate(R.layout.fragment_printers_layout, container, false)
        mListener=ImpresoraPresenter(requireContext(),this)
        initViews(v)
        mListener!!.getList()
        mListener!!.getDefaultPrinter()
//        toolbar.setTitle(R.string.nav_printer)

        return v
    }

    fun initViews(v: View)
    {

        mDefualtPrinterView = v.find(R.id.DefaultView)
        mNoPrinterView = v.find(R.id.NoPrinter)
        mDefualtPrinterName = mDefualtPrinterView!!.find(R.id.Model)
        mDefualtPrinterTest = mDefualtPrinterView!!.find(R.id.Test)
        mDefaultPrinterMac = mDefualtPrinterView!!.find(R.id.tvMacDefault)
        mProgressBar= v.find(R.id.pBarImpresora)
        mList = v.find(R.id.List)
        mNewPrinter= v.find(R.id.AddPrinter)
        mNewPrinter!!.setOnClickListener(View.OnClickListener {

            val intent =  Intent(context,PairedDevices::class.java)
            intent.putExtra("printer" , printers)
            startActivityForResult(intent,REQUEST_CODE)
        })
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
//        layoutManager.orientation = GridLayout.VERTICAL
        layoutManager.orientation = RecyclerView.VERTICAL
        mList!!.layoutManager =layoutManager

        mPrinterOps = PrinterOps(requireContext())
        refreshLayout = v.find(R.id.SwipeImpresora)
        refreshLayout!!.setOnRefreshListener {
            actualizar()
            refreshLayout!!.isRefreshing = false
        }
    }


    override fun showList(list: ArrayList<Impresora>) {
        for (f in list){
            printers.add(f)
            Log.e("Impresora",f.toString())
        }
        mListAdapter= ImpresoraAdapter(requireContext(),list,this)
        mList!!.adapter = mListAdapter
    }

    override fun showDefaultPrinter(p:Impresora) {
        printers.add(p)
        mNoPrinterView!!.visibility = View.GONE
        mDefualtPrinterView!!.visibility = View.VISIBLE
        mDefualtPrinterName!!.text=p.modelo
        mDefaultPrinterMac!!.text = p.mac
        mDefualtPrinterTest!!.onClick {mPrinterOps!!.ImprimirPrueba()
        }

    }

    fun actualizar(){
        printers.removeAll(printers)
        mListener!!.getList()
        mListener!!.getDefaultPrinter()
    }



    override fun showAlertDelete(printer: Impresora) {

        alert("Esta Seguro Que Quieres Borrar Esta Impresora","ATENCION!!"){
            positiveButton("Borrar"){
                printers.removeAll(printers)
                mListener!!.deletePrinter(printer)
                onResume()}
            negativeButton("Cancelar"){return@negativeButton}
        }.show()

    }

    override fun setDefault(printer: Impresora) {
        alert("Desea Que ${printer.modelo} Sea La Impresora Predeterminada?","ATENCION!!"){
            positiveButton("Aceptar"){mListener!!.setDefault(printer)
                onResume()}
            negativeButton("Cancelar"){return@negativeButton}
        }.show()
    }

    override fun showBluetoothPanel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading(t: Boolean) {
        mProgressBar!!.isIndeterminate = t
        if (t)mProgressBar!!.visibility = View.VISIBLE else mProgressBar!!.visibility = View.GONE
    }

    override fun goConfigureActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        actualizar()
    }

}
