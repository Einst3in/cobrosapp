package com.telenord.cobrosapp.ui.clientes

import android.content.Intent
import android.os.Bundle
import androidx.core.view.MenuItemCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import com.google.firebase.FirebaseApp
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import org.jetbrains.anko.support.v4.alert

import java.util.ArrayList

/**
 * Created by aneudy on 18/1/2017.
 */

class BuscaClientes_Fragment : androidx.fragment.app.Fragment(), Clientes_Contract.View, Clientes_Contract.Adapter{

    private var mSearchView: SearchView? = null
    private var refreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    private var LV: androidx.recyclerview.widget.RecyclerView? = null
    private var mLoading: ProgressBar? = null
    var ref: String? = null
    var adapt: PaginationAdapter? = null
    var page : Int? = 0
    private var mTotal: TextView? = null
    private var mListener: Clientes_Contract.Listener? = null
    private var mlLayout: LinearLayout? = null
    private var loading : Boolean = false
    private var lastPage = false
    private var currentPage = page!!
    private var totalPages : Int? = null

    private var filtro: String = ""



    override fun ShowList(clientes: ArrayList<Clientes>, count: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Buscar Cliente"
    }

    override fun onResume() {
        super.onResume()
        if (mListener == null) {
            mListener = Clientes_Presenter(this, requireContext(),adapt!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_busca_cliente, container, false)
        setHasOptionsMenu(true)
//        FirebaseApp.initializeApp(context)
        context?.let { FirebaseApp.initializeApp(it) }
       /* (activity as MainActivity).supportActionBar!!.title = "Clientes"*/
        prepareView(view)
        return view
    }


    private fun prepareView(view: View) {
        LV = view.findViewById<View>(R.id.ListClientes) as androidx.recyclerview.widget.RecyclerView
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(view.context)
        layoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        LV!!.layoutManager = layoutManager
        mLoading = view.findViewById<View>(R.id.mLoading) as ProgressBar

        refreshLayout = view.findViewById<View>(R.id.BuscaCliente_swifeRefresh) as androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        refreshLayout!!.setOnRefreshListener {
            lastPage = false
            page = 0
            currentPage = 0
            if(ref.isNullOrEmpty()){
//                toast("No hay criterios de busqueda")
                  Toast.makeText(context, "No hay criterios de busqueda", Toast.LENGTH_LONG).show()
                refreshLayout!!.isRefreshing = false

            }else{
                mListener!!.Find(ref!!,page!!)
            }

        }

        adapt = PaginationAdapter(requireContext(),this)
        LV!!.adapter = adapt

        LV!!.addOnScrollListener(object: PaginationScrollListener(layoutManager){


            override fun loadMoreItems() {
                loading = true
                currentPage +=1
                Log.e("Paginas",currentPage.toString())
                /*  mListener!!.Find(ref!!,currentPage)*/
                mListener!!.Find(ref!!,currentPage)
            }

            override fun getTotalPageCount(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

            override fun isLastPage(): Boolean {
               return lastPage
            }

            override fun isLoading(): Boolean {
                return loading
            }

        })


        mlLayout = view.findViewById(R.id.lLayoutClientes)
        mTotal = view.findViewById(R.id.txtTotalClientes)



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_busca_cliente, menu)
        super.onCreateOptionsMenu(menu!!, inflater)

        val searchItem = menu.findItem(R.id.action_search)
        mSearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        mSearchView!!.queryHint = "Criterio..."
        mSearchView!!.setOnCloseListener {
            //adapter.clearData();
            adapt!!.clear()
            false
        }
        mSearchView!!.setOnClickListener( object : View.OnClickListener{
            override fun onClick(v: View?) {
                adapt!!.clear()
            }
        })
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                if (adapt != null)adapt!!.clear()
                mSearchView!!.clearFocus()
                lastPage = false
                page = 0
                currentPage = 0
                Log.e("find", query.trim { it <= ' ' })
                ref = query.trim { it <= ' ' }
                /*mListener!!.Find(query.trim { it <= ' ' },page!!)*/
                mListener!!.Find(query.trim { it <= ' ' },page!!)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

    }

    override fun isLoading(c: Boolean) {
        loading = c
    }

    override fun ShowLoading(t: Boolean?) {
        refreshLayout!!.isRefreshing = t!!
    }

    override fun ShowMsj(msj: String) {
        alert(msj,"Atencion!!",{
            positiveButton("Aceptar",{})
        }).show()
    }

    override fun showCount(c: Int,t: Int) {
        var p = c
        if (lastPage) p = t
        mlLayout!!.visibility = View.VISIBLE
        mTotal!!.text = "${p} de ${t}"
    }


    override fun showCount(show : Boolean){
        if (show)mlLayout!!.visibility = View.VISIBLE
        else{ mlLayout!!.visibility = View.GONE
        filtro = ""}
    }
    override fun showTelefonos(telefonos: ArrayList<String>) {

    }

    override fun showMap(lat: Double, lng: Double) {

    }

    override fun goDetalle(cliente: Clientes) {
//        Memory.mCliente = cliente

        val inte = Intent(activity, OperacionesActivity::class.java)
        val bundle : Bundle = Bundle()
        bundle.putSerializable("cliente",cliente)
        inte.putExtras(bundle)

        requireActivity().startActivity(inte)
    }

    override fun isLastPage(isLast: Boolean) {
        lastPage = isLast
    }

    override fun AddItems(cliente: ArrayList<Clientes>, count: Int) {
    }

}
