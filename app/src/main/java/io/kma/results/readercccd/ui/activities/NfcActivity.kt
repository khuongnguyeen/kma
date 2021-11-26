package io.kma.results.readercccd.ui.activities

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast


import org.jmrtd.lds.icao.MRZInfo

import io.kma.results.readercccd.R
import io.kma.results.readercccd.data.CanCuoc
import io.kma.results.readercccd.ui.fragments.NfcFragment
import io.kma.results.readercccd.ui.fragments.CanCuocDetailsFragment
import io.kma.results.readercccd.ui.fragments.CanCuocPhotoFragment

import io.kma.results.readercccd.common.IntentData.KEY_MRZ_INFO
import kotlinx.android.synthetic.main.activity_nfc.*

class NfcActivity : androidx.fragment.app.FragmentActivity(), NfcFragment.NfcFragmentListener, CanCuocDetailsFragment.CanCuocDetailsFragmentListener, CanCuocPhotoFragment.CanCuocPhotoFragmentListener {

    private var mrzInfo: MRZInfo? = null

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        val intent = intent
        if (intent.hasExtra(KEY_MRZ_INFO)) {
            mrzInfo = intent.getSerializableExtra(KEY_MRZ_INFO) as MRZInfo
        } else {
            onBackPressed()
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.warning_no_nfc), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, this.javaClass)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NfcFragment.newInstance(mrzInfo!!), TAG_NFC)
                    .commit()
        }
    }

    public override fun onResume() {
        super.onResume()
        toolbar.setNavigationOnClickListener {
           onBackPressed()
        }
        toolbar.title= "NFC"
    }

    override fun onBackPressed() {
        finish()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onNewIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            handleIntent(intent)
        }else{
            super.onNewIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        val fragmentByTag = supportFragmentManager.findFragmentByTag(TAG_NFC)
        if (fragmentByTag is NfcFragment) {
            fragmentByTag.handleNfcTag(intent)
        }
    }

    override fun onEnableNfc() {


        if (nfcAdapter != null) {
            if (!nfcAdapter!!.isEnabled)
                showWirelessSettings()

            nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onDisableNfc() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onPassportRead(canCuoc: CanCuoc?) {
        showFragmentDetails(canCuoc!!)
    }

    override fun onCardException(cardException: Exception?) {
    }

    private fun showWirelessSettings() {
        Toast.makeText(this, getString(R.string.warning_enable_nfc), Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        startActivity(intent)
    }


    private fun showFragmentDetails(canCuoc: CanCuoc) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, CanCuocDetailsFragment.newInstance(canCuoc))
//                .addToBackStack(TAG_PASSPORT_DETAILS)
                .commit()
    }

    private fun showFragmentPhoto(bitmap: Bitmap) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, CanCuocPhotoFragment.newInstance(bitmap))
//                .addToBackStack(TAG_PASSPORT_PICTURE)
                .commit()
    }


    override fun onImageSelected(bitmap: Bitmap?) {
        showFragmentPhoto(bitmap!!)
    }

    companion object {

        private val TAG = NfcActivity::class.java.simpleName

        private val TAG_NFC = "TAG_NFC"
        private val TAG_PASSPORT_DETAILS = "TAG_PASSPORT_DETAILS"
        private val TAG_PASSPORT_PICTURE = "TAG_PASSPORT_PICTURE"
    }
}
