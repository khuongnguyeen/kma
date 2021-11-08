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

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.kma.results.readercccd.R;
import io.kma.results.readercccd.model.DocData;
import io.kma.results.readercccd.model.SessionData;

public class ShowDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        System.out.println("ShowDataActivity.onResume");
        TextView tvName = findViewById(R.id.tvName);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvLastName = findViewById(R.id.tvLastName);
        TextView tvNumber = findViewById(R.id.tvNumber);
        TextView tvBirthDate = findViewById(R.id.tvBirthDate);
        ImageView imgViewPhoto = findViewById(R.id.imgVwPhoto);

        DocData data= SessionData.getInstance().getDocData();
        if (data!= null) {
            tvName.setText(data.getSecondaryId());
            tvLastName.setText(data.getPrimaryId());
            tvGender.setText(data.getSex());
            tvNumber.setText(data.getPersonalNumber());
            tvBirthDate.setText(data.getBirthDate());
            if (data.getPortrait()!=null)
            {
                imgViewPhoto.setImageDrawable(data.getPortrait());
                System.out.println("ShowDataActivity.onResume: poniendo imgViewPhoto");
            }
            else
                System.out.println("ShowDataActivity.onResume portrait null");
            if (data.getBitmap()!=null)
            {
                System.out.println("ShowDataActivity.onResume: poniendo bitmap");
                imgViewPhoto.setImageBitmap(data.getBitmap());
            }
        }
    }
}
