package com.vladgad.qnb

import android.content.ActivityNotFoundException
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.fragment.app.Fragment
import com.vladgad.qnb.model.EmvCard


class NfcFragment : Fragment(R.layout.frag_nfc_card_reader),
    SimpleCardReader.SimpleCardReaderCallback, NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireActivity())
        if (nfcAdapter == null) {
            Log.d("mTag","No NFC on this device")
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
        //Toast.makeText(this, info, Toast.LENGTH_LONG).show()
    }

    override fun cardMovedTooFastOrLockedNfc() {
        Log.d("mTag", "Tap again")
    }

    override fun errorReadingOrUnsupportedCard() {
        Log.d("mTag", "Error / Unsupported")
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