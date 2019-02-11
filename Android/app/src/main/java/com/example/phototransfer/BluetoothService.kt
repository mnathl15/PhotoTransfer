package com.example.phototransfer

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothService(
    // handler that gets info from Bluetooth service
    private val mHandler: Handler
) {

      class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream

        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d("TAG", "Input stream was disconnected", e)
                    break
                }


            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {

            try {
                Log.d("TAG", "HERE IS OUTPUT")
                mmSocket.connect()
                val mmOutStream = mmSocket.outputStream
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e("TAG", "Error occurred when sending data", e)

                // Send a failure message back to the activity.

                return
            }

        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e("TAG", "Could not close the connect socket", e)
            }
        }
    }
}