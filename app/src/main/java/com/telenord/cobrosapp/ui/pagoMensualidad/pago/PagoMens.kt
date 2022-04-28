package com.telenord.cobrosapp.ui.pagoMensualidad.pago

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.cardnet.CardnetActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import kotlinx.android.synthetic.main.fragment_pago_mens.*
import org.jetbrains.anko.find


class PagoMens : androidx.fragment.app.Fragment(), PagoMensContract.View {

    private var listener: OnFragmentInteractionListener? = null
    var spTipoPago: Spinner? = null
    var cliente: Clientes? = null
    var spBanco: Spinner? = null
    var etNoTarjeta: EditText? = null
    var mListener: PagoMensContract.Listener? = null
    var spBancoCheque: Spinner? = null
    var balance: Double? = 0.0
    var mProgressBar: ProgressBar? = null
    var idBanco: Int? = null
    var tipoTarjeta: TrasnTajeta.TipoTarjeta? = null
    var spTipoTarjeta: Spinner? = null
    var mPago = Pago()

    var diferente : Context? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pago_mens, container, false)
        prepareView(v)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.setTitle("Tipo de Pago")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListener = PagoMensPresenter(this, requireContext())

        setHasOptionsMenu(true)
    }

    fun prepareView(v: View) {
        spTipoPago = v.find(R.id.spTipoPago)

        etNoTarjeta = v.find(R.id.etNoTarjeta)
        mProgressBar = v.find(R.id.pBarPagoFragment)
        spBanco = v.find(R.id.spBanco)
        spBancoCheque = v.find(R.id.spBancoCheque)
        spTipoTarjeta = v.find(R.id.spTipoTarjeta)

        spTipoPago!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val seletedItem = parent!!.getItemAtPosition(position).toString().toUpperCase()
                when (seletedItem) {
                    "TARJETA" -> {
                        Log.d("Tipo de Pago", "Tarjeta")
                        flPagoCheque.visibility = View.GONE
                        flPagoTarjeta.visibility = View.VISIBLE
//                        flPagoCardnet.visibility = View.GONE
                        tipoLoaded = ResponseFactura.Tipo_Pago.ta
                    }
                    "CHEQUE" -> {
                        flPagoTarjeta.visibility = View.GONE
                        flPagoCheque.visibility = View.VISIBLE
//                        flPagoCardnet.visibility = View.GONE
                        Log.d("Tipo de Pago", "Cheque")
                        tipoLoaded = ResponseFactura.Tipo_Pago.ck
                    }
                    "EFECTIVO" -> {
                        flPagoCheque.visibility = View.GONE
                        flPagoTarjeta.visibility = View.GONE
//                        flPagoCardnet.visibility = View.GONE
                        Log.d("Tipo de Pago", "Efectivo")
                        tipoLoaded = ResponseFactura.Tipo_Pago.ef
                    }
                    "CARDNET" -> {
                        flPagoCheque.visibility = View.GONE
                        flPagoTarjeta.visibility = View.GONE
                        flPagoCardnet.visibility = View.VISIBLE
                        Log.d("Tipo de Pago", "Tarjeta sin verifone mediente Cardnet")
                        tipoLoaded = ResponseFactura.Tipo_Pago.card

                    }
                }
            }
        }

        spBanco!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val seletedItem = parent!!.getItemAtPosition(position) as Banco

                idBanco = seletedItem.codigo
                Log.e("Tipo Banco", idBanco!!.toString())
            }
        }

        spTipoTarjeta!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val seletedItem = parent!!.getItemAtPosition(position).toString().toUpperCase()
                if (seletedItem == "DEBITO") tipoTarjeta = TrasnTajeta.TipoTarjeta.D
                else if (seletedItem == "CREDITO") tipoTarjeta = TrasnTajeta.TipoTarjeta.C
            }
        }

    }

    override fun showLoading(t: Boolean) {
        if (t) mProgressBar!!.visibility = View.VISIBLE else mProgressBar!!.visibility = View.GONE
        mProgressBar!!.isIndeterminate = t
    }

//    override fun cardnet(bundle: Bundle) {
//
//    }

    fun pagoTarjeta(): Pago? {

        val entries = ArrayList<EditText>()
        entries.add(etBoucher)
        entries.add(etNoTarjeta!!)

        if (checkEntries(entries)) {

            val monto = PagoActivity.monto

            val insert: String =
                "${etNoTarjeta!!.text},${idBanco},$monto,${etBoucher.text},'$tipoTarjeta'"

            Log.e("Insert", insert.toString())
            mPago.monto = monto
            mPago.tipo_pago = ResponseFactura.Tipo_Pago.ta.name.toUpperCase()
            mPago.insert = insert
            Log.e("Pago", mPago.toString())
            return mPago
        }
        return null
    }


    //scan activity
    fun goScan(){
        (activity as PagoActivity).goScanActivity()
    }
