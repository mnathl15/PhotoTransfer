package com.example.phototransfer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.recycler_device.view.*
import java.util.*

class DeviceAdapter(private val devices: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<DeviceAdapter.MyViewHolder>() {



   class MyViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DeviceAdapter.MyViewHolder {
        // create a new view
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_device, parent, false) as LinearLayout

        return MyViewHolder(layout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.layout.device_name.text = devices.get(position).name
        holder.layout.device_address.text = devices.get(position).address

        holder.layout.linear_layout.setOnClickListener { view: View? ->

            //Makes device discoverable
            val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
            view?.context?.startActivity(discoverableIntent)


            val intent = Intent(Intent(view?.context,SendPhotos::class.java))
            intent.putExtra("Device",devices.get(position))

            view?.context?.startActivity(intent)



        }





    }




    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount():Int{


        return devices.size

    }
}