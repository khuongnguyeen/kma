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
package io.kma.results.readercccd.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.jmrtd.BACKey;

import java.util.Arrays;

import io.kma.results.readercccd.R;
import io.kma.results.readercccd.model.InputData;
import io.kma.results.readercccd.model.SessionData;
import io.kma.results.readercccd.task.NfcReaderTask;
import io.kma.results.readercccd.task.NfcReaderTask.INfcReaderTaskCB;

public class ReadDocActivity extends AppCompatActivity implements INfcReaderTaskCB
{
    NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.read_doc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            return;
        }

        if (nfcAdapter.isEnabled())
        {
            Toast.makeText(this, "NFC enabled", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "NFC not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        System.out.println("ReadDocActivity.onResume");
        // NFC -> ON
        if (nfcAdapter != null)
        {
            Toast.makeText(this, "Searching NFC", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this.getApplicationContext(), this.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String[][] filter = new String[][]{new String[]{"android.nfc.tech.IsoDep"}};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, filter);
            System.out.println("ReadDocAct.onResume nfcAdapted enabled");
        }
        else
        {
            Toast.makeText(this, "Null NFC", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        System.out.println("ReadDocActivity.onPause");
        if (nfcAdapter != null)
        {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    ProgressBar progressBar;
    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        System.out.println("ReadIDAct.onNewIntent");
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            Tag tag = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
            System.out.println("ReadDocAct.onNewIntent tag created");
            if (Arrays.asList(tag.getTechList()).contains("android.nfc.tech.IsoDep")) {
                //BACKeySpec bacKey = new BACKey(SessionData.getInstance().getDniNumberBAC(), SessionData.getInstance().getBirthDateBAC(), SessionData.getInstance().getExpirationDateBAC());
                //new ReadDNIeNFC(IsoDep.get(tag), bacKey, this, this).execute();

                System.out.println("ReadDocAct.onNewIntent contains IsoDep");


                ProgressBar progressBar = findViewById(R.id.progressBar);
                InputData inputData = SessionData.getInstance().getInputData();
                BACKey key = new BACKey( inputData.getPersonalNumber(),inputData.getBirthDate(),inputData.getExpireDate());
                IsoDep isoDep = IsoDep.get(tag);
                //try {
                NfcReaderTask task = new NfcReaderTask( key, isoDep, this.getApplicationContext(), this);
                        //.execute();
                //task.execute(10);
                task.setProgressBar(progressBar);
                task.execute();
                //new NfcReaderTask(tag).execute();
            }
        }

    }


    @Override
    public void onPreExecute()
    {
        System.out.println("ReadIDActivity: onPreExecute");
        Toast.makeText(this, "Reading NFC ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecute(Boolean success, Exception result)
    {
        if(progressBar!=null)
        {
            progressBar.setVisibility(View.GONE);
        }

        if (success)
        {
            System.out.println("ReadIDActivity: onPostExecute success");
            Toast.makeText(this, "NFC Read OK", Toast.LENGTH_SHORT).show();

            Intent intent= new Intent(this,ShowDataActivity.class);
            startActivity(intent);

        }
        else
        {
            System.out.println("ReadIDActivity: onPostExecute NOsuccess");
            Toast.makeText(this, "NFC Read ERROR", Toast.LENGTH_SHORT).show();
        }

    }

}
