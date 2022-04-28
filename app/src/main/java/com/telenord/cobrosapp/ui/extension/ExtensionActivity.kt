package com.telenord.cobrosapp.ui.extension

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import kotlinx.android.synthetic.main.activity_extension.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ExtensionActivity : AppCompatActivity(),ExtensionContract.View {

    var mCliente: Clientes? = null
    var mListener : ExtensionContract.Listener? = null
    var EtCant: EditText? = null
    var ibLock: ImageView? = null
    var mSpinner: Spinner? = null
    var mProgressBar: ProgressBar? = null
    var mPago = Pago()
    var mFacturacion = DetalleFacturacion()
    var mPrecio  = Precio()

    private val REQUEST_RESULT = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extension)
        title = "Extension"
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mListener = ExtensionPresenter(this,this)
        val bundle:Bundle? = intent.extras
        mCliente = bundle!!.getSerializable("cliente") as Clientes
        prepareView()
    }

    fun prepareView()
    {
        ibLock = find(R.id.ivLockExt)
        EtCant = find(R.id.etCantExt)
        mSpinner = find(R.id.spHorarioEX)
        mProgressBar = find(R.id.progressBarExt)
        mListener!!.getHorarios()
        ibLock!!.onClick {
            if (!EtCant!!.isEnabled){
                ibLock!!.imageResource = R.drawable.ic_unlock
                EtCant!!.isEnabled = true
            }else if(EtCant!!.isEnabled){
                ibLock!!.imageResource = R.drawable.ic_lock
                EtCant!!.setText("1")
                EtCant!!.isEnabled = false
            }
        }

    }

    override fun onResume() {
        mListener!!.getPrecio(Pago.Tipo_OP.EX,mCliente!!.contrato!!)

        super.onResume()
    }

    override fun showHorarios(lista: List<Horario>) {
        var adapter = ArrayAdapter<Horario>(this,android.R.layout.simple_spinner_dropdown_item, lista)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHorarioEX.adapter = adapter

        spHorarioEX.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val horario = parent!!.getItemAtPosition(position) as Horario
                mPago.horario = horario.descripcion
            }
        }

    }

    override fun showLoading(t: Boolean) {
       if (t){
           mProgressBar!!.isIndeterminate = true
           mProgressBar!!.visibility = View.VISIBLE
       }else if(!t){
           mProgressBar!!.isIndeterminate = false
          mProgressBar!!.visibility = View.GONE
       }
    }

    override fun showPrecio(precio: Precio)
    {
        mPrecio.cantidad = EtCant!!.text.toString().toInt()
        mPrecio.precio = precio.precio
        mPrecio.monto = mPrecio.precio!! * mPrecio.cantidad!!
        mPago.monto = mPrecio.monto
        mFacturacion = DetalleFacturacion("",precio.fecha,mPago.monto,mPago.monto,0.0,precio.fecha,"Extension")
        tvPrecioExt.text = mPrecio.precio.toString()
        tvCantExt.text = mPrecio.cantidad.toString()
        tvTotalExt.text = mPrecio.monto.toString()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.config_printer,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item!!.itemId){
           android.R.id.home ->{onBackPressed()}

           R.id.btnGuardarPrinter ->{
               if (checkMonto()) {
                mListener!!.getPrecio(Pago.Tipo_OP.EX,mCliente!!.contrato!!)
                    val monto = EtCant!!.text.toString().toInt() * mPrecio.precio!!
                    alert("¿Desea cargar $monto por extension al cliente ${mCliente!!.nombre}?", "¡Atencion!") {
                        positiveButton("Aceptar") { goPagos() }
                        negativeButton("Cancelar") {}
                    }.show()
                }
           }
       }
        return super.onOptionsItemSelected(item)
    }

    fun checkMonto(): Boolean{
        if (EtCant!!.text.isNullOrEmpty() &&  EtCant!!.isEnabled){
            EtCant!!.requestFocus()
            EtCant!!.setError("Debe Ingresar Cantidad")
            return false
        }else if (EtCant!!.text.toString().toInt() <1 &&  EtCant!!.isEnabled){
            EtCant!!.requestFocus()
            EtCant!!.setError("Cantidad Incorrecta")
            return false
        }
        else return true
    }

    fun goPagos(){

        val intent = Intent(this,PagoActivity::class.java)
        val bundle: Bundle? = Bundle()
        bundle!!.putSerializable("cliente",mCliente!!)
        bundle!!.putSerializable("facturacion",mFacturacion!!)
        intent.putExtra("cantidad",mPrecio.cantidad)
        intent.putExtra("monto_pago",mPago.monto)
        intent.putExtra("horario",mPago.horario)
        intent.putExtra("tipo_op",Pago.Tipo_OP.EX)
        intent.putExtras(bundle)
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

}
