package io.kma.results.readercccd.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.kma.results.readercccd.R
import io.kma.results.readercccd.model.InputData
import io.kma.results.readercccd.model.SessionData

class MtzActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("MainActivity.onCreate()")
        setContentView(R.layout.activity_main_mtz)
    }

    fun onReadButtonClick(view: View?) {
        val edTxtNumber = findViewById<EditText>(R.id.edTextPersonalNb)
        val edTxtBirthdate = findViewById<EditText>(R.id.edTextBirthdate)
        val edTxtExpdate = findViewById<EditText>(R.id.edTextExpirationDate)
        val sTxtNumber = edTxtNumber.text.toString()
        val sTxtBirthdate = edTxtBirthdate.text.toString()
        val sTxtExpDate = edTxtExpdate.text.toString()
        println("txtNb:$sTxtNumber")
        println("birthDate:$sTxtBirthdate")
        println("expDate:$sTxtExpDate")
        val inputData = InputData()
        inputData.birthDate = sTxtBirthdate
        inputData.expireDate = sTxtExpDate
        inputData.personalNumber = sTxtNumber
        SessionData.getInstance()?.inputData = inputData
        println("MainActivity.onReadButtonClick")
        val intent = Intent(this, ReadDocActivity::class.java)
        startActivity(intent)
    }

    fun onOcrRecognizeClick(view: View?) {
        val intent = Intent(this, CameraOcrReaderActivity::class.java)
        startActivity(intent)
    }
}