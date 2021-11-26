package io.kma.results.readercccd.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import io.kma.results.readercccd.R
import io.kma.results.readercccd.common.IntentData
import io.kma.results.readercccd.model.InputData
import io.kma.results.readercccd.model.SessionData
import kotlinx.android.synthetic.main.activity_main.*
import net.sf.scuba.data.Gender
import org.jmrtd.lds.icao.MRZInfo


class MainActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



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
                    .setContentText("Vui lòng nhập đủ thông tin!")
                    .show()
            }else{
                if(edTextPersonalNb.text.toString().length <9){
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                        .setContentText("Số cá nhân phải có 9 chữ số!")
                        .show()
                }else{
                    if(edTextBirthdate.text.toString().length <6){
                        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                            .setContentText("Ngày sinh phải có 6 chữ số!")
                            .show()
                    }else{
                        if(edTextExpirationDate.text.toString().length <6){
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setConfirmButtonBackgroundColor(getColor(R.color.red_700))
                                .setContentText("Ngày hết hạn phải có 6 chữ số!")
                                .show()
                        }
                        else{
                            val inputData = InputData()
                            inputData.birthDate = edTextBirthdate.text.toString()
                            inputData.expireDate = edTextExpirationDate.text.toString()
                            inputData.personalNumber = edTextPersonalNb.text.toString()
                            SessionData.getInstance()?.inputData = inputData
                            dialog.dismiss()
                            val mrzInfo = MRZInfo("P", "ESP", "DUMMY", "DUMMY", edTextPersonalNb.text.toString(), "ESP", edTextBirthdate.text.toString(), Gender.MALE, edTextExpirationDate.text.toString(), "DUMMY")
                            val intent = Intent(this, NfcActivity::class.java)
                            intent.putExtra(IntentData.KEY_MRZ_INFO, mrzInfo)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
        dialog.show()
    }

    private fun qrScreen() {
        val intent = Intent(this, QRTabsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun ocrScreen() {
        val intent = Intent(this, CameraOcrReaderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }




}