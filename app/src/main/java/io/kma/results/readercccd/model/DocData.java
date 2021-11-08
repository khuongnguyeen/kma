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
package io.kma.results.readercccd.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.MRZInfo;

//import java.io.IOException;

public class DocData
{
    @SerializedName("doc_type")
    @Expose
    private Integer docType;
    @SerializedName("doc_number")
    @Expose
    private String docNumber;
    @SerializedName("personal_number")
    @Expose
    private String personalNumber;
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    @SerializedName("expire_date")
    @Expose
    private String expireDate;
    @SerializedName("primary_id")
    @Expose
    private String primaryId;
    @SerializedName("secondary_id")
    @Expose
    private String secondaryId;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("nationality")
    @Expose
    private String nationality;
    @SerializedName("portrait")
    @Expose
    private Drawable portrait;
    @SerializedName("bitmap")
    @Expose
    private Bitmap bitmap;
    @SerializedName("mrz")
    @Expose
    private String mrz;

    public DocData(DG1File dg1File)
    {
        try
        {
            MRZInfo mrzInfo = dg1File.getMRZInfo();
            setBirthDate(mrzInfo.getDateOfBirth());
            setDocNumber(mrzInfo.getDocumentNumber());
            setDocType(mrzInfo.getDocumentType());
            setExpireDate(mrzInfo.getDateOfExpiry());
            setNationality(mrzInfo.getNationality());
            setPersonalNumber(mrzInfo.getPersonalNumber());
            setPrimaryId(mrzInfo.getPrimaryIdentifier().replace('<',' ').trim());
            setSecondaryId(mrzInfo.getSecondaryIdentifier().replace('<',' ').trim());
            setSex(mrzInfo.getGender().name());
            setMrz(mrzInfo.toString().trim());
        }
        catch (Exception e)
        {}
    }


    public Integer getDocType()
    {
        return docType;
    }

    public void setDocType(Integer docType)
    {
        this.docType = docType;
    }

    public String getDocNumber()
    {
        return docNumber;
    }

    public void setDocNumber(String docNumber)
    {
        this.docNumber = docNumber;
    }

    public String getPersonalNumber()
    {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber)
    {
        this.personalNumber = personalNumber;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public String getPrimaryId()
    {
        return primaryId;
    }

    public void setPrimaryId(String primaryId)
    {
        this.primaryId = primaryId;
    }

    public String getSecondaryId()
    {
        return secondaryId;
    }

    public void setSecondaryId(String secondaryId)
    {
        this.secondaryId = secondaryId;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getNationality()
    {
        return nationality;
    }

    public void setNationality(String nationality)
    {
        this.nationality = nationality;
    }

    public String getMrz() {
        return mrz;
    }

    public void setMrz(String mrz) {
        this.mrz = mrz;
    }

    public Drawable getPortrait() {
        return portrait;
    }

    public void setPortrait(Drawable portrait) {
        this.portrait = portrait;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
