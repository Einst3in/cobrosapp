package com.telenord.cobrosapp.ui.dialogs.visitaRuta

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import com.telenord.cobrosapp.models.Concepto
import com.telenord.cobrosapp.models.Ruta
import com.telenord.cobrosapp.R
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.ArrayList

class VisitaRuta() : androidx.fragment.app.DialogFragment(), VisitaRutaContract.View {

    var btnAceptar: Button? = null
    var btnCancelar: Button? = null
    var rGroup: RadioGroup? = null
    var mListener: VisitaRutaContract.Listener? = null
    var mPBar : ProgressBar? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mLayout = inflater.inflate(R.layout.visita_ruta,container,false)

        mListener= VisitaRutaPresenter(requireContext(),this)
        initViews(mLayout)
        dialog!!.setTitle("Accion")

        return mLayout
    }

    fun initViews(v: View){

        rGroup = v.findViewById(R.id.rGroupRuta)
        mPBar = v.findViewById(R.id.pBarVisitaRuta)

        btnAceptar =v.findViewById(R.id.btnAceptar)
        btnAceptar!!.onClick {  this@VisitaRuta.dismiss()}

        btnCancelar =v.findViewById(R.id.btnCancelar)
        btnCancelar!!.onClick {  this@VisitaRuta.dismiss()}

        mListener!!.getConceptos()
    }

    override fun showLoading(t: Boolean) {
        mPBar!!.isIndeterminate = t
        if (t){
            mPBar!!.visibility = View.VISIBLE
        }else{
            mPBar!!.visibility = View.GONE
        }

    }

    override fun showConceptos(conceptos: ArrayList<Concepto>){
        for (c in conceptos){
            val rb = RadioButton(context)
            rb.text = c.descripcion
            rb.id = c.codigo!!
            rGroup!!.addView(rb)
        }
        rGroup!!.check(conceptos[0].codigo!!)

        btnAceptar!!.onClick {
            if (rGroup!!.getCheckedRadioButtonId() != -1)
            {
            mListener!!.postVisita("${contrato}",rGroup!!.getCheckedRadioButtonId())
            }
            else this@VisitaRuta.dismiss()
        }

    }

    override fun visitaPosteada() {
       // RutaFragment().actualizarItem(ruta)
       val bundle =    requireActivity().intent
        bundle.putExtra("ruta",ruta)
        targetFragment!!.onActivityResult(targetRequestCode,200,requireActivity().intent)
        this@VisitaRuta.dismiss()
    }





    companion object {
        private var mArr: ArrayList<String>? = null
        lateinit var contrato: String
        lateinit var ruta: Ruta
        fun getInstance(c: Ruta): VisitaRuta {
            ruta = c
            contrato = c.contrato!!
            return VisitaRuta()
        }
    }
}