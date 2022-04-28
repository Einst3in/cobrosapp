package com.telenord.cobrosapp.ui.operaciones.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.telenord.cobrosapp.models.Clientes
import com.telenord.cobrosapp.ui.operaciones.tabs.operacionesTab.OpFragment
import com.telenord.cobrosapp.ui.operaciones.tabs.pagosTab.PagosFragment

class TabsAdapterOperaciones// Constructor
internal constructor(manager: androidx.fragment.app.FragmentManager, mCliente : Clientes) : androidx.fragment.app.FragmentStatePagerAdapter(manager) {
    // Title for tabs
    private val fragmentTitles = arrayOf("Pendietes", "Mapa")
    private val mCliente=mCliente


    // Return the Fragment associated with a specified position.
//    override fun getItem(position: Int): Fragment {
//        return when (position) {
//            0 -> PagosFragment().apply { arguments = Bundle().apply { putParcelable("cliente",mCliente!!) } }
//            1 -> OpFragment().apply { arguments = Bundle().apply { putParcelable("cliente",mCliente!!) } }
//         else -> PagosFragment().apply { arguments = Bundle().apply { putParcelable("cliente",mCliente!!) } }
//        }
//
//    }


    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            0 -> PagosFragment.newInstance(mCliente!!)
            1 -> OpFragment.newInstance(mCliente!!)
            else -> PagosFragment.newInstance(mCliente!!)
        }
    }
    // Return the number of views/fragments available.
    override fun getCount(): Int = 2

    // Return title based on position
    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }
}