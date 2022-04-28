package com.telenord.cobrosapp.ui.clientes

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.util.ConnectionCheck

import java.util.ArrayList

class PaginationAdapter(private val context: Context, private val mAdapterListener: Clientes_Contract.Adapter) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    private var clientesList: MutableList<Clientes>? = null

    private var isLoadingAdded = false
    private var retryPageLoad = false


    private var errorMsg: String? = null

    var movies: MutableList<Clientes>?
        get() = clientesList
        set(movieResults) {
            this.clientesList = movieResults
        }

    val isEmpty: Boolean
        get() = itemCount == 0

    init {

        clientesList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.layout_clientes_list, parent, false)
                viewHolder = ClienteVH(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.progress_item, parent, false)
                viewHolder = LoadingVH(viewLoading)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val cliente = clientesList!![position]

        when (getItemViewType(position)) {

            ITEM -> {
                val clienteVH = holder as ClienteVH

                clienteVH.contrato.text = cliente.contrato
                clienteVH.nombreCliente.text = cliente.nombre
                clienteVH.direccionCliente.text = cliente.direccion
                clienteVH.estatus.text = cliente.statusDescripcion
                clienteVH.cedulaCliente.text = cliente.cedula

                val stado = cliente.status!!
                when (stado) {
                    2,9,16,20,21 -> clienteVH.estatus.setTextColor(context.resources.getColor(R.color.primary))

                    4,5,11,12,13 -> clienteVH.estatus.setTextColor(context.resources.getColor(R.color.desconectado))

                    1,3,7,10 -> clienteVH.estatus.setTextColor(context.resources.getColor(R.color.accent))
                }
/*
                var lat : Double = 0.0
                var lng : Double = 0.0
                holder.mMap.imageResource = R.drawable.ic_location_off
                holder.mMap.onClick {this@PaginationAdapter.context.toast("Ubicacion No Disponible")}


                if(cliente.geo != null){
                    holder.mMap.imageResource = R.drawable.ic_location_on

                    holder.mMap.onClick {

                        val gmmIntentUri = Uri.parse("google.navigation:q=${cliente.geo}&mode=d")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.`package` = "com.google.android.apps.maps"
                        this@PaginationAdapter.context.startActivity(mapIntent)
                    }
                }
                holder.mPhones.imageResource = R.drawable.ic_phone_locked
                if (!cliente.celular.isNullOrEmpty() || !cliente.telefonos.isNullOrEmpty()){
                    holder.mPhones.imageResource = R.drawable.ic_phone
                }
                holder?.mPhones.onClick{
                    if (!cliente.celular.isNullOrEmpty()) {
                        this@PaginationAdapter.context.makeCall(cliente.celular)
                    }else if (!cliente.telefonos.isNullOrEmpty()){
                        this@PaginationAdapter.context.makeCall(cliente.telefonos)
                    }
                    else {this@PaginationAdapter.context.longToast("Numero No Disponible")}
                }*/



                clienteVH.cardView.setOnClickListener {
                    if (ConnectionCheck(context).isConnectingToInternet()){
                        mAdapterListener.goDetalle(cliente)
                    }

                }
            }

            LOADING -> {
                val loadingVH = holder as LoadingVH

                if (retryPageLoad) {

                    loadingVH.mProgressBar.visibility = View.GONE


                } else {
                    loadingVH.mProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (clientesList == null) 0 else clientesList!!.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == clientesList!!.size - 1 && isLoadingAdded) LOADING else ITEM

    }


    fun add(r: Clientes) {
        clientesList!!.add(r)
        notifyItemInserted(clientesList!!.size - 1)
    }

    fun addAll(moveResults: List<Clientes>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun remove(r: Clientes?) {
        val position = clientesList!!.indexOf(r)
        if (position > -1) {
            clientesList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
        mAdapterListener.showCount(false)
        notifyDataSetChanged()
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Clientes())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = clientesList!!.size - 1
        val result = getItem(position)

        if (result != null) {
            clientesList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Clientes? {
        return clientesList!![position]
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    fun showRetry(show: Boolean, errorMsg: String?) {
        retryPageLoad = show
        notifyItemChanged(clientesList!!.size - 1)

        if (errorMsg != null) this.errorMsg = errorMsg
    }


    /**
     * Main list's content ViewHolder
     */
    protected inner class ClienteVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val contrato: TextView
        val estatus: TextView
        val nombreCliente: TextView
        val cedulaCliente: TextView
        val direccionCliente: TextView
        val cardView: androidx.cardview.widget.CardView


        init {

            cardView = itemView.findViewById<View>(R.id.CardViewClientes) as androidx.cardview.widget.CardView
            contrato = itemView.findViewById<View>(R.id.listviewclientes_item_Contrato) as TextView
            estatus = itemView.findViewById<View>(R.id.listviewclientes_item_estatus_escrito) as TextView
            nombreCliente = itemView.findViewById<View>(R.id.listviewclientes_item_NombreCliente) as TextView
            cedulaCliente = itemView.findViewById<View>(R.id.listviewclientes_item_CedulaCliente) as TextView
            direccionCliente = itemView.findViewById<View>(R.id.listviewclientes_item_DireccionCliente) as TextView


        }
    }


    protected inner class LoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
         val mProgressBar: ProgressBar


        init {

            mProgressBar = itemView.findViewById<View>(R.id.loadingAdapter) as ProgressBar

        }


    }

    companion object {

        // View Types
        private val ITEM = 0
        private val LOADING = 1
    }

}
