package io.kma.results.readercccd.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InputData {
    @SerializedName("personal_number")
    @Expose
    var personalNumber: String? = null

    @SerializedName("birth_date")
    @Expose
    var birthDate: String? = null

    @SerializedName("expire_date")
    @Expose
    var expireDate: String? = null
}