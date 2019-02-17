package com.example.phototransfer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_send_photos.*

import kotlinx.android.synthetic.main.activity_send_photos.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.net.URI
import java.util.*

class SendPhotos : AppCompatActivity() {

    private lateinit var currentBitmap: Bitmap
    private lateinit var device:Any
    private lateinit var socket: BluetoothSocket
    private lateinit var clientThread: ClientThread
    private lateinit var fallbackSocket: BluetoothSocket
    private lateinit var output: OutputStream


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photos)


        try {
            device = intent.extras.get("Device")
        }catch (e:Exception){
            e.printStackTrace()
        }


        send_photo.setOnClickListener{
            val intent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, 0)


        }


        //Creates a handler so that activity can load before connection starts
        val handler = Handler()
        handler.postDelayed(connectToServer,5000)


    }



    private val connectToServer=Runnable{

        val bluetoothAdapter:BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()


        clientThread = ClientThread(device as BluetoothDevice,bluetoothAdapter)

        try{

            clientThread.execute()
            socket = clientThread.mmSocket

        }
        catch(e:Exception){
            e.printStackTrace()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            val targetUri:Uri = data!!.data


            try{

                currentBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(targetUri))
                image.setImageBitmap(currentBitmap)

                val stream = ByteArrayOutputStream()
                currentBitmap.compress(Bitmap.CompressFormat.PNG,100,stream)

                val byteArray:ByteArray = stream.toByteArray()

                val sendPhotoThread = SendPhotoThread()
                sendPhotoThread.execute(byteArray)



                error.setText("Trying to send photo")
                loading.visibility = View.VISIBLE


            }
            catch(e:Exception){
                e.printStackTrace()
            }

        }
    }






    //Connects to the python server
    inner class ClientThread(device: BluetoothDevice,bluetoothAdapter: BluetoothAdapter?) : AsyncTask<Void, Void, Boolean>() {

        val device = device
        val bluetoothAdapter = bluetoothAdapter

        private val PORT_NUMBER = 25

        private val TIMEOUTNUM = 100


        private var uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


        override fun doInBackground(vararg p0: Void?): Boolean {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()
            var socket = device.createInsecureRfcommSocketToServiceRecord(uuid)

            var clazz = socket.remoteDevice.javaClass
            var paramTypes = arrayOf<Class<*>>(Integer.TYPE)
            var m = clazz.getMethod("createRfcommSocket", *paramTypes)
            fallbackSocket = m.invoke(socket.remoteDevice, Integer.valueOf(PORT_NUMBER)) as BluetoothSocket

            var startTime = Date().time
            var timeElapsed: Long = 0
            while (timeElapsed < TIMEOUTNUM) {
                try {
                    fallbackSocket.connect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                timeElapsed = Date().time - startTime

            }




            return fallbackSocket.isConnected
        }


        override fun onPostExecute(result: Boolean) {
            loading.visibility = View.GONE
            if (result) {
                error.setText("Connection successful")
                send_photo.visibility = View.VISIBLE

            } else {
                error.setText("Could not connect")
                val backIntent: Intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(backIntent)
            }
        }


        val mmSocket: BluetoothSocket by lazy(LazyThreadSafetyMode.NONE) {

            device.createRfcommSocketToServiceRecord(uuid)
        }


    }

    //Thread for sending bytes
    private inner class SendPhotoThread():AsyncTask<ByteArray,Void,Boolean>() {


        override fun doInBackground(vararg p0 : ByteArray): Boolean {
            output = fallbackSocket.outputStream

            try {
                output.write(p0[0])
                return true
            } catch (io: IOException) {
                Log.d("TAG","HERE BE THE ERROR " + io.printStackTrace())
                return false
            }


        }


        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)


            loading.visibility = View.GONE
            output.close()

            val reIntent = Intent(applicationContext, SendPhotos::class.java)

            reIntent.putExtra("Device",device as BluetoothDevice)
            if(result == true) {
                error.setText("Photo sent successfully!")


            }
            else {
                error.setText("There was an issue sending the photo")
            }

            //restarts activity to re-connect to python server
            startActivity(reIntent)
        }



    }





}



