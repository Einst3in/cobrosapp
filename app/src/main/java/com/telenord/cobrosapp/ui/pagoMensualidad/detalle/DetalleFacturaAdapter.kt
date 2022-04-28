package com.telenord.cobrosapp.ui.pagoMensualidad.detalle

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.telenord.cobrosapp.models.DetalleFacturacion
import com.telenord.cobrosapp.R
import kotlinx.android.synthetic.main.layout_detalle_facturacion.view.*

class DetalleFacturaAdapter(mCxt : Context,mList:ArrayList<DetalleFacturacion>): androidx.recyclerview.widget.RecyclerView.Adapter<DetalleFacturaAdapter.ViewHolder>(){
    var mList:ArrayList<DetalleFacturacion>?=mList
    var mCxt : Context?=mCxt

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(mCxt).inflate(R.layout.layout_detalle_facturacion,parent,false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p =  mList!![position]

        holder.mNumero!!.text = "${p.numero}"
        holder.mFecha!!.text = "${p.fecha}"
        holder.mConcepto!!.text = "${p.concepto}"
        holder.mPagado!!.text = "${p.pagado!!.toDouble()}"
        holder.mBalance!!.text = "${p.balance!!.toDouble()}"
        holder.mMontoGeneral!!.text = "${p.monto!!.toDouble()}"
        holder.mFechaVenc!!.text = "${p.fechaVencimiento}"

    }


    class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        internal var mNumero: TextView ?= null
        internal var mFecha: TextView ?=null
        internal var mConcepto: TextView?=null
        internal var mPagado: TextView?=null
        internal var mBalance: TextView?=null
        internal var mMontoGeneral: TextView?=null
        internal var mFechaVenc: TextView?  = null

        init {
          mNumero = view.tvNumeroDetFact
            mFecha = view.tvFechaDetFact
            mNumero = view.tvNumeroDetFact
            mConcepto = view.tvConceptoDetFact
            mPagado = view.tvPagadoDetFact
            mBalance = view.tvBalanceDetFact
            mMontoGeneral = view.tvMontoDetFact
            mFechaVenc = view.tvFechaVenc

        }

    }

}