package com.vladgad.qnb

import com.vladgad.qnb.model.EmvCard

data class Data (
    var qrData: QrData = QrData(),
    var emvCard: EmvCard = EmvCard()
)