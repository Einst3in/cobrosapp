package com.telenord.cobrosapp.ui.ruta

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.telenord.cobrosapp.models.Ruta
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.util.ConnectionCheck
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.ArrayList

class RutaAdapter (private val context: Context, private val mAdapterListener: RutaContract.Adapter) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    private var clientesList: MutableList<Ruta>? = null

    private var isLoadingAdded = false
    private var retryPageLoad = false


    private var errorMsg: String? = null

    var clientes: MutableList<Ruta>?
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
                val viewItem = inflater.inflate(R.layout.ruta_item, parent, false)
                viewHolder = RutaVH(viewItem)
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
                val clienteVH = holder as RutaVH

                clienteVH.contrato.text = "${cliente.contrato}"
                clienteVH.nombreCliente.text = "${cliente.nombre}"
                clienteVH.direccionCliente.text = "${cliente.direccion}"
                clienteVH.ultimoPago.text = "${cliente.fechaUltimoPago}"
                clienteVH.ultimaVisita.text = "${cliente.ultimaVisita}"
                clienteVH.mensualidad.text = "${cliente.mensualidad}"
                clienteVH.balance.text = "${cliente.balance}"


                if(cliente.geo.isNullOrEmpty()){
                    clienteVH.location.imageResource = R.drawable.ic_location_off
                }
                clienteVH.location.onClick { mAdapterListener.showMap(cliente.geo) }

                val stado = if (cliente.balance!! >= cliente.mensualidad!!) 1 else 0
                when (stado) {
                    0 -> clienteVH.estatus.visibility = View.GONE

                    1 -> clienteVH.estatus.visibility = View.VISIBLE

                    else -> clienteVH.estatus.visibility = View.GONE
                }

                if (cliente.celular.isNullOrEmpty() && cliente.telefono.isNullOrEmpty()){
                    clienteVH.telefono.imageResource = R.drawable.ic_phone_locked
                }else{
                    clienteVH.telefono.imageResource = R.drawable.ic_phone
                }

                clienteVH.telefono.onClick {
                    val telefonos = ArrayList<String>()
                    if (!cliente.celular.isNullOrEmpty())telefonos.add(cliente.celular!!)
                    if (!cliente.telefono.isNullOrEmpty())telefonos.add(cliente.telefono!!)
                    mAdapterListener.showTelefonos(telefonos)
                }

                when (cliente.checkVisita){
                    0->{clienteVH.visitado.text = "No Visitado"
                        clienteVH.visitado.setTextColor(context.resources.getColor(R.color.desconectado))
                        clienteVH.checkB.isChecked = false
                        clienteVH.checkB.isClickable = true
                        clienteVH.checkB.onClick {
                         /*   val dialog = VisitaRuta.getInstance(cliente)
                            dialog.isCancelable = false
                            dialog.show((this@RutaAdapter.context as AppCompatActivity).supportFragmentManager ,"accion")*/
                            clienteVH.checkB.isChecked = false
                            mAdapterListener.showConceptosVisitas(cliente)

                        }}
                    1->{clienteVH.visitado.text = "Visitado"
                        clienteVH.checkB.isChecked = true
                        clienteVH.checkB.isClickable = false
                        clienteVH.visitado.setTextColor(context.resources.getColor(R.color.primary_dark))}
                }

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


    fun add(r: Ruta) {
        clientesList!!.add(r)
        notifyItemInserted(clientesList!!.size - 1)
    }

    fun addAll(moveResults: List<Ruta>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun remove(r: Ruta?) {
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
        add(Ruta())
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

    fun visitado(r: Ruta){
        r.checkVisita = 1
        val position = clientesList!!.indexOf(r)
        notifyItemChanged(position)
        notifyDataSetChanged()

    }
    fun getItem(position: Int): Ruta? {
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
    protected inner class RutaVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val contrato: TextView
        val estatus: TextView
        val nombreCliente: TextView
        val direccionCliente: TextView
        val ultimoPago: TextView
        val ultimaVisita: TextView
        val mensualidad: TextView
        val balance: TextView
        val visitado: TextView
        val cardView: androidx.cardview.widget.CardView
        val location: ImageView
        val telefono: ImageView
        val checkB: CheckBox


        init {

            cardView = itemView.findViewById<View>(R.id.CardViewRuta) as androidx.cardview.widget.CardView
            contrato = itemView.findViewById<View>(R.id.tvContratoRuta) as TextView
            estatus = itemView.findViewById<View>(R.id.tvEstadoRuta) as TextView
            nombreCliente = itemView.findViewById<View>(R.id.tvNombreRuta) as TextView
            direccionCliente = itemView.findViewById<View>(R.id.tvDireccionRuta) as TextView
            mensualidad = itemView.findViewById<View>(R.id.tvMensualidadRuta) as TextView
            balance = itemView.findViewById<View>(R.id.tvBalanceRuta) as TextView
            ultimaVisita = itemView.findViewById<View>(R.id.tvFechaVisitaRuta) as TextView
            ultimoPago = itemView.findViewById<View>(R.id.tvFechaPagoRuta) as TextView
            visitado = itemView.findViewById<View>(R.id.tvVisitadoRuta) as TextView
            location = itemView.findViewById<View>(R.id.ivLocationRuta) as ImageView
            telefono = itemView.findViewById<View>(R.id.ivTelefonoRuta) as ImageView
            checkB = itemView.findViewById<View>(R.id.chkBoxRuta) as CheckBox


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