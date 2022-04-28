package com.telenord.cobrosapp.ui.pagoAdelantado

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert

class PagoAdelantadoActivity : AppCompatActivity(), PagoAdelantadoContract.View {

    var spMeses : Spinner? = null
    var btnCalcular: Button? = null
    var mListener: PagoAdelantadoContract.Listener? = null
    var flDetalle : FrameLayout? = null
    var tvMensualidad : TextView? = null
    var tvPorciento : TextView? = null
    var tvTotal : TextView? = null
    var tvDescuento : TextView? = null
    var tvNeto : TextView? = null
    var mContrato : String? = null
    var mCliente: Clientes? = null
    var mPagoAdelantado : PagoAdelantado? = null
    var mPago = Pago()
    var REQUEST_RESULT = 200
    var mProgressBar: ProgressBar?= null
    var mFacturacion = DetalleFacturacion()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_adelantado)

        mListener = PagoAdelantadoPresenter(this,this)
        title="Pago por adelantado"
        val bundle:Bundle? = intent.extras
        mCliente = bundle!!.getSerializable("cliente") as Clientes
        mContrato = mCliente!!.contrato
        prepareView()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    fun prepareView(){
        spMeses = find(R.id.spMeses)
        btnCalcular = find(R.id.btnCalcularDesc)
        flDetalle = find(R.id.flPagoAde)
        mProgressBar = find(R.id.pBarAdelantado)
        tvMensualidad = find(R.id.tvMensualidad)
        tvPorciento = find(R.id.tvPorciento)
        tvTotal = find(R.id.tvTotal)
        tvDescuento = find(R.id.tvDescuento)
        tvNeto = find(R.id.tvNeto)

        val meses = ArrayList<String>()
        for (a in 6..12){
            meses.add("$a Meses")
        }
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,meses)

        spMeses!!.adapter = adapter
        btnCalcular!!.onClick {

            var split = spMeses!!.selectedItem.toString().split(" ")
            var meses = split[0].toInt()
            mListener!!.getValor(mContrato!!,meses)

            spMeses!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent!!.getItemAtPosition(position).toString()
                    val split = selectedItem.split(" ")
                    val meses = split[0].toInt()

                    mListener!!.getValor(mContrato!!,meses)

                }
            }
        }


    }

    override fun showLoading(t: Boolean) {
        if (t) mProgressBar!!.visibility = View.VISIBLE else mProgressBar!!.visibility = View.GONE
        mProgressBar!!.isIndeterminate = t
    }

    override fun showValor(pago: PagoAdelantado) {
        mPagoAdelantado = pago
        flDetalle!!.visibility = View.VISIBLE
        tvMensualidad!!.text = "${pago.mensualidad}"
        tvDescuento!!.text = "${pago.descuento}"
        tvPorciento!!.text = "${pago.porciento} %"
        tvNeto!!.text  = "${pago.neto}"
        tvTotal!!.text = "${pago.total}"
        mFacturacion = DetalleFacturacion("",pago.fecha,pago.neto,pago.neto,0.0,pago.fecha,"PAGO ADELANTADO ${pago.meses} MESES")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.config_printer,menu)
            return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId)
        {
            android.R.id.home -> {onBackPressed()}

            R.id.btnGuardarPrinter ->
            {
                if (mCliente!!.balance >0){
                    alert("El cliente no debe tener balance pendiente","No se puede hacer el pago"){
                        positiveButton("Aceptar"){}
                        negativeButton("Cancelar"){}
                    }.show()
                }else {
                    if (mPagoAdelantado == null) {
                        alert("Debe calcular el pago por adelantado", "No puede procesar pago") {
                            positiveButton("Aceptar") { btnCalcular!!.performClick() }
                            negativeButton("Cancelar") {}
                        }.show()
                    } else {
                        alert("¿Desea cargar ${mPagoAdelantado!!.neto} por pago por adelantado al cliente ${mCliente!!.nombre}?", "¡Atencion!") {
                            positiveButton("Aceptar") { goPagos(mPagoAdelantado!!) }
                            negativeButton("Cancelar") {}
                        }.show()
                    }
                }

            }

        }
        return super.onOptionsItemSelected(item)
    }
    //goPagos(mPagoAdelantado!!.meses!!, mPagoAdelantado!!.neto!!, mPagoAdelantado!!.descuento!!, mPagoAdelantado!!.porciento!!.toDouble())
    //fun goPagos(cant: Int,monto: Double,descuento: Double,porciento:Double){
        fun goPagos(pago: PagoAdelantado){

        mPago.apply {
            cantidad = pago.meses
            cargo = pago.neto
            monto = pago.neto
            descuento = pago.descuento
            porciento = pago.porciento
            opc_cargo = Pago.Tipo_OP.PA
        }

        val intent = Intent(this, PagoActivity::class.java)
        val bundle: Bundle? = Bundle()
        bundle!!.putSerializable("cliente",mCliente!!)
        bundle!!.putSerializable("facturacion",mFacturacion!!)
        bundle.putSerializable("pago",mPago)
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
}
