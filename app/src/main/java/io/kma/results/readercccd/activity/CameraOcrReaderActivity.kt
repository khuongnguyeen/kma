package io.kma.results.readercccd.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
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
Log.e("camera___________","documentNumber:${mrzInfo?.documentNumber},dateOfExpiry:${mrzInfo?.dateOfExpiry},dateOfBirth:${mrzInfo?.dateOfBirth}")
        if (mrzInfo?.documentNumber==""||mrzInfo?.dateOfExpiry==""||mrzInfo?.dateOfBirth==""){
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                .setContentText("Error scan, please try again")
                .setConfirmClickListener {
                    this@CameraOcrReaderActivity.recreate()
                }
                .show()
        }else{
            SessionData.getInstance()?.inputData = inputData
            println("CameraOcrReaderActivity.onPassportRead")
            val intent = Intent(this, ReadDocActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onError() {
        onBackPressed()
    }

}