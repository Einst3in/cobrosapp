package com.telenord.cobrosapp.ui.averias

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

class AveriasActivity : AppCompatActivity(), AveriaContract.View {

    var mListener: AveriaContract.Listener? = null
    var spConcepto: Spinner? = null
    var mProgress: ProgressDialog? = null
    var etObservacion : EditText? = null
    var mCliente: Clientes? = null
    var idConcepto: Int? = null
    var concepto: String? = null
    var mContacto :Contacto? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_averias)
        title = "Reporte de Averia"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        var bundle = intent.extras
        mCliente = bundle?.getSerializable("cliente") as Clientes
        if (mListener == null ){mListener = AveriasPresenter(this,this)}
        initView()
        mListener!!.getConceptos()
        mListener!!.getContacto()
    }

    fun initView(){
        spConcepto = find(R.id.spConceptos)
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Cargando...")
        mProgress!!.isIndeterminate
        etObservacion = find(R.id.etObsevacionAve)

        spConcepto!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seletedItem = parent!!.getItemAtPosition(position) as Concepto

                idConcepto = seletedItem.codigo
                concepto = seletedItem.descripcion

            }
        }
    }

    override fun finish(t: Boolean) {
        if (t) finish()
    }

    override fun showContacto(contacto: Contacto) {
        mContacto = contacto
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cobrar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> onBackPressed()

            R.id.men_guardar ->{
                if (etObservacion!!.text.isNullOrEmpty()){
                    etObservacion!!.error = "Falta Descripcion"
                }else if(mContacto == null){
                    toast("Faltan los datos de contacto")
                }
                else{
                    alert("¿Desea reportar averia por concepto de ${concepto} al cliente ${mCliente!!.nombre}?","!Atencion¡"){
                        positiveButton("Aceptar"){
                            val observacion = etObservacion!!.text.toString()
                            val averia = Averia(mCliente!!.contrato!!,concepto!!,observacion)
                            mListener!!.postAveria(averia,mContacto!!)
                        }
                        negativeButton("Cancelar"){}
                    }.show()
                }
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun fillSpinner(conceptos: ArrayList<Concepto>) {
        val adapter: ArrayAdapter<Concepto> = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,conceptos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spConcepto!!.adapter = adapter
    }

    override fun showProgress(t: Boolean) {

        if (t)mProgress!!.show()
        else{

            if(mProgress!!.isShowing){
                mProgress!!.dismiss()
            }
        }
    }

}
