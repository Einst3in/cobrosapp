package com.telenord.cobrosapp.ui.historial

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.telenord.cobrosapp.models.Movimientos
import com.telenord.cobrosapp.R
import kotlinx.android.synthetic.main.movimiento_item.view.*

class HistorialAdapter (mCxt : Context, mList:ArrayList<Movimientos>): androidx.recyclerview.widget.RecyclerView.Adapter<HistorialAdapter.ViewHolder>(){
    var mList:ArrayList<Movimientos>?=mList
    var mCxt : Context?=mCxt

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(mCxt).inflate(R.layout.movimiento_item,parent,false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p =  mList!![position]

        holder.mTipo!!.text = "${p.tipo}"
        holder.mFecha!!.text = "${p.fecha}"
        holder.mConcepto!!.text = "${p.concepto}"
        holder.mDebito!!.text = "${p.debito!!.toDouble()}"
        holder.mBalance!!.text = "${p.balance!!.toDouble()}"
        holder.mCredito!!.text = "${p.credito!!.toDouble()}"

    }


    class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        internal var mTipo: TextView?= null
        internal var mFecha: TextView?=null
        internal var mConcepto: TextView?=null
        internal var mDebito: TextView?=null
        internal var mBalance: TextView?=null
        internal var mCredito: TextView?=null

        init {
            mTipo = view.tvMovTipo
            mFecha = view.tvMovFecha
            mConcepto= view.tvMovConcepto
            mDebito = view.tvMovDebito
            mBalance = view.tvMovBalance
            mCredito = view.tvMovCredito
        }

    }

}