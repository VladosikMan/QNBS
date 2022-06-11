package com.vladgad.qnb

import android.app.Application
import android.bluetooth.le.ScanRecord
import com.vladgad.qnb.model.EmvCard

class AppSinglton : Application() {
    //класс синглтон
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
    override fun onCreate() {
        super.onCreate()
    }
}