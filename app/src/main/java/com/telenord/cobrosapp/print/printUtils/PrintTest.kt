package com.telenord.cobrosapp.print.printUtils

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log

class PrintTest
/**
 * @param device the device
 * @param secure if connection should be done via a secure socket
 * @param adapter the Android BT adapter
 * @param uuidCandidates a list of UUIDs. if null or empty, the Serial PP id is used
 */
(private val device: BluetoothDevice, private val secure: Boolean, uuidCandidates: UUID) {

    private var bluetoothSocket: BluetoothSocketWrapper? = null
    private var uuidCandidates: UUID? = null

    init {
        this.uuidCandidates = uuidCandidates

    }

    @Throws(IOException::class)
    fun connect(): BluetoothSocketWrapper {
        var success = false
        if (selectSocket()) {


            try {
                bluetoothSocket!!.connect()
                success = true

            } catch (e: IOException) {
                //try the fallback
                try {
                    bluetoothSocket = FallbackBluetoothSocket(bluetoothSocket!!.underlyingSocket)
                    Thread.sleep(500)
                    bluetoothSocket!!.connect()
                    success = true

                } catch (e1: FallbackException) {
                    Log.w("BT", "Could not initialize FallbackBluetoothSocket classes.", e)
                } catch (e1: InterruptedException) {
                    Log.w("BT", e1.message, e1)
                } catch (e1: IOException) {
                    Log.w("BT", "Fallback failed. Cancelling.", e1)
                    bluetoothSocket!!.close()
                }

            }

        }

        if (!success) {
            throw IOException("Could not connect to device: " + device.address)
        }

        return bluetoothSocket!!
    }

    @Throws(IOException::class)
    private fun selectSocket(): Boolean {


        val tmp: BluetoothSocket
        val uuid = uuidCandidates!!

        Log.i("BT", "Attempting to connect to Protocol: $uuid")
        if (secure) {
            tmp = device.createRfcommSocketToServiceRecord(uuid)
        } else {
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid)
        }
        bluetoothSocket = NativeBluetoothSocket(tmp)

        return true
    }

    interface BluetoothSocketWrapper {

        val inputStream: InputStream

        val outputStream: OutputStream

        val remoteDeviceName: String

        val remoteDeviceAddress: String

        val underlyingSocket: BluetoothSocket

        @Throws(IOException::class)
        fun connect()

        @Throws(IOException::class)
        fun close()

    }


    open class NativeBluetoothSocket(override val underlyingSocket: BluetoothSocket) : BluetoothSocketWrapper {

        override val inputStream: InputStream
            @Throws(IOException::class)
            get() = underlyingSocket.inputStream

        override val outputStream: OutputStream
            @Throws(IOException::class)
            get() = underlyingSocket.outputStream

        override val remoteDeviceName: String
            get() = underlyingSocket.remoteDevice.name

        override val remoteDeviceAddress: String
            get() = underlyingSocket.remoteDevice.address

        @Throws(IOException::class)
        override fun connect() {
            underlyingSocket.connect()
        }

        @Throws(IOException::class)
        override fun close() {
            underlyingSocket.close()
            Log.e("Printer Test","${underlyingSocket!!.isConnected}")
        }

    }

    inner class FallbackBluetoothSocket @Throws(FallbackException::class)
    constructor(tmp: BluetoothSocket) : NativeBluetoothSocket(tmp) {

        private var fallbackSocket: BluetoothSocket? = null

        override val inputStream: InputStream
            @Throws(IOException::class)
            get() = fallbackSocket!!.inputStream

        override val outputStream: OutputStream
            @Throws(IOException::class)
            get() = fallbackSocket!!.outputStream

        init {
            try {
                val clazz = tmp.remoteDevice.javaClass
                val paramTypes = arrayOf<Class<*>>(Integer.TYPE)
                val m = clazz.getMethod("createRfcommSocket", *paramTypes)
                val params = arrayOf<Any>(Integer.valueOf(1))
                fallbackSocket = m.invoke(tmp.remoteDevice, *params) as BluetoothSocket
            } catch (e: Exception) {
                throw FallbackException(e)
            }
        }

        @Throws(IOException::class)
        override fun connect() {
            fallbackSocket!!.connect()
        }

        @Throws(IOException::class)
        override fun close() {
            fallbackSocket!!.close()
            Log.e("Printer Test","${fallbackSocket!!.isConnected}")
        }
    }

    class FallbackException(e: Exception) : Exception(e) {
        companion object {
            private val serialVersionUID = 1L
        }

    }
}