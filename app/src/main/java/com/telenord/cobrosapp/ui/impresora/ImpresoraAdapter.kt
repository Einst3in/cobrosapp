package com.telenord.cobrosapp.ui.impresora

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.R
import org.jetbrains.anko.sdk27.coroutines.onClick

class ImpresoraAdapter(mCtx:Context,mList:ArrayList<Impresora>, mView : ImpresoraContract.View): androidx.recyclerview.widget.RecyclerView.Adapter<ImpresoraAdapter.PrintHolder>(){
    var mCtx:Context?=null
    var mList:ArrayList<Impresora>?=null
    var mView: ImpresoraContract.View? = null
    init {
        this.mCtx=mCtx
        this.mList=mList
        this.mView = mView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrintHolder {
        var v = LayoutInflater.from(mCtx).inflate(R.layout.printer_item_layout,parent,false)
        var vh : PrintHolder?=PrintHolder(v)
        return vh!!
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: PrintHolder, position: Int) {
        var P:Impresora?=mList!!.get(position)
        holder.mModel!!.text= P!!.modelo
        holder.mMac!!.text = P!!.mac
        holder.mConfigure!!.onClick { mView!!.setDefault(P) }
        holder.mDelete!!.onClick { mView!!.showAlertDelete(P) }
    }


    inner class PrintHolder(v: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(v){
        internal var mModel:TextView?=null
        internal var mConfigure:ImageView?=null
        internal var mLayout: androidx.cardview.widget.CardView?=null
        internal var mMac : TextView? = null
        internal var mDelete : ImageView? = null
        internal var mConf : ImageView? = null

        init {
            mLayout = v.findViewById(R.id.Layout)
            mModel= v.findViewById(R.id.Model)
            mConfigure = v.findViewById(R.id.Configure)
            mMac = v.findViewById(R.id.tvMacPrinterList)
            mDelete = v.findViewById(R.id.ivDeletePrinter)

        }
    }
}