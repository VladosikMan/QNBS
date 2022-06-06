package com.vladgad.qnb

import com.vladgad.qnb.model.EmvCard

data class DataQNB (
    var qrData: QrData = QrData(),
    var emvCard: EmvCard = EmvCard()
)