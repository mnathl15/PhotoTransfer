package com.example.phototransfer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.os.ParcelUuid
import android.support.design.widget.Snackbar
import android.text.format.Time
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class ClientThread(device: BluetoothDevice,bluetoothAdapter: BluetoothAdapter?) : Thread() {

    val device = device
    val bluetoothAdapter = bluetoothAdapter

    private val PORT_NUMBER = 25

    private val TIMEOUTNUM = 100000
    private lateinit var output: OutputStream
    private lateinit var fallbackSocket: BluetoothSocket
    private var uuid:UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


     val mmSocket: BluetoothSocket by lazy(LazyThreadSafetyMode.NONE) {

        device.createRfcommSocketToServiceRecord(uuid)
    }

    override fun run() {

        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()
        var socket = device.createInsecureRfcommSocketToServiceRecord(uuid)


        var clazz = socket.remoteDevice.javaClass
        var paramTypes = arrayOf<Class<*>>(Integer.TYPE)
        var m = clazz.getMethod("createRfcommSocket", *paramTypes)
        fallbackSocket = m.invoke(socket.remoteDevice, Integer.valueOf(PORT_NUMBER)) as BluetoothSocket

        val date = Date()

        var startTime = Date().time
        var timeElapsed:Long = 0




        while(timeElapsed < TIMEOUTNUM){

            try {
                fallbackSocket.connect()


            } catch (e: Exception) {
                e.printStackTrace()

            }

            timeElapsed = Date().time - startTime

            Log.d("TAG","CURRENT TIME ELAPSED: " + timeElapsed)



        }






    }


    fun write(byte:ByteArray){
        output = fallbackSocket.outputStream
        output.write(byte)

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
