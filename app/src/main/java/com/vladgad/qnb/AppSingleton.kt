package com.vladgad.qnb


import android.bluetooth.le.ScanRecord
import com.vladgad.qnb.model.EmvCard

object AppSingleton{
    var cardList : ArrayList<EmvCard> = ArrayList()
    set(value){
        field = value
    }
    get() = field
    var qrList : ArrayList<QrData> = ArrayList()
        set(value){
            field = value
        }
        get() = field
    var scanList : ArrayList<ScanRecord> = ArrayList()
        set(value){
            field = value
        }
        get() = field
    init{
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

        cardList.add(card)
        cardList.add(card2)
        cardList.add(card3)
    }
}