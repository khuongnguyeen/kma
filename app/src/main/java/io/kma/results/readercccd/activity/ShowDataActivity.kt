package io.kma.results.readercccd.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.kma.results.readercccd.R
import io.kma.results.readercccd.model.SessionData

class ShowDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_data)
    }

    override fun onResume() {
        super.onResume()
        println("ShowDataActivity.onResume")
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvGender = findViewById<TextView>(R.id.tvGender)
        val tvNationality = findViewById<TextView>(R.id.tvNationality)
        val tvNumber = findViewById<TextView>(R.id.tvNumber)
        val tvMrz = findViewById<TextView>(R.id.tvMrz)
        val tvExpireDate = findViewById<TextView>(R.id.tvExpireDate)
        val tvBirthDate = findViewById<TextView>(R.id.tvBirthDate)
        val imgViewPhoto = findViewById<ImageView>(R.id.imgVwPhoto)
        val data = SessionData.getInstance()?.docData
        if (data != null) {
            tvName.text = data.primaryId + " " + data.secondaryId
            tvNationality.text = data.nationality
            tvGender.text = data.sex
            tvNumber.text = data.personalNumber
            tvExpireDate.text = data.expireDate
            tvMrz.text = data.mrz
            tvBirthDate.text = data.birthDate
            if (data.portrait != null) {
                imgViewPhoto.setImageDrawable(data.portrait)
                println("ShowDataActivity.onResume: poniendo imgViewPhoto")
            } else println("ShowDataActivity.onResume portrait null")
            if (data.bitmap != null) {
                println("ShowDataActivity.onResume: poniendo bitmap")
                imgViewPhoto.setImageBitmap(data.bitmap)
            }
        }
    }
}