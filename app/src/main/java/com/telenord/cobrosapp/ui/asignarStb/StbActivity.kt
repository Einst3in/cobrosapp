package com.telenord.cobrosapp.ui.asignarStb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ProgressBar
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import kotlinx.android.synthetic.main.activity_stb.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find

class StbActivity : AppCompatActivity(),StbContract.View {

    var mListener: StbContract.Listener? = null
    var mCliente: Clientes? = null
    var mProgressBar: ProgressBar? = null
    val REQUEST_RESULT = 200
    var mPago = Pago()
    lateinit var mChk : CheckBox
    lateinit var selectedEquipo: TipoEquipo
    var mFacturacion = DetalleFacturacion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stb)

        title = "Asignar STB"
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mPago.opc_cargo = Pago.Tipo_OP.SB
        mListener = StbPresenter(this,this)
        val bundle:Bundle? = intent.extras
        mCliente = bundle!!.getSerializable("cliente") as Clientes

        prepareView()

        mListener!!.getTipos(mCliente!!.contrato!!)
        mListener!!.getStbContrato(mCliente!!.contrato!!)
    }

    fun prepareView(){
        mProgressBar = find(R.id.pBarStb)
        mChk = find(R.id.cbExt)
        mChk.isChecked = true
        mChk.isEnabled = false

        mChk.setOnCheckedChangeListener { buttonView, isChecked ->
           if (isChecked){
               mListener!!.getPrecioExt(mCliente!!.contrato!!)
           }
           else {
               lLayoutExtStb.visibility = View.GONE
               mPago.extension = 0
               mListener!!.getTipos(mCliente!!.contrato!!)
           }
       }
    }

    override fun showStbContrato(stb: ArrayList<STB>) {
        if (stb.size <=0 || mCliente!!.status == 1){
            mChk.isChecked = false
            mChk.isEnabled = false

        }
    }

    override fun showPrecioExt(precio: Precio) {
        lLayoutExtStb.visibility = View.VISIBLE
        tvPrecioExt.text = "${precio.precio}"
        mPago.cargo =mPago.cargo!! + (precio.precio!!* mPago.cantidad!!)
        mFacturacion.monto = mPago.cargo!!
        mFacturacion.concepto = "${mFacturacion.concepto}, EXTENSION"
        tvMontoStb.text = "${mPago.cargo}"
        mFacturacion.balance = mFacturacion.monto
        mPago.extension = mPago.cantidad!!
    }
    fun String.toMoney():String{
        return "RD$ ${this}"
    }
    override fun showTipos(list: ArrayList<TipoEquipo>) {
        val element = TipoEquipo().apply { descripcion = "Seleccione Tipo de STB" }
        list.add(0,element)
       val adapter = ArrayAdapter<TipoEquipo>(this,android.R.layout.simple_spinner_dropdown_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoStb.adapter = adapter

        spTipoStb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position) as TipoEquipo
                selectedEquipo = selectedItem
                if (!selectedItem.codigo.isNullOrEmpty()){
                    mPago.tipo = selectedItem.tipo
                    mPago.cod_serv = selectedEquipo.codigo
                    mListener!!.getCantidad(selectedItem.tipo!!)
                }
            }
        }

    }

    override fun showLoading(t: Boolean) {
        if (t){
            mProgressBar!!.isIndeterminate = true
            mProgressBar!!.visibility = View.VISIBLE}
        else if (!t){
            mProgressBar!!.isIndeterminate = false
            mProgressBar!!.visibility = View.GONE
        }
    }

    override fun showCantidad(c: Int,tipo:String) {
        var p = c
        if (c>5) p = 5
        val adapter = ArrayAdapter<Int>(this,android.R.layout.simple_spinner_dropdown_item,(1..p).toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCantStb.adapter = adapter

        if (c>0){

            lLayoutStb.visibility = View.VISIBLE
            spCantStb.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent!!.getItemAtPosition(position) as Int
                    mPago.cantidad = selectedItem

                    val precio= Precio().apply{
                        precio = selectedEquipo.precio
                        cantidad = selectedItem
                        monto = (precio!! *cantidad!!)
                        descripcion = selectedEquipo.descripcion
                    }
                    showPrecios(precio)
                    //mListener!!.getPrecios(mCliente!!.contrato!!,tipo,selectedItem)

                }
            }
        }else{

            mPago.cantidad = null
            lLayoutStb.visibility = View.GONE

            alert("Selecciona otro tipo","No hay STB ${spTipoStb.selectedItem} disponible"){
                positiveButton("Aceptar"){

                    spTipoStb.setSelection(0)
                }
                negativeButton("Cancelar"){}
            }.show()
        }

    }

    override fun showPrecios(precio: Precio) {
        tvCantStb.text = "${precio.cantidad}"
        tvMontoStb.text = "${precio.monto}"
        tvPrecioStb.text = "${precio.precio}"
        mPago.cargo = precio.monto
        mPago.desc_serv = precio.descripcion
        mFacturacion = DetalleFacturacion("","",mPago.cargo,mPago.cargo,0.0,"", "${precio.cantidad} ${precio.descripcion}")
        if (mChk.isChecked) mListener!!.getPrecioExt(mCliente!!.contrato!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.config_printer,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                onBackPressed()
            }
            R.id.btnGuardarPrinter ->{
                if (selectedEquipo.codigo.isNullOrEmpty()){
                    alert("Debes seleccionar un tipo de STB","¡Atencion!"){
                        positiveButton("Aceptar"){}
                    }.show()
                }
                else {
                    if (mPago.cantidad != null) {
                        alert("¿Desea cargar ${mFacturacion.monto} por ${mFacturacion.concepto} al cliente ${mCliente!!.nombre}?", "¡Atencion!") {
                            positiveButton("Aceptar") { goPagos() }
                            negativeButton("Cancelar") {}
                        }.show()
                    } else {
                        alert("Selecciona otro tipo", "${spTipoStb.selectedItem} no disponible") {
                            positiveButton("Aceptar") {}
                            negativeButton("Cancelar") {}
                        }.show()
                    }
                }
            }
        }
        return true
    }

    fun goPagos(){
        mPago.monto = mPago.cargo
        val intent = Intent(this, PagoActivity::class.java)
        val bundle: Bundle? = Bundle()
        bundle!!.putSerializable("cliente",mCliente!!)
        bundle.putSerializable("facturacion",mFacturacion)
        bundle.putSerializable("pago",mPago)

        /*
        intent.putExtra("extension",mPago.extension)
        intent.putExtra("cargo",mPago.cargo)
        intent.putExtra("tipo",mPago.tipo)
        intent.putExtra("tipo_op", Pago.Tipo_OP.SB)
        intent.putExtra("cod_serv",mPago.cod_serv)
        intent.putExtra("desc_serv",mPago.desc_serv)*/
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

    companion object{
        var cantidad: Int = 0
        var tieneExtension: Boolean = false
        var fExtension = DetalleFacturacion()
        var fStb = DetalleFacturacion()
    }


}
