package com.telenord.cobrosapp.print

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.UUID

class BluetoothService(context: Context, private val mHandler: Handler) {
    private val mAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mAcceptThread: BluetoothService.AcceptThread? = null
    private var mConnectThread: BluetoothService.ConnectThread? = null
    private var mConnectedThread: ConnectedThread? = null
    @get:Synchronized
    var state = 0
        @Synchronized private set(state) {
            field = state
            this.mHandler.obtainMessage(1, state, -1).sendToTarget()
        }

    val isAvailable: Boolean
        @Synchronized get() = this.mAdapter != null

    val isBTopen: Boolean
        @Synchronized get() = this.mAdapter!!.isEnabled

    val pairedDev: Set<BluetoothDevice>?
        @Synchronized get() {
            var dev: Set<BluetoothDevice>? = null
            dev = this.mAdapter!!.bondedDevices
            return dev
        }

    val isDiscovering: Boolean
        @Synchronized get() = this.mAdapter!!.isDiscovering

    @Synchronized
    fun getDevByMac(mac: String): BluetoothDevice {
        return this.mAdapter!!.getRemoteDevice(mac)
    }

    @Synchronized
    fun getDevByName(name: String): BluetoothDevice? {
        var tem_dev: BluetoothDevice? = null
        val pairedDevices = this.pairedDev
        if (pairedDevices!!.size > 0) {
            val var5 = pairedDevices.iterator()

            while (var5.hasNext()) {
                val device = var5.next()
                if (device.name.indexOf(name) != -1) {
                    tem_dev = device
                    break
                }
            }
        }

        return tem_dev
    }

    @Synchronized
    fun sendMessage(message: String, charset: String) {
        if (message.length > 0) {
            var send: ByteArray
            try {
                send = message.toByteArray(charset(charset))
            } catch (var5: UnsupportedEncodingException) {
                send = message.toByteArray()
            }

            this.write(send)
            val tail = byteArrayOf(10, 13, 0)
            this.write(tail)
        }

    }

    @Synchronized
    fun cancelDiscovery(): Boolean {
        return this.mAdapter!!.cancelDiscovery()
    }

    @Synchronized
    fun startDiscovery(): Boolean {
        return this.mAdapter!!.startDiscovery()
    }

    @Synchronized
    fun start() {
        Log.d("BluetoothService", "start")
        if (this.mConnectThread != null) {
            this.mConnectThread!!.cancel()
            this.mConnectThread = null
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread!!.cancel()
            this.mConnectedThread = null
        }

        if (this.mAcceptThread == null) {
            this.mAcceptThread = AcceptThread()
            this.mAcceptThread!!.start()
        }

        this.state = 1
    }

    @Synchronized
    fun connect(device: BluetoothDevice) {
        Log.d("BluetoothService", "connect to: $device")
        this.state = 0
        this.mConnectThread = null
        if (this.state == 2 && this.mConnectThread != null) {
            this.mConnectThread!!.cancel()
            this.mConnectThread = null
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread!!.cancel()
            this.mConnectedThread = null
        }

        this.mConnectThread = ConnectThread(device)
        this.mConnectThread!!.start()
        this.state = 2
    }

    @Synchronized
    fun connected(socket: BluetoothSocket, device: BluetoothDevice) {
        Log.d("BluetoothService", "connected")
        if (this.mConnectThread != null) {
            this.mConnectThread!!.cancel()
            this.mConnectThread = null }

        if (this.mConnectedThread != null) {
            this.mConnectedThread!!.cancel()
            this.mConnectedThread = null }

        if (this.mAcceptThread != null) {
            this.mAcceptThread!!.cancel()
            this.mAcceptThread = null
        }

        this.mConnectedThread = ConnectedThread(socket)
        this.mConnectedThread!!.start()
        val msg = this.mHandler.obtainMessage(4)
        this.mHandler.sendMessage(msg)
        this.state = 3
    }

    @Synchronized
    fun stop() {
        Log.d("BluetoothService", "stop")
        this.state = 0
        if (this.mConnectThread != null) {
            this.mConnectThread!!.cancel()
            this.mConnectThread = null
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread!!.cancel()
            this.mConnectedThread = null
        }

        if (this.mAcceptThread != null) {
            this.mAcceptThread!!.cancel()
            this.mAcceptThread = null
        }

    }

    fun write(out: ByteArray) {
        val r: ConnectedThread?
        synchronized(this) {
            if (this.state != 3) {
                return
            }

            r = this.mConnectedThread
        }

        r!!.write(out)
    }
    fun socket(): BluetoothSocket?{
        val r: ConnectedThread?
        synchronized(this) {
            if (this.state != 3) {
                return null
            }

            r = this.mConnectedThread
        }
        return r!!.getSocket()
    }

    private fun connectionFailed() {
        this.state = 1
        val msg = this.mHandler.obtainMessage(6)
        this.mHandler.sendMessage(msg)
    }

    private fun connectionLost() {
        val msg = this.mHandler.obtainMessage(5)
        this.mHandler.sendMessage(msg)
    }

    private inner class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket?

