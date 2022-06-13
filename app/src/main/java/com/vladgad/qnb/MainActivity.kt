package com.vladgad.qnb

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
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
    private lateinit var statusPost : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
      //  checkCameraPermission();
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
        statusPost = findViewById(R.id.statusPost)
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
               val status : HttpStatusCode = sendDataPost()
            }
        }
    }

    //отправка данных
    private suspend fun sendDataPost() : HttpStatusCode {
        var gson = Gson()
        var jsonString = gson.toJson(AppSingleton.qnb)
        Log.d("mTag", jsonString)
        val client = HttpClient(CIO)

        val response: HttpResponse = client.post("https://ktor.io/") {
             headers {
                 append(HttpHeaders.Accept, "application/json")
             }
             setBody(jsonString)
        }
        Log.d("mTag", response.status.toString())
        if(response.status == HttpStatusCode.OK){
           // statusPost.text = "Success - " + response.status.toString()
            AppSingleton.qnb.clear()
            AppSingleton.cardList.clear()
            AppSingleton.scanList.clear()
            AppSingleton.qrList.clear()
        }
        
        return response.status
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