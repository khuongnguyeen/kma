package io.kma.results.readercccd.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.tech.IsoDep
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import io.kma.results.readercccd.model.DocData
import io.kma.results.readercccd.model.SessionData.Companion.getInstance
import io.kma.results.readercccd.util.ImageUtil
import jj2000.j2k.decoder.Decoder
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.PassportService
import org.jmrtd.lds.LDSFileUtil
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File

@Suppress("DEPRECATION")
class NfcReaderTask(
    private val bacKey: BACKey,
    private val isoDep: IsoDep,
    private val context: Context,
    private val iNfcReaderTaskCB: INfcReaderTaskCB
) : AsyncTask<Void, Int, Exception?>() {
    override fun doInBackground(vararg voids: Void): Exception? {
        var service: PassportService? = null
        var dg2File: DG2File? = null
        publishProgress(1)
        try {
            val cardService = CardService.getInstance(isoDep)
            println("NfcReaderTask cardService created")
            service = PassportService(
                cardService,
                PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                PassportService.DEFAULT_MAX_BLOCKSIZE,
                false, false)
            service.open()
            service.sendSelectApplet(false)
            service.doBAC(bacKey)
            publishProgress(2)
            println("NfcReaderTask doBAC")
            val comIn = service.getInputStream(PassportService.EF_COM)
            val sodIn = service.getInputStream(PassportService.EF_SOD)
            val dg1In = service.getInputStream(PassportService.EF_DG1)
            publishProgress(3)
            val dg1File = LDSFileUtil.getLDSFile(PassportService.EF_DG1, dg1In) as DG1File
            println("DateOfBirth:" + dg1File.mrzInfo.dateOfBirth)
            println("Gender:" + dg1File.mrzInfo.gender)
            println("Primary: " + dg1File.mrzInfo.primaryIdentifier)
            println("Secondary: " + dg1File.mrzInfo.secondaryIdentifier)
            println("docCode: " + dg1File.mrzInfo.documentCode)
            println("docNumber: " + dg1File.mrzInfo.documentNumber)
            println("primaryID: " + dg1File.mrzInfo.primaryIdentifier)
            println("secondID: " + dg1File.mrzInfo.secondaryIdentifier)
            println("docType: " + dg1File.mrzInfo.documentType)
            println("personalNb: " + dg1File.mrzInfo.personalNumber)
            getInstance()!!.docData = DocData(dg1File)
            publishProgress(4)
            val docCode = dg1File.mrzInfo.documentCode
            publishProgress(5)

            println("Looking for face")
            val dg2In = service.getInputStream(PassportService.EF_DG2)
            dg2File = LDSFileUtil.getLDSFile(PassportService.EF_DG2, dg2In) as DG2File
            println("dg2In.len=" + dg2In.length)
            println("Reading finished")
            publishProgress(6)

        } catch (e: Exception) {
            println("EXC: " + e.message)
            return e
        } finally {
            service?.close()
        }
        publishProgress(7)
        if (dg2File != null) {
            println("dg2File not null: " + dg2File.faceInfos.size)
            val stream = dg2File.faceInfos[0].faceImageInfos[0].imageInputStream
            val imgInfo = dg2File.faceInfos[0].faceImageInfos[0]
            println("MimeType" + imgInfo.mimeType)
            var bitmap: Bitmap? = null
            if (stream == null) {
                println("stream null")
            } else {
                println("stream NO null")
                bitmap = if (imgInfo.mimeType === "image/jp2") {
                    ImageUtil.decode(stream)
                } else {
                    BitmapFactory.decodeStream(stream)
                }
            }
            publishProgress(10)
            getInstance()!!.docData!!.bitmap = bitmap
        }
        return null
    }

    private var progressBar: ProgressBar? = null
    fun setProgressBar(bar: ProgressBar?) {
        progressBar = bar
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        if (progressBar != null) {
            progressBar!!.progress = values[0]!!
        }
    }

    //private ProgressDialog progressDialog;
    override fun onPreExecute() {
        super.onPreExecute()
        iNfcReaderTaskCB.onPreExecute()
        println("NfcReaderTask.onPreExecute")


        /*
        progressDialog = new ProgressDialog(this.context);
        //progressDialog = new ProgressDialog();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Reading...");
        progressDialog.setProgress(0);
        progressDialog.setMax(10);
        progressDialog.show();
        */
        //progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar!!.isIndeterminate = false
            progressBar!!.progress = 0
            progressBar!!.max = 10
            progressBar!!.visibility = View.VISIBLE
        } else {
            println("NfcReaderTask.onPreExecute: progressBar NULL")
        }
    }

    override fun onPostExecute(result: Exception?) {
        if (progressBar != null) {
            progressBar!!.visibility = View.GONE
        }
        if (result == null) {

            //iReadDNIeNFC.onPostExecute(true, result);
            iNfcReaderTaskCB.onPostExecute(true, null)
        } else {
            iNfcReaderTaskCB.onPostExecute(false, result)
        }
    }

    //CALLBACKS DEFINITION
    interface INfcReaderTaskCB {
        fun onPreExecute()
        fun onPostExecute(success: Boolean, result: Exception?)
    }

    companion object {
        private val pinfoDecoder = Decoder.getAllParameters()
    }
}