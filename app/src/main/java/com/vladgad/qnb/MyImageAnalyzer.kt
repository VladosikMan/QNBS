package com.vladgad.qnb

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.FragmentManager
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class MyImageAnalyzer(private val fragmentManager: FragmentManager) : ImageAnalysis.Analyzer {


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