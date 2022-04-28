package com.telenord.cobrosapp.ui.scantarjeta

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.ui.cardnet.CardnetActivity
import com.telenord.cobrosapp.ui.pagoMensualidad.PagoActivity
import org.jetbrains.anko.alert


class ScanActivity : AppCompatActivity() {
    var button: android.widget.Button? = null
    var textView: TextView? = null
    private var scanRequest: Int = 111

    //extrass
    val extra1 = intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        //button?.setOnClickListener { onButtonListener() }

        var hola1 : String = intent.getStringExtra("contrato").toString()
        var hola2 : String = intent.getStringExtra("monto").toString()

        load()



    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun load(){
        val scanIntent = Intent(this, CardIOActivity::class.java)

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false

        // my_scan_request_code is arbitrary and its only used within this activity
        startActivityForResult(scanIntent, scanRequest)
    }

//    private fun onButtonListener(){
//        val scanIntent = Intent(this, CardIOActivity::class.java)
//
//
//        // customize these values to suit your needs.
//        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false
//        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false
//        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false
//
//        // my_scan_request_code is arbitrary and its only used within this activity
//        startActivityForResult(scanIntent, scanRequest)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == scanRequest){
            var resultDisplayStr : String
            if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)){
                val scanResult : CreditCard? = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult?.redactedCardNumber + "\n"

                // Do something with the raw number, e.g.:
                //myService.setCardNumber( scanResult.cardNumber );

                if (scanResult?.isExpiryValid == true) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n"
                }

                if (scanResult?.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv?.length+ " digits.\n"
                }

                if (scanResult?.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n"
                }
            }else{
                resultDisplayStr = "Scan was canceled."
            }
            //do something with resultDisplayStr, maybe display it in a textView
            val a = intent.getStringExtra("contrato").toString()
            val b = intent.getStringExtra("monto").toString()
            gocardnet(a, b)

        }
        //else handle other activity results
    }

    fun gocardnet(a : String, b : String){
        val intent = Intent(this, CardnetActivity::class.java)
        intent.putExtra("contrato", a)
        intent.putExtra("monto", b)
        startActivity(intent)
    }

}