package com.telenord.cobrosapp.ui.operaciones

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import android.util.Log
import android.util.Printer
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions

import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.models.PagoDetalle
import com.telenord.cobrosapp.models.Precio
import com.telenord.cobrosapp.print.printUtils.PrinterOps
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.BaseActivity
import com.telenord.cobrosapp.ui.operaciones.OperactionesPresenter
import com.telenord.cobrosapp.ui.dialogs.PhonesDialog
import com.telenord.cobrosapp.ui.operaciones.tabs.TabsAdapterOperaciones
import com.telenord.cobrosapp.ui.editarTelefono.EditarTelefonoActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity

import java.util.Objects

class OperacionesActivity : BaseActivity(), OperacionesContract.View {

    internal var mListener: OperacionesContract.Listener? = null

    var mNombre: TextView? = null
    var mCedula: TextView? = null
    var mMensualidad: TextView? = null
    var mStatus: TextView? = null
    var mBalance: TextView? = null
    var mContrato: TextView? = null
    var mFoto: ImageView? = null
    var mDetalleMens : ImageView? = null
    var mProgress: ProgressDialog? = null
    var mCliente :Clientes? = null
    var mviewP : androidx.viewpager.widget.ViewPager? = null
    var tabs : TabLayout? = null





    fun cargarDatos(){
        mListener!!.getBalances(mCliente!!.contrato!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operaciones)
        mListener = OperactionesPresenter(this, this)
        prepareView()
        val bundle : Bundle = intent.extras!!
        mCliente = bundle.getSerializable("cliente") as Clientes
        mListener!!.getBalances(mCliente!!.contrato!!)

        Log.e("err0r -->", mCliente?.nombre.toString())

        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        mviewP = findViewById<View>(R.id.vpContainer) as androidx.viewpager.widget.ViewPager
        tabs = findViewById<View>(R.id.tabs) as TabLayout

        mviewP!!.adapter = TabsAdapterOperaciones(supportFragmentManager,mCliente!!)
        mviewP!!.offscreenPageLimit = 1
        mviewP!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs!!.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mviewP))


    }

    fun prepareView() {
        mNombre = findViewById<View>(R.id.Nombre) as TextView
        mCedula = findViewById<View>(R.id.Cedula) as TextView
        mMensualidad = findViewById<View>(R.id.Mensualidad) as TextView
        mStatus = findViewById<View>(R.id.Status) as TextView
        mFoto = find(R.id.Foto)
        mDetalleMens = find(R.id.ivDetalleMens)
        mContrato = findViewById<View>(R.id.txtOpContrato) as TextView
        mBalance = findViewById<View>(R.id.Balance) as TextView
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Cargando...")

    }


    override fun showLoading(t: Boolean?) {
        if (t!!) {
            mProgress!!.show()
        } else {
            mProgress!!.dismiss()
        }
    }

    override fun showMsj(msj: String) {
        alert(msj,"Atencion").show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.operaciones_menu,menu)
        val tels : String? = mCliente!!.telefonos
        val cels : String? = mCliente!!.celular
        val geo :String? = mCliente!!.geo

        if (tels.isNullOrEmpty() && cels.isNullOrEmpty()){
            val itemMenu = menu!!.findItem(R.id.itemLlamar)
            itemMenu.icon = ContextCompat.getDrawable(this,R.drawable.ic_phone_locked) }

        if (geo.isNullOrEmpty()){
            val item = menu!!.findItem(R.id.itemLocation)
            item.icon  = ContextCompat.getDrawable(this,R.drawable.ic_location_off)
        }

        return true
    }

    /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {


    }*/
    override fun showMensualidad(items: List<Precio>) {

        //first I create an ArrayList for being available to add Items
     var details = ArrayList<String>()

     for (c in items){
            Log.e("Elemento",c.toString())
         details.add("${c.descripcion} : ${c.precio}") }

        //then the arrayOfNulls is the only one that can be called in setItems
        var list = arrayOfNulls<String>(details.size)
        list = details.toArray(list)

        if (!list.isNullOrEmpty()){
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Detalle Mensualidad")
            alert.setItems(list) { dialog, which ->}
            alert.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.itemLocation ->{
                if(!mCliente!!.geo.isNullOrEmpty()){

                        val gmmIntentUri = Uri.parse("google.navigation:q=${mCliente!!.geo}&mode=d")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.`package` = "com.google.android.apps.maps"
                        startActivity(mapIntent)
                }else{
                    toast("Ubicacion No Disponible")
                }
            }


            R.id.itemLlamar ->{
                val telefonos = ArrayList<String>()
                if (!mCliente!!.celular.isNullOrEmpty())telefonos.add(mCliente!!.celular!!)
                if (!mCliente!!.telefonos.isNullOrEmpty())telefonos.add(mCliente!!.telefonos!!)

                        this.startActivity<EditarTelefonoActivity>("cliente" to mCliente)

            }
            R.id.itemRefresh ->{
                cargarDatos()
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showBalance(cliente: PagoDetalle?) {
        mCliente!!.status = cliente!!.status
        mCliente!!.statusDescripcion = cliente.status_descripcion
        mCliente!!.mensualidad = cliente.mensualidad!!
        mCliente!!.balance = cliente.balance!!
        mNombre!!.text = mCliente!!.nombre
        mCedula!!.text = mCliente!!.cedula
        mMensualidad!!.text = "RD$ ${cliente!!.mensualidad}"
        mContrato!!.text = mCliente!!.contrato
        mBalance!!.text = "RD$ ${cliente.balance}"
        mCliente!!.mensualidad= cliente.mensualidad!!
        mCliente!!.balance= cliente.balance!!
        mCliente!!.balanceCaja = cliente.balance_caja!!

        val stado = cliente!!.status
        when (stado) {
            2,9,16,20,21 -> mStatus!!.setTextColor(this.resources.getColor(R.color.primary))

            4,5,11,12,13 -> mStatus!!.setTextColor(this.resources.getColor(R.color.desconectado))

            1,3,7,10 -> mStatus!!.setTextColor(this.resources.getColor(R.color.accent))
        }

        mStatus!!.text = cliente!!.status_descripcion


        mFoto!!.onClick {
            getFoto()
        }

        mDetalleMens!!.onClick {
        mListener!!.getDetalleMens(mCliente!!.contrato!!)
        }
    }

    private fun getFoto(){
        val options = RequestOptions().centerCrop()
                .error(ContextCompat.getDrawable(this@OperacionesActivity,R.drawable.ic_broken_image))
        val token = SharedPreferenceManager.getInstance(this@OperacionesActivity).token
        Log.e("Glide","$token")
        val builder = LazyHeaders.Builder().addHeader("Authorization",token)
        val url = GlideUrl(Rest.getPadronUrl(mCliente!!.cedula!!),builder.build())

        Glide.with(this@OperacionesActivity).load(url).apply(options).into(mFoto!!);

    }
}
