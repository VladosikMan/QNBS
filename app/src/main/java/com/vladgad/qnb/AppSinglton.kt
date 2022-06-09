package com.vladgad.qnb

import android.app.Application
import android.bluetooth.le.ScanRecord
import com.vladgad.qnb.model.EmvCard

class AppSinglton : Application() {
    //класс синглтон
    lateinit var cardList : ArrayList<EmvCard>
    lateinit var qrList : ArrayList<QrData>
    lateinit var scanList : ArrayList<ScanRecord>
    override fun onCreate() {
        super.onCreate()
        cardList = ArrayList()
        qrList = ArrayList()
        scanList = ArrayList()
    }
}