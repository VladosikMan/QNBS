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
    var qnb : ArrayList<DataQNB> = ArrayList()
        set(value){
            field = value
        }
        get() = field
    init{

    }
}