package com.vladgad.qnb

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView


import androidx.fragment.app.Fragment
import com.vladgad.qnb.model.EmvCard



class NfcFragment : Fragment(R.layout.frag_nfc_card_reader),
    SimpleCardReader.SimpleCardReaderCallback, NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var cardList: ArrayList<EmvCard>
    private lateinit var lv: ListView
    private lateinit var cardAdapter: CardAdapter
    private lateinit var statusText: TextView



    //адаптер
    private class CardAdapter(public val listCard: ArrayList<EmvCard>, context: Context) :
        BaseAdapter() {
        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return listCard.size
        }

        override fun getItem(p0: Int): Any {
            return listCard.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.item_card, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }
            // задание свойств
            vh.holderName.text = listCard[position].holderName
            vh.cardNumber.text = listCard[position].cardNumber
            vh.mounth.text = listCard[position].expireDateMonth
            vh.year.text = listCard[position].expireDateYear
            return view
        }


        private class ListRowHolder(row: View?) {
            //описание элемента
            public val holderName: TextView
            public val cardNumber: TextView
            public val mounth: TextView
            public val year: TextView

            init {
                this.holderName = row?.findViewById(R.id.holderNameText) as TextView
                this.cardNumber = row?.findViewById(R.id.cardNumberText) as TextView
                this.mounth = row?.findViewById(R.id.monthText) as TextView
                this.year = row?.findViewById(R.id.yearText) as TextView
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //val appSinglton: AppSinglton
        cardList = AppSingleton.cardList
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireActivity())
        if (nfcAdapter == null) {
            Log.d("mTag", "No NFC on this device")
            //Toast.makeText(, "No NFC on this device", Toast.LENGTH_LONG).show()
        } else if (nfcAdapter?.isEnabled == false) {

            // NFC is available for device but not enabled
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startActivityForResult(Intent(Settings.Panel.ACTION_NFC), 2265)
                } catch (ignored: ActivityNotFoundException) {
                }

            } else {
                try {
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                } catch (ignored: ActivityNotFoundException) {
                }
            }
        }
        init(view)

    }

    private fun init(view: View) {
        lv = view.findViewById(R.id.cardList) as ListView
        cardAdapter = CardAdapter(cardList, requireContext())
        lv.adapter = cardAdapter
        statusText = view.findViewById(R.id.statusText) as TextView

    }

    override fun cardIsReadyToRead(card: EmvCard) {
        val info = card.toString()
        Log.d("mTag", card.cardNumber)
        Log.d("mTag", card.expireDateMonth)
        Log.d("mTag", card.expireDateYear)
        Log.d("mTag", card.holderName)
        Log.d("mTag", card.secondCardNumber)
        Log.d("mTag", card.secondExpireDateMonth)
        Log.d("mTag", card.secondExpireDateYear)
        cardAdapter.listCard.add(card)
        cardAdapter.notifyDataSetChanged()
        statusText.text = "Success"
    }

    override fun cardMovedTooFastOrLockedNfc() {
        Log.d("mTag", "Tap again")
        statusText.text = "Tap again"

    }

    override fun errorReadingOrUnsupportedCard() {
        Log.d("mTag", "Error / Unsupported")
        statusText.text = "Error / Unsupported"
    }

    override fun onTagDiscovered(tag: Tag?) {
        SimpleCardReader.readCard(tag, this)
    }

    public override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            requireActivity(), this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(requireActivity())
    }
}