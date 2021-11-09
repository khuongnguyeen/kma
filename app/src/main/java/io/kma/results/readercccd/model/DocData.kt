package io.kma.results.readercccd.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jmrtd.lds.icao.DG1File

class DocData(dg1File: DG1File) {
    @SerializedName("doc_type")
    @Expose
    var docType: Int? = null

    @SerializedName("doc_number")
    @Expose
    var docNumber: String? = null

    @SerializedName("personal_number")
    @Expose
    var personalNumber: String? = null

    @SerializedName("birth_date")
    @Expose
    var birthDate: String? = null

    @SerializedName("expire_date")
    @Expose
    var expireDate: String? = null

    @SerializedName("primary_id")
    @Expose
    var primaryId: String? = null

    @SerializedName("secondary_id")
    @Expose
    var secondaryId: String? = null

    @SerializedName("sex")
    @Expose
    var sex: String? = null

    @SerializedName("nationality")
    @Expose
    var nationality: String? = null

    @SerializedName("portrait")
    @Expose
    var portrait: Drawable? = null

    @SerializedName("bitmap")
    @Expose
    var bitmap: Bitmap? = null

    @SerializedName("mrz")
    @Expose
    var mrz: String? = null

    init {
        try {
            val mrzInfo = dg1File.mrzInfo
            birthDate = mrzInfo.dateOfBirth
            docNumber = mrzInfo.documentNumber
            docType = mrzInfo.documentType
            expireDate = mrzInfo.dateOfExpiry
            nationality = mrzInfo.nationality
            personalNumber = mrzInfo.personalNumber
            primaryId = mrzInfo.primaryIdentifier.replace('<', ' ').trim { it <= ' ' }
            secondaryId = mrzInfo.secondaryIdentifier.replace('<', ' ').trim { it <= ' ' }
            sex = mrzInfo.gender.name
            mrz = mrzInfo.toString().trim { it <= ' ' }
        } catch (e: Exception) {
        }
    }
}