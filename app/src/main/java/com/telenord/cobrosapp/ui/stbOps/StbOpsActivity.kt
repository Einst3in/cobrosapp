package com.telenord.cobrosapp.ui.stbOps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.ResponseFactura
import com.telenord.cobrosapp.models.STB
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.asignarStb.StbActivity
import com.telenord.cobrosapp.ui.cambioStb.CambioStbActivity
import com.telenord.cobrosapp.ui.cambioStb.seleccion.SeleccionStbAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class StbOpsActivity : AppCompatActivity(), StbOpsContract.View {


    lateinit var ivAdd: ImageView
    lateinit var ivCambiar: ImageView
    lateinit var ivReact: ImageView
    lateinit var rvLista: androidx.recyclerview.widget.RecyclerView
    lateinit var mCliente: Clientes
    lateinit var mListener : StbOpsContract.Listener
    lateinit var mPbar: ProgressBar
    lateinit var mCant : TextView
    private val REQUEST_RESULT = 200


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stb_ops)

        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title =  "Operaciones STB"

        mListener = StbOpsPresent(this,this)

        val bundle:Bundle? = intent.extras
        mCliente = bundle!!.getSerializable("cliente") as Clientes

        initValues()

        if (mCliente !=null && mListener != null){
            mListener.getStbs(mCliente.contrato!!)
        }


    }
    fun initValues(){
        ivAdd = find(R.id.ibStbOpsAdd)
        ivCambiar = find(R.id.ibStbOpsCamb)
        ivReact = find(R.id.ibStbOpsCamb)
        rvLista = find(R.id.rvStbLista)
        mPbar = find(R.id.pBarStbOps)
        mCant = find(R.id.tvCantOpsStb)


        ivAdd.onClick { goAdd() }
        ivCambiar.onClick { goCambiar() }

    }

    fun goAdd(){
        val intent = Intent(this, StbActivity::class.java)
        intent!!.putExtra("cliente",mCliente!!)
        startActivityForResult(intent,REQUEST_RESULT)
    }
    fun goCambiar(){
        val intent = Intent(this, CambioStbActivity::class.java)
        intent!!.putExtra("cliente",mCliente!!)
        startActivityForResult(intent,REQUEST_RESULT)
    }
    fun goReact(){

    }

    @SuppressLint("SetTextI18n")
    override fun showStbs(list: List<STB>) {
        mCant.text = "Cant: ${list.size}"
        val adapter = SeleccionStbAdapter(this, list as ArrayList<STB>,null, View.INVISIBLE)
        val manager = androidx.recyclerview.widget.LinearLayoutManager(this)
        manager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        rvLista.layoutManager = manager
        rvLista.adapter =adapter
    }

    override fun showLoading(t: Boolean) {
        mPbar.isIndeterminate = t
        if (t){
           mPbar.visibility = View.VISIBLE
        }else {
            mPbar.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                onBackPressed()
            }
        }

        return true;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RESULT && resultCode == Activity.RESULT_OK)
        {
            var factura = data!!.getSerializableExtra("factura") as ResponseFactura
            val resultIntent = intent
            resultIntent.putExtra("factura",factura)
            setResult(Activity.RESULT_OK,resultIntent)
            finish()
        }
    }

}
