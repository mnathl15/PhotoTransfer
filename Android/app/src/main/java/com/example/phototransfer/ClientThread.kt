package com.example.phototransfer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.os.ParcelUuid
import android.support.design.widget.Snackbar
import android.util.Log
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ClientThread(device: BluetoothDevice,bluetoothAdapter: BluetoothAdapter?) : Thread() {

    val device = device
    val bluetoothAdapter = bluetoothAdapter

    private val PORT_NUMBER = 25

    private var uuid:UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

     val mmSocket: BluetoothSocket by lazy(LazyThreadSafetyMode.NONE) {

        device.createRfcommSocketToServiceRecord(uuid)
    }

    override fun run() {

        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()
        var socket = device.createInsecureRfcommSocketToServiceRecord(uuid)

        Log.d("TAG","HERE IS THE SOCKET: " + socket.isConnected)
        var clazz = socket.remoteDevice.javaClass
        var paramTypes = arrayOf<Class<*>>(Integer.TYPE)
        var m = clazz.getMethod("createRfcommSocket", *paramTypes)
        var fallbackSocket = m.invoke(socket.remoteDevice, Integer.valueOf(PORT_NUMBER)) as BluetoothSocket
        try {
            fallbackSocket.connect()
            // var stream = fallbackSocket.outputStream
           // stream.write(" "))
        } catch (e: Exception) {
            e.printStackTrace()
            //Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT).show()
        }
    }



    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }
}
