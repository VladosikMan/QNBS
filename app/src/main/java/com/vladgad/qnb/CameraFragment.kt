package com.vladgad.qnb

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.frag_qr_scanner.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(R.layout.frag_qr_scanner) {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var analyzer: MyImageAnalyzer
    private lateinit var imageAnalysis : ImageAnalysis
    private lateinit var scanText : TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)

        checkCameraPermission();
        analyzer = MyImageAnalyzer(this, activity!!.supportFragmentManager)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = activity?.let { ProcessCameraProvider.getInstance(it.applicationContext) } as ListenableFuture<ProcessCameraProvider>

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(activity!!.applicationContext))
    }
    private fun init(view : View){
        scanText = view.findViewById(R.id.resultScan)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.createSurfaceProvider(null))

        imageAnalysis= ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview
        )
    }

    // функция по правке прав на съемку
    private fun checkCameraPermission() {
        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
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

    override fun onDestroyView() {
        cameraProviderFuture.get().unbindAll()
        super.onDestroyView()
    }

    private fun resultScan(barcode: Barcode) {
        scanText.text = barcode.rawValue.toString()
    }
    class MyImageAnalyzer(public var cameraFragment: CameraFragment, val manager: FragmentManager) : ImageAnalysis.Analyzer {


        override fun analyze(imageProxy: ImageProxy) {
            scanBarcode(imageProxy)

        }

        @SuppressLint("UnsafeExperimentalUsageError")
        private fun scanBarcode(imageProxy: ImageProxy) {
            imageProxy.image?.let { image ->
                val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()
                scanner.process(inputImage)
                    .addOnCompleteListener {
                        imageProxy.close()
                        if (it.isSuccessful) {
                            readBarcodeData(it.result as List<Barcode>)
                        } else {
                            it.exception?.printStackTrace()
                        }
                    }
            }
        }

        private fun readBarcodeData(barcodes: List<Barcode>) {
            for (barcode in barcodes) {
                var str : String;
                str = barcode.rawValue.toString();
                Log.d("mTag",str);
                cameraFragment.resultScan(barcode)
                var qrData : QrData = QrData(barcode.valueType,barcode.rawValue.toString())
                AppSingleton.qnb.add(DataQNB(0,qrData,null,null))
                manager.popBackStack()
                break

                /*  when (barcode.valueType) {
                          var str : String;
                          str = barcode.rawValue.toString();
                          Log.d("mTag",str);
                      *//*Barcode.TYPE_URL -> {
                    if (!bottomSheet.isAdded)
                        bottomSheet.show(fragmentManager, "")
                    bottomSheet.updateURL(barcode.url?.url.toString())
                }
                Barcode.TYPE_TEXT ->{
                    var str : String;
                    str = barcode.rawValue.toString();
                    Log.d("mTag",str);
                }*//*
            }*/



            }
        }
    }
}