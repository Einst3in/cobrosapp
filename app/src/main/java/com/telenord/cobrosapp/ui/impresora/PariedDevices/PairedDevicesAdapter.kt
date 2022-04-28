package com.telenord.cobrosapp.ui.impresora.PariedDevices

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.impresora.configPrinter.ConfigPrinter
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onClick


class PairedDevicesAdapter(private val mCtx: Context, private val mList : ArrayList<Impresora>, private val mPrinters : ArrayList<Impresora>?): androidx.recyclerview.widget.RecyclerView.Adapter<PairedDevicesAdapter.ViewHolder>() {

val REQUEST_CODE = 100
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewItem = inflater.inflate(R.layout.dispositivo_vinculado_item,parent,false)
        return ViewHolder(viewItem)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = mList[position]
        holder.modelo.text = p.modelo
        holder.mac_address.text = p.mac

                if (mPrinters!=null){
           val f = mPrinters.find { impresora -> impresora.mac == p.mac  }
            if (f == null ){

                holder.ivTipo.imageResource = R.drawable.ic_settings_black_24dp
                holder.ivStatus.imageResource = R.drawable.ic_next
                holder.cardView.onClick {
                    val intent = Intent(mCtx,ConfigPrinter::class.java)
                    intent.putExtra("printer" , p)
                    (mCtx as Activity).startActivityForResult(intent,REQUEST_CODE)

                }}}

    }

   class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        val modelo: TextView
        val mac_address: TextView
        val cardView: androidx.cardview.widget.CardView
        val ivTipo: ImageView
       val ivStatus: ImageView
        init {
            modelo = itemView.findViewById(R.id.tvNombrePairedDevice)
            mac_address = itemView.findViewById(R.id.tvMacPairedDevice)
            cardView= itemView.findViewById(R.id.cvPairedDevice)
            ivTipo = itemView.findViewById(R.id.ivPairedDevice)
            ivStatus = itemView.findViewById(R.id.ivStatusPairedDevice)
        }
    }

}