//test this
    fun pagoTarjetaCardnet(){
//        mPago.tipo_pago = ResponseFactura.Tipo_Pago.card.name.toUpperCase()
//        val a = (activity as PagoActivity).tvContrato?.text
//        val b = (activity as PagoActivity).etMonto.text
//        Log.d("a->", a.toString())
//        Log.d("a->", b.toString())
//        if ((activity as PagoActivity).checkMonto()) {
//            val intent = Intent(context!!, CardnetActivity::class.java)
//            intent.putExtra("contrato", a)
//            intent.putExtra("monto", b.toString())
//            startActivityForResult(intent, 12)
//        }

        (activity as PagoActivity).goCardnetActivity()

//        val intent = Intent(context, CardnetActivity::class.java)
//        startActivityForResult(intent, 12)


    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 12){
//            if(resultCode == Activity.RESULT_OK){
//                Log.d("klk", "Tu dice")
//            }
//        }
//        if(requestCode == Activity.RESULT_CANCELED){
//            Log.d("klk", "Tu dice")
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 12){
//            if(resultCode == Activity.RESULT_OK){
//                Log.d("entro","devuelta")
//                if (data?.extras!!.containsKey("serial")) {
//                    val re = data.extras?.get("serial") as ResponseFactura
//
//                    (activity as PagoActivity).showAlertCardnet(re)
//                }
//            }
//            if(resultCode == Activity.RESULT_CANCELED){
//                Log.d("cardrd","fue cancelado")
//            }
//        }
//    }

    fun pagoCheque(): Pago? {

        val entries = ArrayList<EditText>()
        entries.add(etNoCheque)
        entries.add(etNoCuentaCheque)

        val monto = PagoActivity.monto
        if (checkEntries(entries)) {
            val cadena = "${etNoCheque!!.text},${0},$monto,${etNoCuentaCheque.text}"
            mPago.monto = monto
            mPago.tipo_pago = ResponseFactura.Tipo_Pago.ck.name.toUpperCase()
            mPago.insert = cadena
            return mPago
        }
        return null
    }

    fun pagoEfectivo(): Pago {


//        btnCobrar!!.onClick {
//        if (checkMonto()){
        mPago.tipo_pago = ResponseFactura.Tipo_Pago.ef.name.toUpperCase()
        mPago.insert = ""
        return mPago
    }

    /*  fun fillPago(pago: Pago): Pago{
          var tmpPago: Pago = pago
          if (tipoLoaded == ResponseFactura.Tipo_Pago.ck){
              tmpPago.tipo_pago = ResponseFactura.Tipo_Pago.ck
          }
          return tmpPago
      }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListener!!.getDatos()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            diferente = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    fun checkEntries(entries: ArrayList<EditText>): Boolean {

        for (e in entries) {
            if (e.text.isNullOrEmpty()) {
                e.setError("Campo Requerido")
                return false
            }
        }
        return true
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun showBancos(list: ArrayList<Banco>) {

            var adapterTa =
                ArrayAdapter<Banco>(diferente!!, android.R.layout.simple_spinner_dropdown_item, list)
            adapterTa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spBanco!!.adapter = adapterTa
            var adapterCHQ =
                ArrayAdapter<Banco>(diferente!!, android.R.layout.simple_spinner_dropdown_item, list)
            adapterCHQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spBancoCheque!!.adapter = adapterCHQ

    }



    override fun showTipoPago(list: ArrayList<String>) {
        var adapter =
            ArrayAdapter<String>(diferente!!, android.R.layout.simple_spinner_dropdown_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoPago!!.adapter = adapter
    }

    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(pago: Pago)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater!!.inflate(R.menu.menu_cobrar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {


            R.id.men_guardar -> {

                var pago: Pago? = null

                when (tipoLoaded) {
                    ResponseFactura.Tipo_Pago.ef -> {
                        pago = pagoEfectivo()
                    }
                    ResponseFactura.Tipo_Pago.ck -> {
                        pago = pagoCheque()
                    }
                    ResponseFactura.Tipo_Pago.ta -> {
                        pago = pagoTarjeta()
                    }
                    ResponseFactura.Tipo_Pago.card -> {
//                        pagoTarjetaCardnet()
                        goScan()

                    }
                }
                if (pago != null) listener!!.onFragmentInteraction(pago)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var monto: Double? = null
        var tipoLoaded: ResponseFactura.Tipo_Pago? = null
    }
}