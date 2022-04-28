package com.telenord.cobrosapp.ui.impresora.PariedDevices

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.telenord.cobrosapp.models.Impresora
import com.telenord.cobrosapp.print.BTOperator

class PairedDevicesPresenter:PairedDevicesContract.Listener {
    internal var mCtx:Context?=null
    internal var mView:PairedDevicesContract.View?=null

    var mBto: BTOperator?=null

    constructor(mCtx:Context,mView:PairedDevicesContract.View){
        this.mCtx=mCtx
        this.mView=mView
        mBto = BTOperator()
    }


    override fun getList() {
        mBto!!.BTO_EnableBluetooth()


        val strList = mBto!!.BTO_GetBondedDevice()

        val iterator = strList.iterator()
        val devices=  ArrayList<Impresora>()
        while (iterator.hasNext()) {
            val device = iterator.next() as BluetoothDevice
            val strMac = device.address

            Log.e("MAC=>>",device.address)
            Log.e("MAC=>>",device.name)
            devices.add(Impresora(device.address,device.name))
//            Log.e("MAC=>>", String(device.type))


//            if (strMac == "00:15:83:")
//            {
//                btList.add(device.name)
//                btList.add(device.address)
//            }
        }

        if (!devices.isEmpty()) mView!!.showList(devices)


//        for (i in strList.indices) {
//            if (strList.get(i) == deviceName) {
//                if (mobileprint.PRTConnectDevices(strList, i + 1)) {
//                    // connect succeed
//                    return true
//                }
//            }
//        }
//        return false
    }

    override fun goSelected() {
        TODO("Not yet implemented")
    }
}