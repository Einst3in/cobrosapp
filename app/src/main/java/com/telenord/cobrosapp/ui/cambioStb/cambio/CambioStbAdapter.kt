package com.telenord.cobrosapp.ui.cambioStb.cambio

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.telenord.cobrosapp.models.Horario
import com.telenord.cobrosapp.models.PrecioSTB
import com.telenord.cobrosapp.models.STB
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.cambioStb.seleccion.SeleccionStbFragment
import org.jetbrains.anko.sdk27.coroutines.onItemSelectedListener

class CambioStbAdapter(val mCtx: Context,val mList:ArrayList<STB> ,val mView: adapterInteractionCambio): androidx.recyclerview.widget.RecyclerView.Adapter<CambioStbAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewGroup: Int): ViewHolder {
        val v= LayoutInflater.from(mCtx).inflate(R.layout.stb_cambio_item_layout,parent,false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.mTipo.text = item.tipo
        holder.mSerie.text = item.serie
        holder.mPrecio.text = "$ ${item.precioCambio}"
    }

class ViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view){

    internal  var mSerie:TextView
    internal  var mTipo: TextView
    internal var mPrecio :TextView

    init {
        mSerie = view.findViewById(R.id.tvSerieCambioSTB) as TextView
        mTipo = view.findViewById(R.id.tvTipoStbCambio) as TextView
        mPrecio = view.findViewById(R.id.tvPrecioCambioStbItem) as TextView
    }
}
}