package com.telenord.cobrosapp.ui.impresora.PariedDevices

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import com.telenord.cobrosapp.R
import kotlinx.android.synthetic.main.activity_paired_devices.*
import android.content.Intent
import com.telenord.cobrosapp.models.Impresora
import org.jetbrains.anko.sdk27.coroutines.onClick


class PairedDevices : AppCompatActivity() ,PairedDevicesContract.View{

    var mRecyclerView : androidx.recyclerview.widget.RecyclerView?= null
    var mAdapter :  PairedDevicesAdapter?  = null
    var printers: ArrayList<Impresora>? = null
    val REQUEST_CODE = 100


    override fun showProgress(i: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showList(p: ArrayList<Impresora>) {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.orientation =  androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        mRecyclerView!!.layoutManager = layoutManager
        mRecyclerView!!.setHasFixedSize(false)
        mAdapter = PairedDevicesAdapter(this,p,printers)
        mRecyclerView!!.adapter = mAdapter!!
    }

    internal var mListener:PairedDevicesContract.Listener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paired_devices)
        title = "Dispositivos Vinculados"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

         printers = intent.getSerializableExtra("printer") as ArrayList<Impresora>?


        mRecyclerView = PairedList as androidx.recyclerview.widget.RecyclerView
        mListener=PairedDevicesPresenter(this,this)


        ConfigBluetooth.onClick { openBluetooth() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult(REQUEST_CODE)
           finish()
        }

    }

    override fun onResume() {
        super.onResume()
        mListener!!.getList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {onBackPressed()
            return true}
        }
        return super.onOptionsItemSelected(item)
    }

    fun openBluetooth(){
        val intentOpenBluetoothSettings = Intent()
        intentOpenBluetoothSettings.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
        startActivity(intentOpenBluetoothSettings)
    }
}
