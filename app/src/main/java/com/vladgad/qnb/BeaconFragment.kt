package com.vladgad.qnb

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment


class BeaconFragment : Fragment(R.layout.frag_beacon_receiver) {

    lateinit var bluetoothManager: BluetoothManager
    lateinit var adapter: BluetoothAdapter
    lateinit var scanner: BluetoothLeScanner
    private var scanning = false
    private var veryBad = true
    private val handler = Handler()

    private lateinit var buttonBle: Button
    private lateinit var conditionText: TextView

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 20000


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        bluetoothManager =
            getActivity()?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adapter = bluetoothManager.getAdapter()
        //adapter =  BluetoothAdapter.getDefaultAdapter();
        scanner = adapter.bluetoothLeScanner
        updateInterface(1)
    }

    private fun init(view: View) {
        buttonBle = view.findViewById(R.id.bleButton)
        buttonBle.setOnClickListener {

            if (veryBad) {
                scanning = false
                scanLeDevice()
                veryBad = false
                updateInterface(0)
            } else {
                veryBad = true
                scanner.stopScan(leScanCallback)
                updateInterface(1)
            }
        }
        conditionText = view.findViewById(R.id.conditionText)
    }


    private fun scanLeDevice() {

        scanner.startScan(leScanCallback)
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                scanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            Log.d("mTag", "11")
            scanner.startScan(leScanCallback)
        } else {
            scanning = false
            scanner.stopScan(leScanCallback)
        }
    }

    private fun updateInterface(state: Int) {
        when (state) {
            0 -> {
                conditionText.setText("Прослушивание включено")
                buttonBle.setText("Выключить")
            }
            1 -> {
                conditionText.setText("Прослушивание выключено")
                buttonBle.setText("Включить")
            }
        }
    }

    //private val leDeviceListAdapter = LeDeviceListAdapter()
    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            Log.d("mTag", result.scanRecord.toString())

        }
    }

}