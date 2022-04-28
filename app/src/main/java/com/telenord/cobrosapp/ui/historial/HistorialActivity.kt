package com.telenord.cobrosapp.ui.historial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.MovimientosResponse
import com.telenord.cobrosapp.R
import kotlinx.android.synthetic.main.activity_historial.*
import org.jetbrains.anko.find

class HistorialActivity : AppCompatActivity(),HistorialContract.View {


    var mCliente: Clientes? = null
    var mListener: HistorialContract.Listener? = null
    var mRecyclerView: androidx.recyclerview.widget.RecyclerView? = null
    var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        title = "Historial"
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        mListener = HistorialPresenter(this,this)
        val bundle:Bundle? = intent.extras
        mCliente = bundle!!.getSerializable("cliente") as Clientes
        prepareView()
        mListener!!.getMovimientos(mCliente!!.contrato!!)

    }
    fun prepareView(){
        mRecyclerView = find(R.id.rvMovimientos)
        progressBar = find(R.id.progressHistorial)
    }

    override fun showMovimientos(movimientos: MovimientosResponse) {
        tvMovimientos.text = "${movimientos.cantidad}"
        val adapter = HistorialAdapter(this,movimientos.movimientos!!)
        val manager = androidx.recyclerview.widget.LinearLayoutManager(this)
        manager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        mRecyclerView!!.layoutManager = manager
        mRecyclerView!!.adapter = adapter }

    override fun showLoading(t:Boolean) {
        progressBar!!.isIndeterminate = t
        if (t){
            progressBar!!.visibility = View.VISIBLE
        }else {
            progressBar!!.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                onBackPressed()
            }
        }
        return true
    }
}
