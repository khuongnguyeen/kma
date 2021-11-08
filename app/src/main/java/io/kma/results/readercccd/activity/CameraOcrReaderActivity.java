/*
 * Copyright (C) 2020 Atos Spain SA
 *
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kma.results.readercccd.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.jmrtd.lds.icao.MRZInfo;

import io.kma.results.readercccd.R;
//import io.kma.results.readercccd.activity.fragments.Camera2MLKitFragment;
import io.kma.results.readercccd.activity.fragments.CamMLKitFragment;
import io.kma.results.readercccd.model.InputData;
import io.kma.results.readercccd.model.SessionData;


public class CameraOcrReaderActivity extends AppCompatActivity implements CamMLKitFragment.CamMLKitFragmentListener
//        implements Camera2MLKitFragment.Camera2MLKitFragmentListener
{

    private static final String TAG = CameraOcrReaderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    //.replace(R.id.container, new Camera2MLKitFragment())
                    .replace(R.id.container, new CamMLKitFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onPassportRead(MRZInfo mrzInfo) {
        /*Intent intent = new Intent();
        //intent.putExtra(IntentData.KEY_MRZ_INFO, mrzInfo);//[TOREVIEW]
        setResult(RESULT_OK, intent);
        finish();*/


        InputData inputData = new InputData();
        inputData.setBirthDate(mrzInfo.getDateOfBirth());
        inputData.setExpireDate(mrzInfo.getDateOfExpiry());
        inputData.setPersonalNumber(mrzInfo.getDocumentNumber());
        SessionData.getInstance().setInputData(inputData);

        System.out.println("CameraOcrReaderActivity.onPassportRead");
        Intent intent = new Intent(this,ReadDocActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError() {
        onBackPressed();
    }
}
