package com.telenord.cobrosapp.ui.editarTelefono

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.Telefonos
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.util.regex.Pattern

class EditarTelefonoActivity : AppCompatActivity(),EditarTelefonoContract.View {


    lateinit var lLayoutTelAnt: LinearLayout
    lateinit var lLayoutCelAnt: LinearLayout
    lateinit var lLayoutOtroTelAnt: LinearLayout
    lateinit var lLayoutEmail: LinearLayout

    lateinit var icEditTel:  LinearLayout
    lateinit var icEditCel: LinearLayout
    lateinit var icEditOtroTel: LinearLayout
    lateinit var icEditEmail: LinearLayout

    lateinit var tvEditTelAnt: TextView
    lateinit var tvEditCelAnt: TextView
    lateinit var tvEditOtroTelAnt: TextView
    lateinit var tvEditEmail: TextView

    lateinit var etEditTel: EditText
    lateinit var etEditCel: EditText
    lateinit var etEditOtroTel: EditText
    lateinit var etEditEmail: EditText

    lateinit var btnCelLlamar : Button
    lateinit var btnTelLlamar : Button
    lateinit var btnOtroTelLlamar : Button



    lateinit var mListener: EditarTelefonoContract.Listener
    lateinit var mCliente: Clientes
    lateinit var mBar: ProgressBar
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_telefono)

        title  = "Editar Numeros"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        mListener = EditarTelefonoPresenter(this,this)
        initValues()
        mCliente = intent?.extras?.getSerializable("cliente") as Clientes

        if (mCliente.contrato != null){
            mListener.getTelefonos(mCliente.contrato!!)
        }

    }

    fun initValues(){
        lLayoutTelAnt = find(R.id.lLayoutTel)
        lLayoutCelAnt = find(R.id.lLayoutCel)
        lLayoutOtroTelAnt = find(R.id.lLayoutOtroTel)
        lLayoutEmail = find(R.id.lLayoutEmail)

        icEditTel = find(R.id.icEditTel)
        icEditCel = find(R.id.icEditCel)
        icEditOtroTel = find(R.id.icEditOtroTel)
        icEditEmail = find(R.id.icEditEmail)

        tvEditTelAnt = find(R.id.tvTelEditarTel)
        tvEditCelAnt = find(R.id.tvCelEditarTel)
        tvEditOtroTelAnt = find(R.id.tvOtroTelEditarTel)
        tvEditEmail = find(R.id.tvEmailEditarTel)

        etEditTel = find(R.id.etTelEditarTel)
        etEditCel = find(R.id.etCelEditarTel)
        etEditOtroTel = find(R.id.etOtroTelEditarTel)
        etEditEmail = find(R.id.etEmailEditarTel)

        btnCelLlamar =  find(R.id.btnCelLlamar)
        btnTelLlamar =  find(R.id.btnTelLlamar)
        btnOtroTelLlamar =  find(R.id.btnOtroTelLlamar)

        mBar = find(R.id.pBarEditarTel)

    }

    override fun showLoading(t: Boolean) {
        mBar.isIndeterminate = t
        mBar.visibility = if(t) View.VISIBLE else View.INVISIBLE

    }

    override fun showTelefono(telefono: Telefonos?) {
        mTelefono = telefono!!

       if (telefono!=null){
           if (!telefono.telefono.isNullOrEmpty()){
               lLayoutTelAnt.visibility = View.VISIBLE
               tvEditTelAnt.text = telefono.telefono
               etEditTel.setText(telefono.telefono)
               etEditTel.isEnabled = false

               icEditTel.onClick {
                   etEditTel.isEnabled = !etEditTel.isEnabled
               }

               btnTelLlamar.onClick { this@EditarTelefonoActivity.makeCall(telefono.telefono) }
           }
           if (!telefono.celular.isNullOrEmpty()){
               lLayoutCelAnt.visibility = View.VISIBLE
               tvEditCelAnt.text = telefono.celular
               etEditCel.setText(telefono.celular)
               etEditCel.isEnabled = false

               icEditCel.onClick {
                   etEditCel.isEnabled = !etEditCel.isEnabled
               }

               btnCelLlamar.onClick { this@EditarTelefonoActivity.makeCall(telefono.celular) }
           }
           if (!telefono.otroTelefono.isNullOrEmpty()){
               lLayoutOtroTelAnt.visibility = View.VISIBLE
               tvEditOtroTelAnt.text = telefono.otroTelefono
               etEditOtroTel.setText(telefono.otroTelefono)
               etEditOtroTel.isEnabled = false

               icEditOtroTel.onClick {
                   etEditOtroTel.isEnabled = !etEditOtroTel.isEnabled
               }

               btnOtroTelLlamar.onClick { this@EditarTelefonoActivity.makeCall(telefono.otroTelefono) }
           }

           if (!telefono.email.isNullOrEmpty()){
               lLayoutEmail.visibility = View.VISIBLE
               tvEditEmail.text = telefono.email
               etEditEmail.setText(telefono.email)
               etEditEmail.isEnabled = false

               icEditEmail.onClick {
                   etEditEmail.isEnabled = !etEditEmail.isEnabled
               }


           }
       }
    }

    fun String.isValidEmail(): Boolean = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    override fun telefonoGuardado() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                onBackPressed()
            }
            R.id.men_guardar ->{

                if (etEditEmail.text.isNotEmpty() && !etEditEmail.text.toString().isValidEmail()){
                         etEditEmail.error = "Direccion de correo invalida" }

               else if (mTelefono.telefono == "${etEditTel.text}" && mTelefono.celular == "${etEditCel.text}" && mTelefono.otroTelefono == "${etEditOtroTel.text}" && mTelefono.email == "${etEditEmail.text}"){
                    toast("No se hicieron cambios")
                    finish()
                }else{
                    alert("¿Desea realizar los cambios?","Atencion!!!",{
                        positiveButton("Aceptar",{
                            mListener.postTelefono(mCliente.contrato!!, "${etEditTel.text}", "${etEditCel.text}","${etEditOtroTel.text}","${etEditEmail.text}")
                        })
                    }).show()

                }
            }
        }
        return true
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cobrar,menu)
        return true
    }

    companion object{
        lateinit var mTelefono: Telefonos
    }


}