        init {
            var tmp: BluetoothServerSocket? = null

            try {
                tmp = this@BluetoothService.mAdapter!!.listenUsingRfcommWithServiceRecord("BTPrinter", BluetoothService.MY_UUID)
            } catch (var4: IOException) {
                Log.e("BluetoothService", "listen() failed", var4)
            }
            this.mmServerSocket = tmp
        }

        override fun run() {
            Log.d("BluetoothService", "BEGIN mAcceptThread$this")
            this.name = "AcceptThread"
            var socket: BluetoothSocket? = null

            while (this@BluetoothService.state != 3) {
                Log.d("Hilo de ejecucion", "Ejecutando....")

                try {
                    socket = this.mmServerSocket!!.accept()
                } catch (var6: IOException) {
                    Log.e("BluetoothService", "accept() failed", var6)
                    break
                }

                if (socket != null) {
                    val var2 = this@BluetoothService
                    synchronized(this@BluetoothService) {
                        when (this@BluetoothService.state) {
                            0, 3 -> try {
                                socket.close()
                            } catch (var4: IOException) {
                                Log.e("BluetoothService", "Could not close unwanted socket", var4)
                            }

                            1, 2 -> this@BluetoothService.connected(socket, socket.remoteDevice)
                            else -> try {
                                socket.close()
                            } catch (var4: IOException) {
                                Log.e("BluetoothService", "Could not close unwanted socket", var4)
                            }
                        }
                    }
                }
            }

            Log.i("BluetoothService", "END mAcceptThread")
        }

        fun cancel() {
            Log.d("BluetoothService", "cancel $this")

            try {
                this.mmServerSocket!!.close()
            } catch (var2: IOException) {
                Log.e("BluetoothService", "close() of server failed", var2)
            }

        }
    }

    private inner class ConnectThread(private val mmDevice: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket?

        init {
            var tmp: BluetoothSocket? = null

            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(BluetoothService.MY_UUID)
            } catch (var5: IOException) {
                Log.e("BluetoothService", "create() failed", var5)
            }

            this.mmSocket = tmp
        }

        override fun run() {
            Log.i("BluetoothService", "BEGIN mConnectThread")
            this.name = "ConnectThread"
            this@BluetoothService.mAdapter!!.cancelDiscovery()

            try {
                this.mmSocket!!.connect()
            } catch (var5: IOException) {
                this@BluetoothService.connectionFailed()

                try {
                    this.mmSocket!!.close()
                } catch (var3: IOException) {
                    Log.e("BluetoothService", "unable to close() socket during connection failure", var3)
                }

                this@BluetoothService.start()
                return
            }

            val var1 = this@BluetoothService
            synchronized(this@BluetoothService) {
                this@BluetoothService.mConnectThread = null
            }

            this@BluetoothService.connected(this.mmSocket, this.mmDevice)
        }

        fun cancel() {
            try {
                this.mmSocket!!.close()
            } catch (var2: IOException) {
                Log.e("BluetoothService", "close() of connect socket failed", var2)
            }

        }
    }



    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        init {
            Log.d("BluetoothService", "create ConnectedThread")
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (var6: IOException) {
                Log.e("BluetoothService", "temp sockets not created", var6)
            }

            this.mmInStream = tmpIn
            this.mmOutStream = tmpOut
        }

        override fun run() {
            Log.d("Conexion de Hilo", "Conectando......")
            Log.i("BluetoothService", "BEGIN mConnectedThread")

            try {
                while (true) {
                    val buffer = ByteArray(256)
                    val bytes = this.mmInStream!!.read(buffer)
                    if (bytes <= 0) {
                        Log.e("BluetoothService", "disconnected")
                        this@BluetoothService.connectionLost()
                        if (this@BluetoothService.state != 0) {
                            Log.e("BluetoothService", "disconnected")
                            this@BluetoothService.start()
                        }
                        break
                    }
                    this@BluetoothService.mHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget()
                }
            } catch (var3: IOException) {
                Log.e("BluetoothService", "disconnected", var3)
                this@BluetoothService.connectionLost()
                if (this@BluetoothService.state != 0) {
                    this@BluetoothService.start()
                }
            }

        }

        fun write(buffer: ByteArray) {
            try {
                this.mmOutStream!!.write(buffer)
                this@BluetoothService.mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget()
            } catch (var3: IOException) {
                Log.e("BluetoothService", "Exception during write", var3)
            }

        }
        fun getOutstream(): OutputStream?{
            return this.mmOutStream
        }

        fun getSocket(): BluetoothSocket{
            return this.mmSocket
        }

        fun cancel() {
            try {
                this.mmSocket.close()
            } catch (var2: IOException) {
                Log.e("BluetoothService", "close() of connect socket failed", var2)
            }

        }
    }

    companion object {
        private val TAG = "BluetoothService"
        private val D = true
        val MESSAGE_STATE_CHANGE = 1
        val MESSAGE_READ = 2
        val MESSAGE_WRITE = 3
        val MESSAGE_DEVICE_NAME = 4
        val MESSAGE_CONNECTION_LOST = 5
        val MESSAGE_UNABLE_CONNECT = 6
        private val NAME = "BTPrinter"
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val STATE_NONE = 0
        val STATE_LISTEN = 1
        val STATE_CONNECTING = 2
        val STATE_CONNECTED = 3
    }
}