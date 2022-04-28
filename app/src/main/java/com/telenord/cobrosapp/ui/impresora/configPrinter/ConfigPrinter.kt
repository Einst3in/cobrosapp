package com.telenord.cobrosapp.ui.impresora.configPrinter

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.models.ImpresoraModelo
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import kotlinx.android.synthetic.main.activity_config_printer.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find

class ConfigPrinter : AppCompatActivity(), ConfPrinterContract.View {

    var REQUEST_CODE = 100
    var mListener: ConfPrinterContract.Listener? = null
    var printer : Impresora? = null
    var pBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_printer)
        title = "Configurar Impresora"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        printer = intent.getSerializableExtra("printer") as Impresora
        if (printer!= null) initValues(printer!!)

    }

    fun initValues(printer: Impresora){
        tvNombreConfPrinter.text = printer.modelo
        tvMacConfPrinter.text = printer.mac
        pBar = find(R.id.pBarConfPrinter)
        mListener = ConfPrinterPresenter(this,this)
        mListener!!.getModels()


        spModPrinter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedItem = parent!!.getItemAtPosition(position) as ImpresoraModelo
                printer!!.modelo_id = selectedItem.id
            }
        }

    }

    override fun showModels(models: ArrayList<ImpresoraModelo>) {
        var adapter = ArrayAdapter<ImpresoraModelo>(this,android.R.layout.simple_spinner_dropdown_item,models)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spModPrinter.adapter = adapter
    }

    fun guardarPrinter(){
        alert("¿Desea guardar esta impresora?","¡Atencion!"){
            positiveButton("Aceptar") {
                mListener!!.addPrinter(printer!!.mac,printer!!.modelo_id)
            }
            negativeButton("Cancelar",{})
        }.show()

    }

    override fun showAlert() {
        alert("Desea Agregar Como Predeterminada","Atencion"){
            positiveButton("Aceptar") {
                SharedPreferenceManager.getInstance(this@ConfigPrinter).printer(printer!!)
                mListener!!.setDefault(printer!!.mac)
                setResult(Activity.RESULT_OK,intent)
                finish()

            }
            negativeButton("Cancelar",{
                setResult(Activity.RESULT_OK,intent)
                finish()})
        }.show()
    }

    override fun showLoading(t: Boolean) {
        pBar!!.isIndeterminate = t
        if (t){
            pBar!!.visibility = View.VISIBLE
        }else pBar!!.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.config_printer,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btnGuardarPrinter -> consume { guardarPrinter() }

        android.R.id.home -> consume {onBackPressed()}

        else -> super.onOptionsItemSelected(item)
    }
    inline fun consume(f: () -> Unit):Boolean{
        f()
        return true
    }
}
