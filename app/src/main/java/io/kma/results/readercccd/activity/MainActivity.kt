package io.kma.results.readercccd.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.kma.results.readercccd.R
import io.kma.results.readercccd.model.InputData
import io.kma.results.readercccd.model.SessionData
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity:AppCompatActivity() {

    private val MY_CAMERA_PERMISSION_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
        }

        cv_2.setOnClickListener {
            qrScreen()
        }

        cv_1.setOnClickListener {
            mtzScreen()

        }
        cv_3.setOnClickListener {
            ocrScreen()

        }


    }

    private fun qrScreen() {
        val intent = Intent(this, BottomTabsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun ocrScreen() {
        val intent = Intent(this, CameraOcrReaderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun mtzScreen() {
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }



}