/*
 * Copyright (C) 2020 Atos Spain SA
 *
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kma.results.readercccd.task;

import android.os.AsyncTask;
import android.content.Context;
import android.graphics.Bitmap;

import io.kma.results.readercccd.mlkit.VisionImageProcessor;
import io.kma.results.readercccd.mlkit.VisionProcessorBase;
import io.fotoapparat.preview.Frame;

public class OcrReaderTask extends AsyncTask<Void,Void,Boolean>
{
    private Context context;
    private VisionImageProcessor frameProcessor;
    private Bitmap bitmap;
    private VisionProcessorBase.OcrListener ocrListener;
    private Frame frame;


    public OcrReaderTask( Context context,
                          VisionImageProcessor imageProcessor, Bitmap bitmap, VisionProcessorBase.OcrListener ocrListener) {
        this.context = context;
        this.frameProcessor = imageProcessor;
        this.bitmap = bitmap;
        this.ocrListener = ocrListener;
    }

    public OcrReaderTask(Context context,
                         VisionImageProcessor imageProcessor, Frame frame, VisionProcessorBase.OcrListener ocrListener) {
        this.context = context;
        this.frameProcessor = imageProcessor;
        //this.bitmap = bitmap;
        this.frame = frame;
        this.ocrListener = ocrListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        //frameProcessor.process(bitmap, ocrListener);
        frameProcessor.process(frame,0,ocrListener);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);
    }
}
