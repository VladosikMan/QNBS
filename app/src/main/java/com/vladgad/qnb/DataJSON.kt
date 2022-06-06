package com.vladgad.qnb

import com.vladgad.qnb.model.EmvCard

data class DataJSON(
    var qrDataList: ArrayList<QrData> = ArrayList(),
    var emvCard: ArrayList<EmvCard> = ArrayList()
)
