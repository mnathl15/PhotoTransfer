package com.example.phototransfer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_device.*
import kotlinx.android.synthetic.main.recycler_device.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var deviceList:ArrayList<BluetoothDevice>
    private lateinit var filter: IntentFilter
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        deviceList = ArrayList()




        viewManager = LinearLayoutManager(this)
        viewAdapter = DeviceAdapter(deviceList)

        recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }

        checkPermissions()

        setupBluetooth()
        findPairedDevices()
        checkForNearbyDevices()


    }




    fun setupBluetooth(){

        if(bluetoothAdapter?.isEnabled == false){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent,1)

        }



    }

    //Receives all paired devices
    fun findPairedDevices(){

        val pairedDevices:Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device->

            deviceList.add(device) //Adds another paired device to our list
            viewAdapter.notifyDataSetChanged()


        }

    }


    //Broadcast receiver for letting us know when we have found another bluetooth device
    private val bluetoothReceiver=object : BroadcastReceiver(){

        override fun onReceive(context:Context,intent:Intent){
            val action:String = intent.action



            when(action){

                BluetoothDevice.ACTION_FOUND -> {


                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)



                    if(!deviceList.contains(device)){
                        deviceList.add(device) // adds another unpaired device to our list
                    }

                    viewAdapter.notifyDataSetChanged()


                }



            }

        }

    }

    private fun checkForNearbyDevices(){

        filter  = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)


        registerReceiver(bluetoothReceiver,filter)

        bluetoothAdapter?.startDiscovery()

    }



    //Need to check user permission in order to detect bluetooth devices
    private fun checkPermissions(){


        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1);
        }
    }



    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(bluetoothReceiver)
    }
}
