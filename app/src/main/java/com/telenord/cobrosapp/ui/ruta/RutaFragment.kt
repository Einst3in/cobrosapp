package com.telenord.cobrosapp.ui.ruta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.Concepto
import com.telenord.cobrosapp.models.Ruta
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.clientes.PaginationScrollListener
import com.telenord.cobrosapp.ui.dialogs.PhonesDialog
import com.telenord.cobrosapp.ui.dialogs.visitaRuta.VisitaRuta
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import kotlinx.android.synthetic.main.fragment_ruta.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import java.util.ArrayList


class RutaFragment : androidx.fragment.app.Fragment(), RutaContract.View,RutaContract.Adapter {

    var mListener: RutaContract.Listener? = null
    var mBar: ProgressBar? = null
    var page : Int? = 0
    var refreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    private var mTotal: TextView? = null
    private var mlLayoutSector: LinearLayout? = null
    private var adapt : RutaAdapter? = null
    private var loading : Boolean = false
    private var lastPage = false
    private var currentPage = page!!
    private var RV : androidx.recyclerview.widget.RecyclerView? =  null
    var mCount: TextView? = null

    @get:Synchronized
    var filter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_ruta, container, false)
        initViews(v)
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mListener = RutaPresenter(this,requireContext(),null)

    }

    override fun showCount(c: Int, t: Int) {
        var p = c
        if (lastPage) p = t
        mCount!!.text = "  Resultados: ${c}"
    }

    fun initViews(v: View){
        mCount = v.findViewById(R.id.tvCountRuta)
        mBar = v.find(R.id.pBarRuta)
        mlLayoutSector = v.findViewById(R.id.lLayoutSectorRuta)
        mListener!!.getConceptos()
        RV = v.findViewById(R.id.rvRuta)
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(v.context)
        layoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        RV!!.layoutManager = layoutManager


        refreshLayout = v.findViewById<View>(R.id.SwipeRuta) as androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        refreshLayout!!.setOnRefreshListener {
            lastPage = false
            page = 0
            currentPage = 0

            Log.e("Swipe","Funciona aqui")
            refreshLayout!!.isRefreshing = false

        }

        adapt = RutaAdapter(requireContext(),this)
        RV!!.adapter = adapt
        mListener = RutaPresenter(this,requireContext(),adapt!!)
        RV!!.addOnScrollListener( object : PaginationScrollListener(layoutManager){
            override fun loadMoreItems() {
                loading = true
                currentPage +=1
                mListener!!.getRuta(filter,currentPage)
                toast("${filter}")
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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = "Mi Ruta"

    }

    override fun ShowLoading(t: Boolean?) {
        mBar!!.isIndeterminate = t!!
        if (t)mBar!!.visibility = View.VISIBLE
        else mBar!!.visibility = View.GONE
    }

    override fun showConceptos(c: ArrayList<Concepto>) {
        val adapter = ArrayAdapter<Concepto>(requireContext(),android.R.layout.simple_spinner_dropdown_item,c)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRuta.adapter = adapter

        mListener!!.getRuta(0,0)

    }

    override fun showRuta(p0 : List<Ruta>?,p1 : List<String>?) {


        var mList : List<Ruta> = p0!!
        filtrarSector(mList)


        spRuta!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position) as Concepto
                filter = selectedItem.codigo!!

                when (filter){
                    0->{
                        adapt!!.clear()
                        mList = p0
                        filtrarSector(mList)
                    }
                    1-> {
                        adapt!!.clear()
                        mList = p0.filter { s -> s.balance!! >= s.mensualidad!!}
                        filtrarSector(mList)
                    }
                    2 ->{
                        adapt!!.clear()
                        mList = p0.filter { s -> s.checkVisita == 0}
                        filtrarSector(mList)
                    }
                    3 ->{
                        adapt!!.clear()
                        mList = p0.filter { s -> s.checkVisita == 1}
                        filtrarSector(mList)
                    }
                }

            }
        }




    }

    fun filtrarSector(lista: List<Ruta>){
        val p1 = lista.distinctBy { s -> s.sector }
        val p2 = p1.map { it.sector!! }

        if (!p2.isNullOrEmpty()){
            val sectores = ArrayList<String>()
            sectores.add(0,"Todos")
            sectores.addAll(p2.sorted())

            mlLayoutSector!!.visibility = View.VISIBLE


            val adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,sectores)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spSectorRuta.adapter = adapter

            var mListSector = lista
            adapt!!.clear()
            adapt!!.addAll(mListSector)
            showCount(mListSector.size,0)
            spSectorRuta.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val sector = parent?.getItemAtPosition(position) as String
                    if (position == 0){
                        adapt!!.clear()
                        mListSector = lista
                        showCount(mListSector.size,0)
                        adapt!!.addAll(mListSector)
                    }else{

                        Log.e("Sector Frg",sector)
                        adapt!!.clear()
                        mListSector = lista.filter { s -> s.sector == sector }
                        showCount(mListSector.size,0)
                        adapt!!.addAll(mListSector)
                    }

                }
            }

        }else mlLayoutSector!!.visibility = View.GONE

    }

/*     fun prepareArray(lista: ArrayList<Ruta>):ArrayList<Ruta>{
          lista.filter { s -> s.isCorte == 1 }

    }*/

    override fun showConceptosVisitas(ruta: Ruta) {

        val dialog = VisitaRuta.getInstance(ruta)
        dialog.setTargetFragment(this,200)
        dialog.isCancelable = false
        dialog.show(this.requireFragmentManager(),"accion")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 200 && resultCode == 200){

            val ruta = data!!.getSerializableExtra("ruta") as Ruta
            adapt!!.visitado(ruta)
        }
    }

    override fun showTelefonos(telefonos: ArrayList<String>) {

    }

    override fun showMap(geo: String?) {
        if(!geo.isNullOrEmpty()){

            val gmmIntentUri = Uri.parse("google.navigation:q=${geo}&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            startActivity(mapIntent)
        }else{
            toast("Ubicacion No Disponible")
        }
    }

    override fun isLoading(c: Boolean) {
        loading = c
    }

    override fun isLastPage(isLast: Boolean) {
        lastPage = isLast
    }

    override fun goDetalle(cliente: Ruta) {
        var client = Clientes()
        client.contrato = cliente.contrato
        client.cedula = cliente.cedula
        client.nombre = cliente.nombre
        client.geo = cliente.geo
        client.celular = cliente.celular
        client.telefonos = cliente.telefono
        val inte = Intent(activity, OperacionesActivity::class.java)
        val bundle : Bundle = Bundle()
        bundle.putSerializable("cliente",client)
        inte.putExtras(bundle)
        requireActivity().startActivity(inte)
    }

    override fun showCount(show: Boolean) {
        Log.e("Nitido","show")
    }
}
