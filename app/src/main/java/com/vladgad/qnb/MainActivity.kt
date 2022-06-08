package com.vladgad.qnb

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.vladgad.qnb.model.EmvCard
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import java.util.concurrent.ExecutorService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var buttonScanner: Button
    private lateinit var buttonNFC: Button
    private lateinit var buttonBeacon: Button
    private lateinit var buttonSendURL: Button
    private lateinit var cameraFragment: CameraFragment
    private lateinit var nfcFragment: NfcFragment
    private lateinit var beaconFragment: BeaconFragment
    private lateinit var mainFragment: MainFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        checkCameraPermission();
        if (savedInstanceState == null) {
            // 2
            supportFragmentManager
                // 3
                .beginTransaction()

                // 4|
                .add(R.id.fragment_container_view, mainFragment)
                .setReorderingAllowed(true)
                // 5
                .commit()
        }

    }

    //init
    private fun init() {
        buttonScanner = findViewById(R.id.buttonScanner)
        buttonNFC = findViewById(R.id.buttonNFC)
        buttonBeacon = findViewById(R.id.buttonBeacon)
        buttonSendURL = findViewById(R.id.buttonSendURL)
        buttonWork()
        cameraFragment = CameraFragment()
        nfcFragment = NfcFragment()
        beaconFragment = BeaconFragment()
        mainFragment = MainFragment()
    }

    private fun buttonWork() {

        buttonScanner.setOnClickListener {
            //переход к камере
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, mainFragment)
                .setReorderingAllowed(true)
                .commit()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, cameraFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }


        buttonNFC.setOnClickListener {
            //переход к nfc

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, mainFragment)
                .setReorderingAllowed(true)
                .commit()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, nfcFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }

        buttonBeacon.setOnClickListener {
            //переход к beacon
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, mainFragment)
                .setReorderingAllowed(true)
                .commit()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, beaconFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }

        buttonSendURL.setOnClickListener {
            GlobalScope.launch(Unconfined) {
                sendDataPost()
            }
        }
    }

    //отправка данных
    private suspend fun sendDataPost() {
        var gson = Gson()

        var dataJSON: DataJSON = DataJSON()


        var card: EmvCard = EmvCard()
        card.cardNumber = "2200 2407 3152 2914"
        card.expireDateMonth = "12"
        card.expireDateYear = "30"


        var card2: EmvCard = EmvCard()
        card2.cardNumber = "2200 2407 3152 2914"
        card2.expireDateMonth = "12"
        card2.expireDateYear = "30"

        var card3: EmvCard = EmvCard()
        card3.cardNumber = "2200 2407 3152 2914"
        card3.expireDateMonth = "12"
        card3.expireDateYear = "30"
        dataJSON.emvCard.add(card)
        dataJSON.emvCard.add(card2)
        dataJSON.emvCard.add(card3)

        var qrData: QrData = QrData()
        qrData.qrType = 2
        qrData.qrRawData = "ewr"

        var qrData2: QrData = QrData()
        qrData2.qrType = 1
        qrData2.qrRawData = "ewr"

        var qrData3: QrData = QrData()
        qrData3.qrType = 3
        qrData3.qrRawData = "ewr"

        dataJSON.qrDataList.add(qrData)
        dataJSON.qrDataList.add(qrData2)
        dataJSON.qrDataList.add(qrData3)

        var jsonString = gson.toJson(dataJSON)
        Log.d("mTag", jsonString)
        val client = HttpClient(CIO)
        /*  val response: HttpResponse = client.post("http://localhost:8080/post") {
              headers{
                  append(HttpHeaders.Accept,"")
              }
              setBody(jsonString)
          }*/

        val response: HttpResponse = client.post("https://ktor.io/") {
            /* headers {
                 append(HttpHeaders.Accept, "application/json")
             }
             setBody(jsonString)*/
        }
        Log.d("mTag", response.status.toString())

        client.close()
    }

    // функция по правке прав на съемку
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Intent().also {
                it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                it.data = Uri.fromParts("package", packageName, null)
                startActivity(it)
                finish()
            }
        }
    }


}