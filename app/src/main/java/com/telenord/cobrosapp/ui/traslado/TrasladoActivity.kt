package com.telenord.cobrosapp.ui.traslado

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import kotlinx.android.synthetic.main.activity_traslado.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class TrasladoActivity : AppCompatActivity(),TrasladoContract.View {

    var mCliente: Clientes? = null
    var mListener: TrasladoContract.Listener? = null
    var mProgressBar: ProgressBar? = null
    var mTrasladoOrden = TrasladoOrden()
    var mFacturacion = DetalleFacturacion()
    var mCiudad: Int? = null
    val REQUEST_RESULT = 200
    var mPago = Pago()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traslado)
        title = "Orden de traslado"
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)


        mListener = TrasladoPresenter(this,this)
        val bundle:Bundle? = intent.extras
        mCliente = bundle!!.getSerializable("cliente") as Clientes

        prepareView()

        if (mCliente!=null){
        mListener!!.getDireccionActual(mCliente!!.contrato!!)
            mListener!!.getCiudades()

            ivRefCiudad.onClick { mListener!!.getCiudades() }


            chkEdificio.onClick {
                if (chkEdificio.isChecked) {lLayoutEdificio.visibility = View.VISIBLE
                    ivRefEdificio.onClick { mListener!!.getEdificio() }
                    mListener!!.getEdificio()}
                else {lLayoutEdificio.visibility = View.GONE
                mTrasladoOrden.edificio = null}
            }

            chkEsquina.onClick {
                if (chkEsquina.isChecked) {lLayoutEsquina.visibility = View.VISIBLE
                    val ciudad = spCiudad.selectedItem as Ciudad
                    ivRefEsquina.onClick {mListener!!.getEsquina(ciudad.codigo!!)  }
                    mListener!!.getEsquina(ciudad.codigo!!)}
                else lLayoutEsquina.visibility = View.GONE
            }

        }
        mListener!!.getPrecioTraslado(mCliente!!.contrato!!,Pago.Tipo_OP.TR)
        mListener!!.getHorario()

    }

    fun prepareView(){
        mProgressBar = find(R.id.pBarTraslados)

        etCasa.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                mTrasladoOrden.numero_casa = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mTrasladoOrden.numero_casa = s.toString()
            }
        })

        etApartamento.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                mTrasladoOrden.numero_apartamento = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mTrasladoOrden.numero_apartamento = s.toString()
            }
        })

        etReferencia.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                mTrasladoOrden.referencia = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mTrasladoOrden.referencia = s.toString()
            }
        })

    }

    override fun showLoading(t: Boolean) {
        if (t)
        {
            mProgressBar!!.visibility = View.VISIBLE
            mProgressBar!!.isIndeterminate = true
        }
        else if (!t)
        {
            mProgressBar!!.isIndeterminate = false
            mProgressBar!!.visibility = View.GONE

        }
    }

    override fun showCiudades(list: ArrayList<Ciudad>) {
        list.add(0,Ciudad().apply { nombre="Seleccionar Ciudad" })
        val adapter = ArrayAdapter<Ciudad>(this,android.R.layout.simple_spinner_dropdown_item,list)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCiudad.adapter = adapter

        spCiudad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seletedItem = parent!!.getItemAtPosition(position) as Ciudad
                if(seletedItem.codigo != null){
                mCiudad = seletedItem.codigo
                mTrasladoOrden.ciudad = seletedItem.codigo
                ivRefSector.onClick {mListener!!.getSectores(seletedItem.codigo!!)  }
                mListener!!.getSectores(seletedItem.codigo!!)}else{
                    mTrasladoOrden.ciudad = null
                }
            }
        }
    }


    override fun showEdificios(list: ArrayList<Edificio>) {
        list.add(0,Edificio().apply { nombre="Seleccionar Edificio" })
        val adapter = ArrayAdapter<Edificio>(this,android.R.layout.simple_spinner_dropdown_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEdificio.adapter = adapter

        spEdificio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position) as Edificio
                if (selectedItem.codigo!= null){
                ivRefEdificio.onClick {
                mTrasladoOrden.edificio = "${selectedItem.codigo}" }}
            }
        }

    }

    override fun showSectores(list: ArrayList<Sector>) {
        list.add(0, Sector().apply { nombre="Seleccionar Sector" })
        val adapter = ArrayAdapter<Sector>(this,android.R.layout.simple_spinner_dropdown_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSector.adapter = adapter

        spSector!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position) as Sector
                if (selectedItem.codigo != null){
                mTrasladoOrden.sector = selectedItem.codigo
                ivRefCalle.onClick { mListener!!.getCalles(selectedItem.codigo!!) }
                mListener!!.getCalles(selectedItem.codigo!!)}
                else{
                    mTrasladoOrden.sector = null
                }
            }
        }
    }

    override fun showCalles(list: ArrayList<Calles>) {
        list.add(0,Calles().apply { nombre="Seleccionar Calle" })
       val adapter = ArrayAdapter<Calles>(this,android.R.layout.simple_spinner_dropdown_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCalle.adapter  = adapter

        spCalle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position) as Calles
                if (selectedItem.codigo!=null){
                mTrasladoOrden.calle = selectedItem.codigo
                mTrasladoOrden.zona = selectedItem.zona}else{
                    mTrasladoOrden.calle = null
                    mTrasladoOrden.zona = null
                }
            }
        }
    }

    override fun showDireccionActual(direccion: String) {
       tvDireccionActual.text = direccion
    }

    override fun showEsquina(list: ArrayList<Calles>) {
        list.add(0,Calles().apply { nombre="Seleccionar Esquina" })
        val adapter = ArrayAdapter<Calles>(this,android.R.layout.simple_spinner_dropdown_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEsquina.adapter  = adapter

        spEsquina.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position) as Calles
                if (selectedItem.codigo != null){
                    mTrasladoOrden.esquina = "${selectedItem.codigo}"
                }

            }
        }
    }
    fun checkAllEntries():Boolean{
        mTrasladoOrden.run {
            if (calle == null || sector ==  null || ciudad == null || zona == null){
                alert("Debes llenar todos los datos correctamente","Atencion!!",{
                    positiveButton("Aceptar"){}
                }).show()
                return false
            }else return true
        }

    }
    fun checkEntry(et: EditText): Boolean{
        if (et.text.isNullOrEmpty()){
            et.requestFocus()
            et.setError("Campo Requerido")
            return false
        }else return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.config_printer,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId)
        {
        android.R.id.home ->
        {
            onBackPressed()
        }
        R.id.btnGuardarPrinter ->
        {
            if (checkAllEntries()&& checkEntry(etCasa) && checkEntry(etReferencia)){
            alert("¿Desea cargar ${mPago.cargo} por traslado al cliente ${mCliente!!.nombre}?","¡Atencion!"){
                positiveButton("Aceptar"){
                    Log.e("traslado",mTrasladoOrden.toString())
                    goPagos()}
                negativeButton("Cancelar"){}
            }.show()}
        }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showHorario(list: ArrayList<Horario>) {
        val adapter  = ArrayAdapter<Horario>(this,android.R.layout.simple_spinner_dropdown_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHorarioTR.adapter= adapter

        spHorarioTR.onItemSelectedListener =  object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val horario = parent!!.getItemAtPosition(position) as Horario
                mPago.horario = horario.descripcion
            }
        }
    }

    override fun showPrecio(precio: Precio) {
        mPago.cargo = precio.precio
        mFacturacion = DetalleFacturacion("",precio.fecha,mPago.cargo, mPago.cargo,0.0,precio.fecha,"Traslado")

    }

    fun goPagos(){



        val intent = Intent(this, PagoActivity::class.java)
        val bundle: Bundle? = Bundle()
        mPago.apply {
            monto = cargo
            opc_cargo = Pago.Tipo_OP.TR
        }
        bundle!!.putSerializable("cliente",mCliente!!)
        bundle!!.putSerializable("traslado",mTrasladoOrden)
        bundle!!.putSerializable("facturacion",mFacturacion)
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
