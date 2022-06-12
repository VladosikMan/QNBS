package com.vladgad.qnb

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.vladgad.qnb.model.EmvCard


class BeaconFragment : Fragment(R.layout.frag_beacon_receiver) {

    lateinit var bluetoothManager: BluetoothManager
    lateinit var adapter: BluetoothAdapter
    lateinit var scanner: BluetoothLeScanner
    private var scanning = false
    private var veryBad = true
    private val handler = Handler()
    private lateinit var lv: ListView
    private lateinit var beaconAdapter: BeaconAdapter

    private lateinit var buttonBle: Button
    private lateinit var conditionText: TextView
    private lateinit var beaconList: ArrayList<ScanRecord>
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 20000

    //adapter
    private class BeaconAdapter(public val listBeacon: ArrayList<ScanRecord>, context: Context) :
        BaseAdapter() {
        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return listBeacon.size
        }

        override fun getItem(p0: Int): Any {
            return listBeacon.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.item_beacon, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }
            // задание свойств
            vh.scanText.text = listBeacon[position].toString()
            return view
        }

        private class ListRowHolder(row: View?) {
            //описание элемента
            public val scanText: TextView
            init {
                this.scanText = row?.findViewById(R.id.scanText) as TextView
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        beaconList = AppSingleton.scanList
        init(view)
        checkBeaconPermission()
        bluetoothManager =
            getActivity()?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adapter = bluetoothManager.getAdapter()
        scanner = adapter.bluetoothLeScanner
        if (adapter == null) {
            // Device does not support Bluetooth
        } else if (!adapter.isEnabled()) {
            // Bluetooth is not enabled :)

            // NFC is available for device but not enabled
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startActivityForResult(Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 2265)
                } catch (ignored: ActivityNotFoundException) {
                }

            } else {
                try {
                    startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                } catch (ignored: ActivityNotFoundException) {
                }
            }
        } else {
            // Bluetooth is enabled
            scanner = adapter.bluetoothLeScanner
            updateInterface(1)
        }
        //adapter =  BluetoothAdapter.getDefaultAdapter();

    }

    private fun init(view: View) {
        lv = view.findViewById(R.id.cardList) as ListView
        beaconAdapter = BeaconAdapter(beaconList, requireContext())
        lv.adapter = beaconAdapter
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
            //beaconList.add(result.scanRecord!!)
            AppSingleton.scanList.add(result.scanRecord!!)
            AppSingleton.qnb.add(DataQNB(2,null,null,result.scanRecord))
            Log.d("mTag", result.scanRecord.toString())
        }
    }

    // функция по правке прав на съемку
    private fun checkBeaconPermission() {
        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION

                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            Intent().also {
                it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                it.data = Uri.fromParts("package", activity?.packageName, null)
                startActivity(it)
                activity?.finish()
            }
        }
    }

}