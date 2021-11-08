/*
 * Copyright (C) 2020 Atos Spain SA
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import net.sf.scuba.smartcards.CardFileInputStream;
import net.sf.scuba.smartcards.CardService;

import org.jmrtd.BACKey;
import org.jmrtd.PassportService;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.iso19794.FaceImageInfo;

import java.io.InputStream;

import io.kma.results.readercccd.model.SessionData;
import io.kma.results.readercccd.model.DocData;
import io.kma.results.readercccd.util.ImageUtil;
import jj2000.j2k.decoder.Decoder;

public class NfcReaderTask extends AsyncTask<Void, Integer, Exception>
{

    private BACKey bacKey;
    private IsoDep isoDep;
    private Context context;
    private  INfcReaderTaskCB iNfcReaderTaskCB;

    public NfcReaderTask(BACKey key, IsoDep isoDep, Context context, INfcReaderTaskCB iNfcCB)
    {
        this.bacKey = key;
        this.isoDep = isoDep;
        this.context = context;
        this.iNfcReaderTaskCB = iNfcCB;
    }

    @Override
    protected Exception doInBackground(Void... voids)
    {
        PassportService service = null;
        DG2File dg2File= null;
        publishProgress(1);
        try
        {
            CardService cardService = CardService.getInstance(isoDep);
            System.out.println("NfcReaderTask cardService created");
            //service = new PassportService(cardService);
            service = new PassportService( cardService,
                    PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                    PassportService.DEFAULT_MAX_BLOCKSIZE,
                    false,false);
            service.open();
            service.sendSelectApplet(false);
            service.doBAC( this.bacKey);
            publishProgress(2);
            System.out.println("NfcReaderTask doBAC");
            //LDS CREATION
            //LDS lds = new LDS();
            CardFileInputStream comIn = service.getInputStream(PassportService.EF_COM);
            //lds.add(PassportService.EF_COM, comIn, comIn.getLength());


            CardFileInputStream sodIn = service.getInputStream(PassportService.EF_SOD);
            //lds.add(PassportService.EF_SOD, sodIn, sodIn.getLength());


            //DATA GROUP 1: MAIN DATA
            CardFileInputStream dg1In = service.getInputStream(PassportService.EF_DG1);
            publishProgress(3);
            DG1File dg1File = (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1,dg1In);
            System.out.println("DateOfBirth:"+dg1File.getMRZInfo().getDateOfBirth());
            System.out.println("Gender:"+dg1File.getMRZInfo().getGender());
            System.out.println("Primary: "+dg1File.getMRZInfo().getPrimaryIdentifier());
            System.out.println("Secondary: "+dg1File.getMRZInfo().getSecondaryIdentifier());
            System.out.println("docCode: "+dg1File.getMRZInfo().getDocumentCode());
            System.out.println("docNumber: "+dg1File.getMRZInfo().getDocumentNumber());
            System.out.println("primaryID: "+dg1File.getMRZInfo().getPrimaryIdentifier());
            System.out.println("secondID: "+dg1File.getMRZInfo().getSecondaryIdentifier());
            System.out.println("docType: "+dg1File.getMRZInfo().getDocumentType());
            System.out.println("personalNb: "+dg1File.getMRZInfo().getPersonalNumber());

            //lds.add(PassportService.EF_DG1, dg1In, dg1In.getLength());

            SessionData.getInstance().setDocData( new DocData(dg1File));
            publishProgress(4);
            String docCode = dg1File.getMRZInfo().getDocumentCode();
            /*
            if (docCode.startsWith("ID"))
            {
                //DATA GROUP 11: ADITIONAL DATA
                System.out.println("Looking for additional data");
                CardFileInputStream dg11In = service.getInputStream(PassportService.EF_DG11);
                DG11File dg11File = (DG11File) LDSFileUtil.getLDSFile(PassportService.EF_DG11, dg11In);

                //lds.add(PassportService.EF_DG11, dg11In, dg11In.getLength());
                System.out.println("Personal summary: " + dg11File.getPersonalSummary());
                System.out.println("CustodyInfo: " + dg11File.getCustodyInformation());
                System.out.println("DateOfBirth"+dg11File.getFullDateOfBirth());
                System.out.println("Perm address: "+dg11File.getPermanentAddress());
            }
            */
            publishProgress(5);


            //SAVE DATA FROM GROUP 1 AND 11

//            saveDataFromMrz(lds);
            //DATA GROUP 2: FACE
            System.out.println("Looking for face");
            CardFileInputStream dg2In = service.getInputStream(PassportService.EF_DG2);
            dg2File = (DG2File) LDSFileUtil.getLDSFile(PassportService.EF_DG2,dg2In);
            //lds.add(PassportService.EF_DG2, dg2In, dg2In.getLength());
            // System.out.println(lds.getDG2File().getFaceInfos().get(0).getFaceImageInfos().get(0).getEyeColor().name());
            System.out.println("dg2In.len="+dg2In.getLength());
