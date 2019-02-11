package com.example.phototransfer

import android.bluetooth.BluetoothDevice


data class Device(val name:String,val address: String,val bluetoothDevice: BluetoothDevice, val paired:Boolean)
