package com.telenord.cobrosapp.ui.cuadre

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.telenord.cobrosapp.models.CuadreDetalle
import com.telenord.cobrosapp.R

class CuadreAdapter(private val mList: ArrayList<CuadreDetalle>): androidx.recyclerview.widget.RecyclerView.Adapter<CuadreAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = mList[position]
        holder.fecha.text = p.fecha
        holder.indice.text = "${position + 1}"
        holder.contrato.text = p.contrato
        holder.monto.text = "${p.monto!!.toDouble()}"
        holder.recibo.text = p.recibo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cuadre_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
    return mList.size
    }



    inner class ViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){


        val indice :TextView
        val contrato:TextView
        val recibo:TextView
        val fecha:TextView
        val monto:TextView

        init {
            contrato = itemView.findViewById<View>(R.id.tvContCuad) as TextView
            indice = itemView.findViewById<View>(R.id.tvIndiceCuad) as TextView
            recibo =itemView.findViewById<View>(R.id.tvReciboCuad) as TextView
            fecha = itemView.findViewById<View>(R.id.tvFechaCuad) as TextView
            monto = itemView.findViewById<View>(R.id.tvMontoCuad) as TextView
        }
    }
}