package io.kma.results.readercccd.task

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import io.fotoapparat.preview.Frame
import io.kma.results.readercccd.mlkit.VisionImageProcessor
import io.kma.results.readercccd.mlkit.VisionProcessorBase.OcrListener

class OcrReaderTask : AsyncTask<Void?, Void?, Boolean> {
    private var context: Context
    private var frameProcessor: VisionImageProcessor
    private var bitmap: Bitmap? = null
    private var ocrListener: OcrListener
    private var frame: Frame? = null

    constructor(
        context: Context,
        imageProcessor: VisionImageProcessor, bitmap: Bitmap?, ocrListener: OcrListener
    ) {
        this.context = context
        frameProcessor = imageProcessor
        this.bitmap = bitmap
        this.ocrListener = ocrListener
    }

    constructor(
        context: Context,
        imageProcessor: VisionImageProcessor, frame: Frame?, ocrListener: OcrListener
    ) {
        this.context = context
        frameProcessor = imageProcessor
        //this.bitmap = bitmap;
        this.frame = frame
        this.ocrListener = ocrListener
    }

    override fun doInBackground(vararg voids: Void?): Boolean {
        //frameProcessor.process(bitmap, ocrListener);
        frameProcessor.process(frame!!, 0, ocrListener)
        return true
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
    }
}