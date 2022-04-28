package com.telenord.cobrosapp.ui.asignarInternet

import android.app.Activity
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import kotlinx.android.synthetic.main.activity_internet.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find

class InternetActivity : AppCompatActivity(),InternetContract.View {

    var mListener: InternetContract.Listener? = null
    var mCliente: Clientes? = null
    var mProgressBar: ProgressBar? = null
    var mPlan= PlanInternet()
    lateinit var mEquipo : TipoEquipo
    var mPago = Pago()
    val REQUEST_RESULT = 200
    var mFacturacion = DetalleFacturacion()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internet)
        title = "Asignar Internet"
        var bundle = intent.extras
        mCliente = bundle?.getSerializable("cliente") as Clientes
        prepareView()
        mListener = InternetPresenter(this,this)
        mListener!!.getTipos(mCliente!!.contrato!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    fun prepareView(){
        mProgressBar = find(R.id.pBarInternet)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.config_printer,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun showTipos(list: ArrayList<TipoEquipo>) {

        val lista = ArrayList<TipoEquipo>()
        val p = TipoEquipo().apply { descripcion = "Seleccionar Equipo" }

        lista.add(0,p).also { lista.addAll(list) }

        val adapter = ArrayAdapter<TipoEquipo>(this,android.R.layout.simple_spinner_dropdown_item,lista)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoInt.adapter  = adapter



        spTipoInt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val equipo = parent!!.getItemAtPosition(position) as TipoEquipo
                mEquipo = equipo
                if (mEquipo.codigo !=null){
                    mListener!!.getPlanes(mCliente!!.contrato!!,mEquipo.codigo!!)
                }

            }
        }

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                onBackPressed()
            }
            R.id.btnGuardarPrinter ->{
                if (mPlan.codigo == null || mPlan.concepto ==null){
                    alertInternet()
                }else if (mPlan.existencia <=0){
                    alert("No hay equipos disponibles, favor comunicarse con almacen").show()
                }
                    else alert("¿Desea cargar ${mPlan.instalacion} por ${mPlan.concepto} ${mCliente!!.nombre}?","¡Atencion!"){
                    positiveButton("Aceptar"){goPagos()}
                    negativeButton("Cancelar"){}
                }.show()
            }
        }

        return true
    }

    override fun showLoading(t: Boolean) {
        if (t){
            mProgressBar!!.isIndeterminate = true
            mProgressBar!!.visibility = View.VISIBLE
        }else if (!t){
            mProgressBar!!.isIndeterminate = false
            mProgressBar!!.visibility = View.GONE
        }
    }


    override fun showPlanes(list: ArrayList<PlanInternet>) {
        val lista = ArrayList<PlanInternet>()
        val p = PlanInternet().apply { descripcion = "Selecciona un Plan" }

        lista.add(0,p).also { lista.addAll(list) }

        val adapter = ArrayAdapter<PlanInternet>(this,android.R.layout.simple_spinner_dropdown_item,lista)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spPlanInt.adapter = adapter


        spPlanInt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent!!.getItemAtPosition(position) as PlanInternet
                mPlan = item
                if (item.codigo != null){
                    mFacturacion = DetalleFacturacion("",item.fecha,item.instalacion,item.instalacion,0.0,item.fecha,"${mPlan.concepto}")
                    tvPrecioInt.text = "${item.instalacion!!.toDouble()}"
                    tvMensInt.text = "${item.mensualidad!!.toDouble()}"
                    tvMensSinInt.text = "${mCliente!!.mensualidad}"
                }

            }
        }
    }

    fun goPagos(){

        mPago.apply {
            plan= mPlan.codigo
            tipo = mEquipo.codigo
            opc_cargo =  Pago.Tipo_OP.CM
            cargo = mPlan.instalacion!!.toDouble()
            monto = mPlan.instalacion!!.toDouble()
        }
        val intent = Intent(this, PagoActivity::class.java)
        val bundle: Bundle? = Bundle()

        bundle!!.putSerializable("cliente",mCliente!!)
        bundle!!.putSerializable("facturacion",mFacturacion!!)
        bundle!!.putSerializable("pago",mPago)
        intent!!.putExtras(bundle)

        startActivityForResult(intent,REQUEST_RESULT)
    }
   fun alertInternet(){
       alert ("Debe seleccionar un plan","No puede continuar",{
           positiveButton("Aceptar",{})
       }).show()
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
