package com.telenord.cobrosapp.ui.main

import android.Manifest
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.navigation.NavigationView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import android.telephony.TelephonyManager
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.crashlytics.android.Crashlytics

import com.telenord.cobrosapp.authenticator.AuthConst
import com.telenord.cobrosapp.authenticator.AuthenticatorActivity
import com.telenord.cobrosapp.models.Dispositivo
import com.telenord.cobrosapp.R
import com.telenord.cobrosapp.Rest.SharedPreferenceManager
import com.telenord.cobrosapp.util.Resolve
import com.telenord.cobrosapp.ui.BaseActivity
import com.telenord.cobrosapp.ui.clientes.BuscaClientes_Fragment
import com.telenord.cobrosapp.ui.impresora.ImpresoraFragment
import com.telenord.cobrosapp.ui.cuadre.CuadreFragment
import com.telenord.cobrosapp.ui.ruta.RutaFragment
import com.telenord.cobrosapp.util.Utils
import org.jetbrains.anko.act

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,MainContract.View {

    private val developer_mode = true
    var mListener : MainContract.Listener? = null
    var mNombre   : TextView? = null
    var mCiudad    : TextView? = null
    var mCompania : TextView? = null
    var mVersion   : TextView? = null
    var nv : NavigationView? = null





    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mListener = MainPresenter(this,this)

        nv = findViewById(R.id.nav_view)

      runtime_permissions()


    }

    @SuppressLint("MissingPermis sion", "NewApi")
    fun permissionsChecked(){

        val mAccount = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        val accounts = mAccount.getAccountsByType(AuthConst.ACCOUNT_TYPE)
        if (accounts.size <= 0 || SharedPreferenceManager(this).info == null) {

            val intent = Intent(this, AuthenticatorActivity::class.java)
            startActivity(intent)
            finish()
        }else {
            val info = SharedPreferenceManager.getInstance(this).info
            Log.e("Main Info",info.toString())
            val header = nv!!.getHeaderView(0)

            mNombre =   header.findViewById(R.id.tvNomUser)
            mCiudad =   header.findViewById(R.id.tvCiudadUser)
            mCompania = header.findViewById(R.id.tvCompUser)
            mVersion =  header.findViewById(R.id.tvVersion)

            mNombre!!.text = "${info.usuario}".toUpperCase()
            mCiudad!!.text = "${info.ciudad}".toUpperCase()
            mCompania!!.text = "${info.oficina}".toUpperCase()
            try {
                val pInfo = packageManager.getPackageInfo(packageName, 0).versionName
               mVersion!!.text = "VERSION: ${pInfo}"


            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
//                Crashlytics.logException(e)
                FirebaseCrashlytics.getInstance().recordException(Throwable(e))

            }
//            Crashlytics.setUserName("${info.usuario}")
            FirebaseCrashlytics.getInstance().setUserId("${info.usuario}")


            val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            val imei = manager.imei
            val imei = Utils().getDeviceId(act)
            val user = accounts[0].name

            var dispositivo = SharedPreferenceManager.getInstance(this).dispositivo
            if (dispositivo.imei != imei && dispositivo.user != user) {
                val tmpdispositivo = Dispositivo(imei, user)
                mListener!!.sendDispositivo(tmpdispositivo)

            }

            val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
            setSupportActionBar(toolbar)

            val cobros = BuscaClientes_Fragment()
            MostrarFragment(cobros)

            val drawer = findViewById<View>(R.id.drawer_layout) as androidx.drawerlayout.widget.DrawerLayout
            val toggle = ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            navigationView.setNavigationItemSelectedListener(this)
        }
    }

    fun MostrarFragment(fragment: androidx.fragment.app.Fragment) {
        val fragmentManager: androidx.fragment.app.FragmentManager
        val fragmentTransaction: androidx.fragment.app.FragmentTransaction
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flContenedorDeFragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as androidx.drawerlayout.widget.DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun sendDispositivo(dispositivo: Dispositivo) {
        SharedPreferenceManager.getInstance(this).Device(dispositivo.imei, dispositivo.user)
    }
    /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when(id){
            R.id.nav_cobros ->{
                val cobros = BuscaClientes_Fragment()
                MostrarFragment(cobros)
            }

            R.id.nav_cuadre -> {
                val cuadre = CuadreFragment()
                MostrarFragment(cuadre)
            }
            R.id.nav_ruta ->{
                val ruta = RutaFragment()
                MostrarFragment(ruta)
            }

            R.id.nav_printer ->{
                MostrarFragment(ImpresoraFragment.newInstance())
            }

            R.id.nav_salir->{
                Resolve.logoutUser(this)
            }


        }

        val drawer = findViewById<View>(R.id.drawer_layout) as androidx.drawerlayout.widget.DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    @TargetApi(Build.VERSION_CODES.M)

    fun runtime_permissions(): Boolean {

        if (Build.VERSION.SDK_INT >= 23){

            if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCOUNT_MANAGER )!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS )!= PackageManager.PERMISSION_GRANTED)

        {

            requestPermissions(

                    arrayOf(Manifest.permission.CALL_PHONE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.ACCOUNT_MANAGER,
                            Manifest.permission.READ_CONTACTS),
                    100
            )
            Log.e("Main Activity","Faltan Permisos")
            return true

        }

        permissionsChecked()
        return false }
        else{
            permissionsChecked()
            return false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: kotlin.Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            permissionsChecked()
        }
    }
}
