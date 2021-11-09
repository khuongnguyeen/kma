package io.kma.results.readercccd.mlkit

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import net.sf.scuba.data.Gender
import org.jmrtd.lds.icao.MRZInfo
import java.io.IOException
import java.util.regex.Pattern

class OcrMrzDetectorProcessor : VisionProcessorBase<FirebaseVisionText?>() {
    private val detector: FirebaseVisionTextRecognizer
    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage?): Task<FirebaseVisionText?> {
        return detector.processImage(image!!)
    }



    override fun onSuccess(
        results: FirebaseVisionText?,
        frameMetadata: FrameMetadata,
        timeRequired: Long,
        ocrListener: OcrListener
    ) {
        var fullRead = ""
        val blocks = results!!.textBlocks
        for (i in blocks.indices) {
            var temp = ""
            val lines = blocks[i].lines
            for (j in lines.indices) {
                temp += lines[j].text + "-"
            }
            temp = temp.replace("\r".toRegex(), "").replace("\n".toRegex(), "")
                .replace("\t".toRegex(), "")
            fullRead += "$temp-"
        }
        println("Read: $fullRead")
        val patternLineOldPassportType = Pattern.compile(REGEX_OLD_PASSPORT)
        val matcherLineOldPassportType = patternLineOldPassportType.matcher(fullRead)
        val patternIDLine1 = Pattern.compile(REGEX_ID_LINE_1)
        val matcherIDLine1 = patternIDLine1.matcher(fullRead)
        val patternIDLine2 = Pattern.compile(REGEX_ID_LINE_2)
        val matcherIDLine2 = patternIDLine2.matcher(fullRead)
        val foundIDLine1 = matcherIDLine1.find()
        val foundIDLine2 = matcherIDLine2.find()
        println("fullREAD: $fullRead")
        if (foundIDLine1) println("foundIDLine1 OK")
        if (foundIDLine2) println("foundIDLine2 OK")
        if (foundIDLine1 && foundIDLine2) {
            val line1ID = matcherIDLine1.group(0)
            val line2ID = matcherIDLine2.group(0)
            var documentNumber = line1ID.substring(5, 14)
            val dateOfBirthDay = line2ID.substring(0, 6)
            val expirationDate = line2ID.substring(8, 14)

            documentNumber = documentNumber.replace("O".toRegex(), "0")
            val mrzInfo = createDummyMrz(documentNumber, dateOfBirthDay, expirationDate)
            ocrListener.onMRZRead(mrzInfo, timeRequired)
        } else if (matcherLineOldPassportType.find()) {
            val line2 = matcherLineOldPassportType.group(0)
            var documentNumber = line2.substring(0, 9)
            val dateOfBirthDay = line2.substring(13, 19)
            val expirationDate = line2.substring(21, 27)
            documentNumber = documentNumber.replace("O".toRegex(), "0")
            val mrzInfo = createDummyMrz(documentNumber, dateOfBirthDay, expirationDate)
            ocrListener.onMRZRead(mrzInfo, timeRequired)
        } else {
            //Try with the new IP passport type
            val patternLineIPassportTypeLine1 = Pattern.compile(REGEX_IP_PASSPORT_LINE_1)
            val matcherLineIPassportTypeLine1 = patternLineIPassportTypeLine1.matcher(fullRead)
            val patternLineIPassportTypeLine2 = Pattern.compile(REGEX_IP_PASSPORT_LINE_2)
            val matcherLineIPassportTypeLine2 = patternLineIPassportTypeLine2.matcher(fullRead)
            if (matcherLineIPassportTypeLine1.find() && matcherLineIPassportTypeLine2.find()) {
                val line1 = matcherLineIPassportTypeLine1.group(0)
                val line2 = matcherLineIPassportTypeLine2.group(0)
                var documentNumber = line1.substring(5, 14)
                val dateOfBirthDay = line2.substring(0, 6)
                val expirationDate = line2.substring(8, 14)
                documentNumber = documentNumber.replace("O".toRegex(), "0")
                val mrzInfo = createDummyMrz(documentNumber, dateOfBirthDay, expirationDate)
                ocrListener.onMRZRead(mrzInfo, timeRequired)
            } else {
                ocrListener.onMRZReadFailure(timeRequired)
            }
        }
    }

    protected fun createDummyMrz(
        documentNumber: String?,
        dateOfBirthDay: String?,
        expirationDate: String?
    ): MRZInfo {
        return MRZInfo(
            "P",
            "ESP",
            "DUMMY",
            "DUMMY",
            documentNumber,
            "ESP",
            dateOfBirthDay,
            Gender.MALE,
            expirationDate,
            ""
        )
    }

    override fun onFailure(e: Exception, timeRequired: Long, ocrListener: OcrListener?) {
        Log.w(TAG, "Text detection failed.$e")
        ocrListener?.onFailure(e, timeRequired)
    }

    companion object {
        private val TAG = OcrMrzDetectorProcessor::class.java.simpleName
        private const val REGEX_OLD_PASSPORT =
            "[A-Z0-9<]{9}[0-9]{1}[A-Z<]{3}[0-9]{6}[0-9]{1}[FM<]{1}[0-9]{6}[0-9]{1}"
        private const val REGEX_IP_PASSPORT_LINE_1 = "\\bIP[A-Z<]{3}[A-Z0-9<]{9}[0-9]{1}"
        private const val REGEX_IP_PASSPORT_LINE_2 =
            "[0-9]{6}[0-9]{1}[FM<]{1}[0-9]{6}[0-9]{1}[A-Z<]{3}"
        private const val REGEX_ID_LINE_1 = "[I1]{1}[A-Z<]{4}[A-Z0-9<]{9}"
        private const val REGEX_ID_LINE_2 = "[0-9]{7}[FM<]{1}[0-9]{7}" //[A-Z<]{3}";
    }

    init {
        detector = FirebaseVision.getInstance().onDeviceTextRecognizer
    }
}