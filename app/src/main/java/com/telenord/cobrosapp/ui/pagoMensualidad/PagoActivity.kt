package com.telenord.cobrosapp.ui.pagoMensualidad

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.print.printUtils.PrinterOps
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.operaciones.OperacionesActivity
import com.telenord.cobrosapp.ui.cardnet.CardnetActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.detalle.DetalleMens
import com.telenord.cobrosapp.ui.pagoMensualidad.pago.PagoMens
import com.telenord.cobrosapp.ui.scantarjeta.ScanActivity
import com.telenord.cobrosapp.util.DecimalDigitsInputFilter
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onClick


private const val TAG = "Pago Activity"

class PagoActivity : AppCompatActivity(), PagoContract.View, DetalleMens.OnFragmentInteractionListener, PagoMens.OnFragmentInteractionListener {

    lateinit var etMonto: EditText
    var cliente : Clientes? = null
    var mListener : PagoContract.Listener? = null
    var tvBalance: TextView? = null
    var tvAcuerdo: TextView? = null
    var tvCuotas : TextView? = null
    var balance : Double? = 0.0
    var mCliente : Clientes? =null
    var tvContrato: TextView? = null
    var ivLockPago: ImageView? = null
    var tvMonto : TextView? = null
    var mProgressBar: ProgressBar? = null
    var mPago= Pago()
    lateinit var chkPorcientoCobro : CheckBox
    var dialogValid: Int?=0
    var nombreCliente : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_mens)



        val bundle:Bundle? = intent.extras

        mCliente = bundle!!.getSerializable("cliente") as Clientes
        nombreCliente = mCliente!!.nombre!!

        balance = mCliente!!.balance

        mPago.opc_cargo = (intent.extras?.get("tipo_op")as? Pago.Tipo_OP)
       // mPago.monto = intent.extras?.getDouble("monto_pago")



            if (intent.extras?.getDouble("cargo")!=null){mPago.cargo = intent.extras?.getDouble("cargo") }

              if (intent.extras?.getSerializable("pago") != null){ mPago = intent.extras?.getSerializable("pago") as Pago  }

            mPago.facturacion = bundle.getSerializable("facturacion") as DetalleFacturacion?
            mPago.imei=  SharedPreferenceManager.getInstance(this).dispositivo.imei

            if (intent.extras?.getSerializable("cambio_stb")!= null) { mPago.cambio_stb  = intent.extras?.getSerializable("cambio_stb")as ArrayList<STB>}
            if(bundle.getSerializable("traslado")!=null){ mPago.traslado = bundle.getSerializable("traslado")  as TrasladoOrden}

            prepareView()


        DetalleMens.mClientes = mCliente
        DetalleMens.mFacturacion = mPago.facturacion
        tvBalance = find(R.id.tvbalance)
        tvBalance!!.text = "RD$ ${balance!!}"
        tvContrato = find(R.id.tvContratoPago)
        tvContrato!!.text = mCliente!!.contrato

        if (mListener ==null)
        {
            mListener = PagoPresenter(this, this)
        }
        mListener!!.getAcuerdo(mCliente!!.contrato!!)


        if (fragmentLoaded==null) {MostrarFragment(DetalleMens())
        fragmentLoaded = fDetalle}


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        Log.d("contrato1->", tvContrato?.text.toString())
        Log.d("monto1->", etMonto.text.toString())

    }

    override fun showAcuerdo(acuerdo: Acuerdo) {
        tvAcuerdo!!.text = "${acuerdo.acuerdo}"
        tvCuotas!!.text = "${acuerdo.cuotas_vencidas}"
    }

    override fun showLoading(l: Boolean) {
        if (l){
            mProgressBar!!.isIndeterminate = true
            mProgressBar!!.visibility = View.VISIBLE
        }else{
            mProgressBar!!.visibility = View.GONE
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFragmentInteraction(pago: Pago) {
        mPago.insert = pago.insert
        mPago.tipo_pago = pago.tipo_pago
        if (checkMonto()) {
            if (etMonto!!.isEnabled){
                mPago.monto = etMonto!!.text.toString().toDouble()
            }else{
                if (mPago.opc_cargo == Pago.Tipo_OP.FC)
                {
                    mPago.monto = mCliente!!.balance
                }

            }
            if(dialogValid==0){
                showAlert(mPago)
                dialogValid=1
            }
        }

    }



    fun prepareView(){
        tvAcuerdo = find(R.id.tvAcuerdo)
        tvCuotas = find(R.id.tvCuotas)
        etMonto = find(R.id.etMontoMens)
        etMonto!!.filters = arrayOf(DecimalDigitsInputFilter(5, 2))
        ivLockPago = find(R.id.ivLockPago)
        tvMonto = find(R.id.lblMontoMens)
        mProgressBar = find(R.id.progressBarPago)
        chkPorcientoCobro = find(R.id.chkBoxPorcientoCobro)



        if (mPago.opc_cargo == Pago.Tipo_OP.CS || mPago.opc_cargo == Pago.Tipo_OP.CM || mPago.opc_cargo == Pago.Tipo_OP.SB ){
            chkPorcientoCobro.visibility = View.VISIBLE
        }
        if (mPago.opc_cargo != Pago.Tipo_OP.FC){
                pagoTotal(mPago.cargo!!)
        }
        else {
            if (mCliente!!.balance > 0) {
                etMonto!!.error = null
                tvMonto!!.text = "Cobrar total pendiente"
                pagoTotal(mCliente!!.balance)

                ivLockPago!!.onClick {
                    if (!etMonto!!.isEnabled) {
                        tvMonto!!.text = "Ingrese monto a pagar"
                        etMonto!!.setText("")
                        pagoDigitado()
                    } else if (etMonto!!.isEnabled) {
                        etMonto!!.error = null
                        tvMonto!!.text = "Cobrar total pendiente"
                        pagoTotal(mCliente!!.balance)

                }
                }

            } else if (mCliente!!.balance <= 0) {
                pagoDigitado()
            }
        }
    }


    fun pagoTotal(precio: Double){
        etMonto!!.isEnabled = false
        ivLockPago!!.imageResource = R.drawable.ic_lock
        var nPrecio = precio

        chkPorcientoCobro.onClick {
            if (chkPorcientoCobro.isChecked){

                nPrecio = nPrecio/2

            }else{
                nPrecio = precio
            }
            monto = nPrecio

            etMonto!!.setText("$nPrecio")
            mPago.monto = monto
        }
        monto = nPrecio
        etMonto!!.setText("$nPrecio")

    }
    fun pagoDigitado(){
        etMonto!!.isEnabled = true
        ivLockPago!!.imageResource = R.drawable.ic_unlock

        etMonto!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) monto = s.toString().toDouble()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.isNullOrEmpty()) monto = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) monto = s.toString().toDouble()
            }
        })
    }

    fun MostrarFragment(fragment: androidx.fragment.app.Fragment){
        val manager = supportFragmentManager
        val transition = manager.beginTransaction()
        transition.replace(R.id.flContenedorPagos, fragment)
        transition.commit()
    }

    fun checkMonto(): Boolean{
        if (etMonto!!.text.isNullOrEmpty() &&  etMonto!!.isEnabled){
            etMonto!!.requestFocus()
            etMonto!!.setError("Debe Ingresar Monto")
            return false
        }else if (mPago.opc_cargo != Pago.Tipo_OP.RC && etMonto!!.text.toString().toDouble() <1     ){
            etMonto!!.requestFocus()
            etMonto!!.setError("Monto Incorrecto")
            return false
        }
        else return true
    }

    fun showAlert(pago: Pago){
        val alerta =alert("¿Esta seguro de realizar el pago de ${pago.monto} al cliente ${mCliente!!.nombre}?", "Confirmar Pago"){
            positiveButton("Aceptar"){
                Log.e("El Pago", mPago.toString())
                if (PrinterOps(this@PagoActivity).hasPrinterToUser())mListener!!.Cobrar(mCliente!!, pago)
            }
            negativeButton("Cancelar"){
                dialogValid=0;
            }
    }
        alerta.isCancelable = false
        alerta.build()
        alerta.show()

    }

    fun showAlertCardnet(res : ResponseFactura){
        getFactura(res)
    }


    override fun getFactura(factura: ResponseFactura) {
        val resultIntent = intent
        resultIntent.putExtra("factura", factura)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                if (fragmentLoaded == fPago) {
                    MostrarFragment(DetalleMens())
                    item!!.setIcon(R.drawable.ic_next_white)
                    fragmentLoaded = fDetalle
                } else finish()
                return true
            }

        /*    R.id.btnGuardarPrinter ->{

                if (fragmentLoaded == fPago){

                }
                else {
                    optionmenu!!.findItem(R.id.btnGuardarPrinter).isEnabled = false
                    MostrarFragment(PagoMens())
                    item!!.setIcon(R.drawable.ic_check_circle)
                fragmentLoaded = fPago
                    }
                Log.e("Fragment","$fragmentLoaded")
            }*/

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        fragmentLoaded = null
    }

    companion object {
        var fragmentLoaded : Int? = null
        val fDetalle = 1
        val fPago = 2
        var monto: Double? = null
    }

    override fun showError(i: String) {
        alert(i, "ATENCION!") {
            positiveButton("Aceptar", {})
        }.show()
    }

    fun goScanActivity(){
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra("contrato", tvContrato?.text)
        intent.putExtra("monto", etMonto.text)
        if(checkMonto()){
            startActivity(intent)
        }

    }

    fun goCardnetActivity(){
        val a = tvContrato?.text
        val b = etMonto.text
        Log.d("a->", a.toString())
        Log.d("a->", b.toString())
        if (checkMonto()) {
            val intent = Intent(this, CardnetActivity::class.java)
            intent.putExtra("contrato", a)
            intent.putExtra("monto", b.toString())
            Log.d("dklfj", nombreCliente!!)
            val a = nombreCliente!!.substringBefore(" ")
            val b = nombreCliente!!.substringAfter(",")
            val c = b.substringBeforeLast(" ")
            Log.d("a", a)
            Log.d("a", b)
            val armarNombre = a+c
            Log.d("NombreArmado-->", armarNombre)
            intent.putExtra("nombreCliente", armarNombre)
            startActivityForResult(intent, 12)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 12){
            if(resultCode == Activity.RESULT_OK){
                Log.d("entro","devuelta")
                if (data?.extras!!.containsKey("serial")) {
                    val re = data.extras?.get("serial") as ResponseFactura
                    showAlertCardnet(re)
                }
            }
            if(resultCode == Activity.RESULT_CANCELED){
                Log.d("cardrd","fue cancelado")
                MostrarFragment(PagoMens())
            }
            if(resultCode == 1000){
//                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

}
