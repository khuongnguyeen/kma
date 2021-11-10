package io.kma.results.readercccd.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
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
            val checkedId = radio_group.checkedRadioButtonId
                if (checkedId == R.id.camera){
                    ocrScreen()
                }else{
                    openDialog()
                }


        }

    }

    private fun openDialog(){
        val dialog = Dialog(this)
        with(dialog){
            requestWindowFeature(1)
            setContentView(R.layout.dialog_explain)
            window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        val edTextPersonalNb = (dialog.findViewById(R.id.edTextPersonalNb) as EditText)
        val edTextBirthdate = (dialog.findViewById(R.id.edTextBirthdate) as EditText)
        val edTextExpirationDate = (dialog.findViewById(R.id.edTextExpirationDate) as EditText)
        (dialog.findViewById(R.id.confirm) as Button).setOnClickListener {
            if (edTextPersonalNb.text.toString()==""||edTextBirthdate.text.toString()==""||edTextExpirationDate.text.toString()==""){
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                    .setContentText("Please enter enough information!")
                    .show()
            }else{
                if(edTextPersonalNb.text.toString().length <9){
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                        .setContentText("Personal number must be 9 digits!")
                        .show()
                }else{
                    if(edTextBirthdate.text.toString().length <6){
                        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                            .setContentText("Birthdate must be 6 digits!")
                            .show()
                    }else{
                        if(edTextExpirationDate.text.toString().length <6){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                                .setContentText("ExpirationDate must be 6 digits!")
                                .show()
                        }
                        else{
                            val inputData = InputData()
                            inputData.birthDate = edTextBirthdate.text.toString()
                            inputData.expireDate = edTextExpirationDate.text.toString()
                            inputData.personalNumber = edTextPersonalNb.text.toString()
                            SessionData.getInstance()?.inputData = inputData
                            val intent = Intent(this, ReadDocActivity::class.java)
                            dialog.dismiss()
                            startActivity(intent)
                        }
                    }
                }
            }
        }
        dialog.show()
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




}