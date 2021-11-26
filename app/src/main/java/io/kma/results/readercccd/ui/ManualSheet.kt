package io.kma.results.readercccd.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.kma.results.readercccd.R
import io.kma.results.readercccd.common.IntentData
import io.kma.results.readercccd.model.InputData
import io.kma.results.readercccd.model.SessionData
import io.kma.results.readercccd.ui.activities.NfcActivity
import kotlinx.android.synthetic.main.bottom_sheet_manual.*
import net.sf.scuba.data.Gender
import org.jmrtd.lds.icao.MRZInfo


class ManualSheet: BottomSheetDialogFragment() {

    fun newInstance(): ManualSheet? {
        return ManualSheet()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_manual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_xac.setOnClickListener {
            if (edTextPersonalNb.text.toString()==""||edTextBirthdate.text.toString()==""||edTextExpirationDate.text.toString()==""){
                SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmButtonBackgroundColor(context?.getColor(R.color.blue_new))
                        .setContentText("Vui lòng nhập đủ thông tin!")
                        .show()
            }else{
                if(edTextPersonalNb.text.toString().length <9){
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setConfirmButtonBackgroundColor(context?.getColor(R.color.blue_new))
                            .setContentText("Số cá nhân phải có 9 chữ số!")
                            .show()
                }else{
                    if(edTextBirthdate.text.toString().length <6){
                        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setConfirmButtonBackgroundColor(context?.getColor(R.color.blue_new))
                                .setContentText("Ngày sinh phải có 6 chữ số!")
                                .show()
                    }else{
                        if(edTextExpirationDate.text.toString().length <6){
                            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                    .setConfirmButtonBackgroundColor(context?.getColor(R.color.blue_new))
                                    .setContentText("Ngày hết hạn phải có 6 chữ số!")
                                    .show()
                        }
                        else{
                            val inputData = InputData()
                            inputData.birthDate = edTextBirthdate.text.toString()
                            inputData.expireDate = edTextExpirationDate.text.toString()
                            inputData.personalNumber = edTextPersonalNb.text.toString()
                            SessionData.getInstance()?.inputData = inputData
                            dismiss()
                            val mrzInfo = MRZInfo("P", "ESP", "DUMMY", "DUMMY", edTextPersonalNb.text.toString(), "ESP", edTextBirthdate.text.toString(), Gender.MALE, edTextExpirationDate.text.toString(), "DUMMY")
                            val intent = Intent()
                            context?.let { it1 -> intent.setClass(it1, NfcActivity::class.java) }
                            intent.putExtra(IntentData.KEY_MRZ_INFO, mrzInfo)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }



}