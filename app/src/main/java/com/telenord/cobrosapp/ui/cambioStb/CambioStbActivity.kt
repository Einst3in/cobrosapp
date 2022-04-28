package com.telenord.cobrosapp.ui.cambioStb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.cambioStb.seleccion.SeleccionStbFragment
import com.telenord.cobrosapp.ui.cambioStb.seleccion.SeleccionStbFragment.Companion.Stbs
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivityForResult
import kotlin.math.log

class CambioStbActivity : AppCompatActivity() {



    lateinit var mCliente: Clientes
    lateinit var mContainer : FrameLayout
    private val REQUEST_RESULT = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambio_stb)

        title = "Cambio STB"
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initValues()
        mCliente = intent.extras?.getSerializable("cliente") as Clientes

        SeleccionStbFragment.mCliente = mCliente

        if (fragmentLoaded==null){
            MostrarFragment(SeleccionStbFragment())
            fragmentLoaded = fSeleccion
        }

    }

    fun initValues(){
        mContainer = find(R.id.flCambioStb)
    }

    fun MostrarFragment(fragment: androidx.fragment.app.Fragment){
        val manager = supportFragmentManager
        val transition= manager.beginTransaction()
        transition.replace(R.id.flCambioStb,fragment)
        transition.commit()

    }

    fun goPagos(mCliente: Clientes?,mPago: Pago,mFacturacion: DetalleFacturacion){
        val intent:Intent = Intent(this,PagoActivity::class.java)
        val bundle: Bundle? = Bundle()
        bundle!!.putSerializable("cliente",mCliente!!)
        bundle!!.putSerializable("facturacion",mFacturacion)
        bundle.putSerializable("pago",mPago.apply { opc_cargo = Pago.Tipo_OP.CS })

        /*intent!!.putExtra("cambio_stb",mPago.cambio_stb)
        intent!!.putExtra("horario",mPago.horario)
        intent!!.putExtra("monto_pago",mPago.monto)
        intent!!.putExtra("tipo",mPago.tipo)
        intent!!.putExtra("tipo_op", Pago.Tipo_OP.CS)
        intent!!.putExtra("cantidad",mPago.cantidad)
        intent!!.putExtra("plan",mPago.plan)
        intent!!.putExtra("cargo",mPago.cargo)*/
        intent!!.putExtras(bundle)
        startActivityForResult(intent,REQUEST_RESULT)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId){
            android.R.id.home -> {
                if (fragmentLoaded == fCambio){MostrarFragment(SeleccionStbFragment())
                    item!!.setIcon(R.drawable.ic_next_white)
                    fragmentLoaded = fSeleccion
                }
                else {
                    Stbs.removeAll(Stbs)
                    finish()}
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        fragmentLoaded = null
    }

companion object{
    var fragmentLoaded : Int? = null
    val fSeleccion=  1
    val fCambio = 2
}


}
