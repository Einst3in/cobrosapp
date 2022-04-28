package com.telenord.cobrosapp.ui.cardnet

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fevziomurtekin.payview.Payview
import com.fevziomurtekin.payview.data.PayModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.telenord.cobrosapp.models.ErrorResponse
import com.telenord.cobrosapp.models.Pago
import com.telenord.cobrosapp.models.ReciboFact
import com.telenord.cobrosapp.models.ResponseFactura
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.Rest
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import com.telenord.cobrosapp.util.ErrorUtils
import com.telenord.cobrosapp.util.Utils
import kotlinx.android.synthetic.main.activity_cardnet.*
import kotlinx.android.synthetic.main.activity_pago_mens.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.webView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CardnetActivity : AppCompatActivity(), CardnetContract.View{

    lateinit var mListener: CardnetContract.Listener

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardnet)

        mListener = CardnetPresenter(this, this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val extra1 = intent.getStringExtra("monto").toString()
        val extra2 = intent.getStringExtra("contrato").toString()
        Log.e("cardnetE-", extra1)
        Log.e("cardnetE-", extra2)



        payview.setOnDataChangedListener(object : Payview.OnChangelistener{
            override fun onChangelistener(payModel: PayModel?, isFillAllComponents: Boolean) {

                payModel?.cardOwnerName = "Miguel"
                Log.d("PayView", "data : ${payModel?.cardOwnerName} \n " +
                        "is Fill all form component : $isFillAllComponents")

            }

        })

        payview.setPayOnclickListener(View.OnClickListener {
            Log.d("PayView "," clicked. iss Fill all form Component : ${payview.isFillAllComponents}")

        })

//        webViewSetup()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    fun webViewSetup(){
        val imei = Utils().getDeviceId(this)
        val token = SharedPreferenceManager.getInstance(this).token
//        val token = "9f00097a7a188867d4cd1dc066d5dd37f20419ee"
        Log.e("Desde Authenticator", imei)
        Log.e("Desde authenticator", token)

        val bundle: Bundle = intent.extras!!
        val monto = bundle.getString("monto")
        val contrato = bundle.getString("contrato")
        val nombreCliente = bundle.getString("nombreCliente")!!

        Log.e("Monto a Cobrar->", monto.toString())
        Log.e("Contrato a pagar->", contrato.toString())
        Log.e("NombreCliente-->", nombreCliente)


//        wb_webview?.webChromeClient = WebChromeClient()

//        wb_webview.addJavascriptInterface(JsInterface(this,mListener), "Android")

//        wb_webview?.apply {
//            loadUrl("http://192.168.10.49:5001/pago?ref=$contrato&0001&monto=$monto&token=$token&imei=$imei&tarjetahabiente=$nombreCliente")
//            settings.javaScriptEnabled = true
//            settings.safeBrowsingEnabled = true
//        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showError(i: String, pago: Pago) {
        alert("ATENCION!") {
            positiveButton("Aceptar", {
                mListener.reintentarPago(i, pago)
            })
            isCancelable = false
        }.show()
    }

    override fun showLoading(l: Boolean) {
//        if (l!!) {
//            mProgress!!.show()
//        } else {
//            mProgress!!.dismiss()
//        }
    }

    override fun goHome(bundle: Bundle) {
        Log.d("response", bundle.toString())
        val a = bundle.get("response") as ResponseFactura
        val intent = Intent()
        intent.putExtra("serial",a)

        setResult(RESULT_OK,intent)
        finish()
    }

    override fun showErrorCardnet(i: String, pago: Pago) {
        alert("ERROR AL PROCESAR EL PAGO!") {
            positiveButton("Reintentar", {
                mListener.reintentarPago(i, pago)
            })
            negativeButton("Cancelar",{
                setResult(1000)
                finish()
            })
            isCancelable = false
        }.show()
    }

}