//          saveFaceImgBitmap(lds.getDG2File());
            System.out.println("Reading finished");
            publishProgress(6);

            //SessionData.getInstance().setDocData( new DocData(lds));
            //lds.getDG2File().getFaceInfos().get(0).getFaceImageInfos().get(0).getImageInputStream();

        }
        catch (Exception e)
        {
            System.out.println("EXC: "+e.getMessage());
            return e;
        }
        finally
        {
            if (service != null)
            {
                service.close();
            }
        }
        //super.
        publishProgress(7);
        if (dg2File!= null)
        {
            System.out.println("dg2File not null: "+dg2File.getFaceInfos().size());
            InputStream stream = dg2File.getFaceInfos().get(0).getFaceImageInfos().get(0).getImageInputStream();

            FaceImageInfo imgInfo = dg2File.getFaceInfos().get(0).getFaceImageInfos().get(0);
            System.out.println("MimeType"+imgInfo.getMimeType());
            Bitmap bitmap=null;
            if (stream==null)
            {
                System.out.println("stream null");
            }
            else
            {
                System.out.println("stream NO null");
                if (imgInfo.getMimeType()== "image/jp2")
                {
                    bitmap = ImageUtil.decode(stream);
                   /* ParameterList parameterList = new ParameterList();
                    jj2000.j2k.decoder.Decoder decoder  = new jj2000.j2k.decoder.Decoder()*/
                    //int[] colors = jj2000.j2k.decoder.Decoder
                    //org.jmrtd.jj2000.JJ2000Decoder.decode
                    //JJ2000Decoder
                    /*
                    String pinfo[][] = Decoder.getAllParameters();
                    ParameterList parameters, defaults;
                    //....
                    Decoder decoder = new Decoder(parameters);
                    decoder.run();
                    */
                    //JJ2KDecoder decoder;

                   /*
                    jj2000.j2k.util.ParameterList  defpl = new ParameterList();
                    for (int i = pinfoDecoder.length - 1; i >= 0; --i) {
                        if (pinfoDecoder[i][3] != null) {
                            defpl.put(pinfoDecoder[i][0], pinfoDecoder[i][3]);
                        }
                    }
                    ParameterList pl = new ParameterList(defpl);

                    //pl.setProperty("rate", "3");

                    Decoder dec = new Decoder(pl);
                    //return dec.run(input);
                    dec.run();




                    //Bitmap.createBitmap()
                    bitmap = Bitmap.createBitmap(stream,0,imgInfo.getWidth(),imgInfo.getHeight(),Bitmap.Config.ARGB_8888);

                    */
                }
                else
                {

                    bitmap = BitmapFactory.decodeStream(stream);
                }
            }
            publishProgress(10);
            //Drawable drawPhoto = Drawable.createFromStream( stream, null);
            //if (drawPhoto==null) System.out.println("drawPhoto null");
            SessionData.getInstance().getDocData().setBitmap(bitmap);
        }
        return null;
    }
    private static String pinfoDecoder[][] = Decoder.getAllParameters();

    private ProgressBar progressBar;
    public void setProgressBar(ProgressBar bar) {
        this.progressBar = bar;
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
        if(progressBar!=null)
        {
            progressBar.setProgress(values[0]);
        }


        /*if (progressDialog!=null)
        {
            progressDialog.setProgress(values[0]);
        }*/

    }

    //private ProgressDialog progressDialog;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        iNfcReaderTaskCB.onPreExecute();

        System.out.println("NfcReaderTask.onPreExecute");


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
        if(progressBar!=null)
        {
            progressBar.setIndeterminate(false);
            progressBar.setProgress(0);
            progressBar.setMax(10);
            progressBar.setVisibility(View.VISIBLE);

        }
        else
        {
            System.out.println("NfcReaderTask.onPreExecute: progressBar NULL");
        }
    }


    @Override
    protected void onPostExecute(Exception result)
    {
        if(progressBar!=null)
        {
            progressBar.setVisibility(View.GONE);
        }
        if (result == null)
        {

            //iReadDNIeNFC.onPostExecute(true, result);
            iNfcReaderTaskCB.onPostExecute(true,null);

        }
        else
        {
            iNfcReaderTaskCB.onPostExecute(false,result);
        }
    }

    //CALLBACKS DEFINITION
    public interface INfcReaderTaskCB {
        public void onPreExecute();
        public void onPostExecute(Boolean success, Exception result);
    }
}
