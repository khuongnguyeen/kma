package io.kma.results.readercccd.activity

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.kma.results.readercccd.R
import io.kma.results.readercccd.model.SessionData
import io.kma.results.readercccd.task.NfcReaderTask
import io.kma.results.readercccd.task.NfcReaderTask.INfcReaderTaskCB
import org.jmrtd.BACKey
import java.util.*

class ReadDocActivity : AppCompatActivity(), INfcReaderTaskCB {
    var nfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_doc)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
            return
        }
        if (nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "NFC enabled", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "NFC not available", Toast.LENGTH_LONG).show()
        }
    }

    public override fun onResume() {
        super.onResume()
        println("ReadDocActivity.onResume")
        // NFC -> ON
        if (nfcAdapter != null) {
            Toast.makeText(this, "Searching NFC", Toast.LENGTH_SHORT).show()
            val intent = Intent(this.applicationContext, this.javaClass)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
            nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, filter)
            println("ReadDocAct.onResume nfcAdapted enabled")
        } else {
            Toast.makeText(this, "Null NFC", Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onPause() {
        super.onPause()
        println("ReadDocActivity.onPause")
        if (nfcAdapter != null) {
            nfcAdapter!!.disableForegroundDispatch(this)
        }
    }

    var progressBar: ProgressBar? = null
    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
        println("ReadIDAct.onNewIntent")
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.extras!!.getParcelable<Tag>(NfcAdapter.EXTRA_TAG)
            println("ReadDocAct.onNewIntent tag created")
            if (Arrays.asList(*tag!!.techList).contains("android.nfc.tech.IsoDep")) {
                println("ReadDocAct.onNewIntent contains IsoDep")
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                val inputData = SessionData.getInstance()?.inputData
                val key =
                    BACKey(inputData?.personalNumber, inputData?.birthDate, inputData?.expireDate)
                val isoDep = IsoDep.get(tag)
                val task = NfcReaderTask(key, isoDep, this.applicationContext, this)
                task.setProgressBar(progressBar)
                task.execute()
            }
        }
    }

    override fun onPreExecute() {
        println("ReadIDActivity: onPreExecute")
        Toast.makeText(this, "Reading NFC ...", Toast.LENGTH_SHORT).show()
    }

    override fun onPostExecute(success: Boolean, result: Exception?) {
        if (progressBar != null) {
            progressBar!!.visibility = View.GONE
        }
        if (success) {
            println("ReadIDActivity: onPostExecute success")
            Toast.makeText(this, "NFC Read OK", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ShowDataActivity::class.java)
            startActivity(intent)
        } else {
            println("ReadIDActivity: onPostExecute NOsuccess")
            Toast.makeText(this, "NFC Read ERROR", Toast.LENGTH_SHORT).show()
        }
    }
}