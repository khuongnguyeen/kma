package io.kma.results.readercccd.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.kma.results.readercccd.R
import io.kma.results.readercccd.activity.fragments.CamMLKitFragment
import io.kma.results.readercccd.activity.fragments.CamMLKitFragment.CamMLKitFragmentListener
import io.kma.results.readercccd.model.InputData
import io.kma.results.readercccd.model.SessionData
import org.jmrtd.lds.icao.MRZInfo

class CameraOcrReaderActivity : AppCompatActivity(), CamMLKitFragmentListener
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CamMLKitFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onPassportRead(mrzInfo: MRZInfo?) {
        val inputData = InputData()
        inputData.birthDate = mrzInfo?.dateOfBirth
        inputData.expireDate = mrzInfo?.dateOfExpiry
        inputData.personalNumber = mrzInfo?.documentNumber
        SessionData.getInstance()?.inputData = inputData
        println("CameraOcrReaderActivity.onPassportRead")
        val intent = Intent(this, ReadDocActivity::class.java)
        startActivity(intent)
    }

    override fun onError() {
        onBackPressed()
    }

}