package com.example.phototransfer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_send_photos.*

import kotlinx.android.synthetic.main.activity_send_photos.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI

class SendPhotos : AppCompatActivity() {

    private lateinit var currentBitmap: Bitmap
    private lateinit var device:Any
    private lateinit var socket: BluetoothSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photos)


        device = intent.extras.get("Device")


        makeDiscoverable()

        send_photo.setOnClickListener{
            val intent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, 0)


        }









    }


    private fun makeDiscoverable(){
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600)
        }
        startActivity(discoverableIntent)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            val targetUri:Uri = data!!.data


            try{
                currentBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(targetUri))
                image.setImageBitmap(currentBitmap)

                val stream:ByteArrayOutputStream = ByteArrayOutputStream()
                currentBitmap.compress(Bitmap.CompressFormat.PNG,100,stream)

                val byteArray:ByteArray = stream.toByteArray()

                connectToServer()
                sendPhoto(byteArray)


            }
            catch(e:Exception){
                e.printStackTrace()
            }

        }
    }


    private fun connectToServer(){

        val bluetoothAdapter:BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()


        val connThread = ClientThread(device as BluetoothDevice,bluetoothAdapter)

        try{

            connThread.run()
            socket = connThread.mmSocket

        }
        catch(e:Exception){
            e.printStackTrace()
        }


    }



    private fun sendPhoto(byteArray: ByteArray) {


        val bluetoothService: BluetoothService.ConnectedThread = BluetoothService.ConnectedThread(socket)
        

        bluetoothService.write(byteArray)



    }

}
