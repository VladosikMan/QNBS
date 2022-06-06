package com.vladgad.qnb
import com.google.mlkit.vision.barcode.Barcode

data class QrData(
    var qrType: Int = 0,
    var qrRawData: String = ""
)
