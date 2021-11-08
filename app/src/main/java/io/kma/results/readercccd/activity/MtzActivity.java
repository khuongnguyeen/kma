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

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.kma.results.readercccd.R;
import io.kma.results.readercccd.model.InputData;
import io.kma.results.readercccd.model.SessionData;

public class MtzActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity.onCreate()");
        setContentView(R.layout.activity_main_mtz);
    }

    public void onReadButtonClick(android.view.View view)
    {
        EditText edTxtNumber = findViewById(R.id.edTextPersonalNb);
        EditText edTxtBirthdate = findViewById(R.id.edTextBirthdate);
        EditText edTxtExpdate = findViewById(R.id.edTextExpirationDate);
        String sTxtNumber = edTxtNumber.getText().toString();
        String sTxtBirthdate = edTxtBirthdate.getText().toString();
        String sTxtExpDate = edTxtExpdate.getText().toString();

        System.out.println("txtNb:"+sTxtNumber);
        System.out.println("birthDate:"+sTxtBirthdate);
        System.out.println("expDate:"+sTxtExpDate);
        InputData inputData = new InputData();
        inputData.setBirthDate(sTxtBirthdate);
        inputData.setExpireDate(sTxtExpDate);
        inputData.setPersonalNumber(sTxtNumber);
        SessionData.getInstance().setInputData(inputData);

        System.out.println("MainActivity.onReadButtonClick");
        Intent intent = new Intent(this,ReadDocActivity.class);
        startActivity(intent);
    }

    public void onOcrRecognizeClick(View view)
    {
        Intent intent = new Intent(this,CameraOcrReaderActivity.class);
        startActivity(intent);
    }
}
