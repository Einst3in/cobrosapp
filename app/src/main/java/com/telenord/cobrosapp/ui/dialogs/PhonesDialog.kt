package com.telenord.cobrosapp.ui.dialogs


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.telenord.cobrosapp.models.Clientes

import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.editarTelefono.EditarTelefonoActivity
import org.jetbrains.anko.find
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity

import java.util.ArrayList

class PhonesDialog : androidx.fragment.app.DialogFragment() {

    private var mList: ListView? = null
    private var mAdapter: ArrayAdapter<String>? = null
    var btnTelefono1: Button? = null
    var btnTelefono2 : Button? = null
    lateinit var btnAddNumero : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mLayout = inflater.inflate(R.layout.dialog_phone,container,false)

        dialog!!.setTitle("Telefonos")

        initViews(mLayout)
        return mLayout
    }

    fun initViews(v: View){
        btnTelefono1 = v.find(R.id.btnTelefono1)
        btnTelefono2 = v.find(R.id.btnTelefono2)
        btnAddNumero = v.find(R.id.btnAddTelefono)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnAddNumero.onClick {
            this@PhonesDialog.startActivity<EditarTelefonoActivity>("cliente" to mCliente ) }
        when (mArr!!.size){
            1->{
                btnTelefono1!!.visibility  = View.VISIBLE
                btnTelefono1!!.text = mArr!![0]
                btnTelefono1!!.onClick { mCtx!!.makeCall(mArr!![0])
                    dialog!!.dismiss()}
            }
            2 -> {
                btnTelefono1!!.visibility  = View.VISIBLE
                btnTelefono2!!.visibility  = View.VISIBLE
                btnTelefono1!!.text = mArr!![0]
                btnTelefono2!!.text = mArr!![1]
                btnTelefono1!!.onClick { mCtx!!.makeCall(mArr!![0])
                dialog!!.dismiss()}
                btnTelefono2!!.onClick { mCtx!!.makeCall(mArr!![1])
                    dialog!!.dismiss()}
            }
        }
    }

    companion object {
        private var mArr: ArrayList<String>? = null
        private var mCliente =  Clientes()
        private var mCtx : Context? = null
        fun getInstance(phones: ArrayList<String>, cliente:Clientes,context: Context): PhonesDialog {
            mArr = phones
            mCliente = cliente
            mCtx = context
            return PhonesDialog()
        }
    }
}
