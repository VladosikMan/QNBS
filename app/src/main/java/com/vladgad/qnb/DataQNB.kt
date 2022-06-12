package com.vladgad.qnb

import android.bluetooth.le.ScanRecord
import com.vladgad.qnb.model.EmvCard

data class DataQNB(
    var type:Int,
    var qrData: QrData?,
    var emvCard: EmvCard?,
    var scanRecord: ScanRecord?
)