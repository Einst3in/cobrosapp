package com.telenord.cobrosapp.ui.cambioStb.seleccion

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.telenord.cobrosapp.models.STB
import com.telenord.cobrosapp.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk27.coroutines.onClick

class SeleccionStbAdapter(val mCtx: Context, val mList: ArrayList<STB>, val mView:adapterInteraction?,val chkView: Int ): androidx.recyclerview.widget.RecyclerView.Adapter<SeleccionStbAdapter.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val v = LayoutInflater.from(mCtx).inflate(R.layout.stb_seleccion_item_layout,parent,false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.mSerie?.text = item.serie
        holder.mTarjeta?.text = item.tarjeta
        holder.mFecha?.text = item.fecha
        holder.mTipo?.text = item.tipo

        if (item.nueva>0){
            holder.mNueva!!.text = "Nueva"
        }

        holder.mCheck!!.visibility = chkView
        holder.mCheck!!.isChecked = item.marcada

        holder.mCheck!!.onClick {
                if (item.tipo == SeleccionStbFragment.tipoSelected || SeleccionStbFragment.tipoSelected == ""){
                    if (holder.mCheck!!.isChecked){
                        SeleccionStbFragment.Stbs.add(item)
                        mView!!.showCantStb(SeleccionStbFragment.Stbs.size)
                        seleccionada(item)
                    }else{
                        SeleccionStbFragment.Stbs.remove(item)
                        mView!!.showCantStb(SeleccionStbFragment.Stbs.size)
                    }
                }
                else{
                    if (holder.mCheck!!.isChecked) holder.mCheck!!.isChecked = false
                    mCtx.alert("Debe seleccionar Una STB tipo ${SeleccionStbFragment.tipoSelected}").show()
                }
            mView!!.changeType()
            if (SeleccionStbFragment.Stbs.size ==0) clearCheck()

        }
    }

    fun seleccionada(stb: STB){
        stb.marcada = true
        val position = mList!!.indexOf(stb)
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    fun clearCheck(){
        mList.map { it.marcada = false }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view){

        internal var mSerie:TextView?=null
        internal var mTarjeta: TextView? = null
        internal var mFecha: TextView?=null
        internal var mTipo:TextView? = null
        internal var mNueva: TextView?= null
        internal var mCheck:CheckBox?=null

        init {
            mSerie = view.findViewById(R.id.tvSerieCambioItem) as TextView
            mTarjeta = view.findViewById(R.id.tvTarjetaCambioItem) as TextView
            mFecha = view.findViewById(R.id.tvFechaCambioItem) as TextView
            mTipo = view.findViewById(R.id.tvTipoSTBCambioItem) as TextView
            mCheck = view.findViewById(R.id.cbCambioItem) as CheckBox
            mNueva = view.findViewById(R.id.tvNuevaCambioItem) as TextView

        }




    }
}



