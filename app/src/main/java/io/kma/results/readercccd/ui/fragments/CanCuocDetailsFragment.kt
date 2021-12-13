package io.kma.results.readercccd.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.jmrtd.FeatureStatus
import org.jmrtd.VerificationStatus

import java.security.MessageDigest
import java.text.SimpleDateFormat

import io.kma.results.readercccd.R
import io.kma.results.readercccd.common.IntentData
import io.kma.results.readercccd.data.CanCuoc
import io.kma.results.readercccd.utils.ImageUtil.date2TimeStamp2
import io.kma.results.readercccd.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_passport_details.*
import java.util.*

class CanCuocDetailsFragment : androidx.fragment.app.Fragment() {

    private var canCuocDetailsFragmentListener: CanCuocDetailsFragmentListener? = null

    internal var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    private var canCuoc: CanCuoc? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val inflatedView = inflater.inflate(R.layout.fragment_passport_details, container, false)




        return inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments!!.containsKey(IntentData.KEY_PASSPORT)) {
            canCuoc = arguments.getParcelable<CanCuoc>(IntentData.KEY_PASSPORT)
        } else {
            //error
        }


        iconPhoto!!.setOnClickListener {
            var bitmap = canCuoc!!.face
            if (bitmap == null) {
                bitmap = canCuoc!!.portrait
            }
            if (canCuocDetailsFragmentListener != null) {
                canCuocDetailsFragmentListener!!.onImageSelected(bitmap)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        refreshData(canCuoc)
    }

    private fun refreshData(canCuoc: CanCuoc?) {
        if (canCuoc == null) {
            return
        }

        if (canCuoc.face != null) {
            //Add teh face
            iconPhoto!!.setImageBitmap(canCuoc.face)
        } else if (canCuoc.portrait != null) {
            //If we don't have the face, we try with the portrait
            iconPhoto!!.setImageBitmap(canCuoc.portrait)
        }

        val personDetails = canCuoc.personDetails
        if (personDetails != null) {
            val name = personDetails.primaryIdentifier!!.replace("<", " ").trim { it <= ' ' }
            val surname = personDetails.secondaryIdentifier!!.replace("<", " ").trim { it <= ' ' }
            value_name!!.text = getString(R.string.name, name, surname)
            value_DOB!!.text = date2TimeStamp2(personDetails.dateOfBirth)
            val a = personDetails.optionalData1?.split(" ")

            if (a?.size!! > 0)  value_cid!!.text = a[0]

            value_gender!!.text = personDetails.gender!!.name
            value_passport_number!!.text = personDetails.documentNumber
            value_expiration_date!!.text = date2TimeStamp2(personDetails.dateOfExpiry)
            value_issuing_state!!.text = personDetails.issuingState
            value_nationality!!.text = personDetails.nationality
        }

        val additionalPersonDetails = canCuoc.additionalPersonDetails
        if (additionalPersonDetails != null) {
            //This object it's not available in the majority of passports
            card_view_additional_person_information!!.visibility = View.VISIBLE

            if (additionalPersonDetails.custodyInformation != null) {
                value_custody!!.text = additionalPersonDetails.custodyInformation
            }
            if (additionalPersonDetails.fullDateOfBirth != null) {

                value_date_of_birth!!.text = additionalPersonDetails.fullDateOfBirth
            }
            if (additionalPersonDetails.otherNames != null && additionalPersonDetails.otherNames!!.size > 0) {
                value_other_names!!.text = arrayToString(additionalPersonDetails.otherNames!!)
            }
            if (additionalPersonDetails.otherValidTDNumbers != null && additionalPersonDetails.otherValidTDNumbers!!.size > 0) {
                value_other_td_numbers!!.text = arrayToString(additionalPersonDetails.otherValidTDNumbers!!)
            }
            if (additionalPersonDetails.permanentAddress != null && additionalPersonDetails.permanentAddress!!.size > 0) {
                value_permanent_address!!.text = arrayToString(additionalPersonDetails.permanentAddress!!)
            }

            if (additionalPersonDetails.personalNumber != null) {
                value_personal_number!!.text = additionalPersonDetails.personalNumber
            }

            if (additionalPersonDetails.personalSummary != null) {
                value_personal_summary!!.text = additionalPersonDetails.personalSummary
            }

            if (additionalPersonDetails.placeOfBirth != null && additionalPersonDetails.placeOfBirth!!.size > 0) {
                value_place_of_birth!!.text = arrayToString(additionalPersonDetails.placeOfBirth!!)
            }

            if (additionalPersonDetails.profession != null) {
                value_profession!!.text = additionalPersonDetails.profession
            }

            if (additionalPersonDetails.telephone != null) {
                value_telephone!!.text = additionalPersonDetails.telephone
            }

            if (additionalPersonDetails.title != null) {
                value_title!!.text = additionalPersonDetails.title
            }
        } else {
            card_view_additional_person_information!!.visibility = View.GONE
        }

        val additionalDocumentDetails = canCuoc.additionalDocumentDetails
        if (additionalDocumentDetails != null) {
            card_view_additional_document_information!!.visibility = View.VISIBLE

            if (additionalDocumentDetails.dateAndTimeOfPersonalization != null) {
                value_date_personalization!!.text = additionalDocumentDetails.dateAndTimeOfPersonalization
            }
            if (additionalDocumentDetails.dateOfIssue != null) {
                value_date_issue!!.text = additionalDocumentDetails.dateOfIssue
            }

            if (additionalDocumentDetails.endorsementsAndObservations != null) {
                value_endorsements!!.text = additionalDocumentDetails.endorsementsAndObservations
            }

            if (additionalDocumentDetails.endorsementsAndObservations != null) {
                value_endorsements!!.text = additionalDocumentDetails.endorsementsAndObservations
            }

            if (additionalDocumentDetails.issuingAuthority != null) {
                value_issuing_authority!!.text = additionalDocumentDetails.issuingAuthority
            }

            if (additionalDocumentDetails.namesOfOtherPersons != null) {
                value_names_other_persons!!.text = arrayToString(additionalDocumentDetails.namesOfOtherPersons!!)
            }

            if (additionalDocumentDetails.personalizationSystemSerialNumber != null) {
                value_system_serial_number!!.text = additionalDocumentDetails.personalizationSystemSerialNumber
            }

            if (additionalDocumentDetails.taxOrExitRequirements != null) {
                value_tax_exit!!.text = additionalDocumentDetails.taxOrExitRequirements
            }
        } else {
            card_view_additional_document_information!!.visibility = View.GONE
        }

        displayAuthenticationStatus(canCuoc.verificationStatus, canCuoc.featureStatus!!)


        val sodFile = canCuoc.sodFile
        if (sodFile != null) {

            val docSigningCertificate = sodFile.docSigningCertificate

            if (docSigningCertificate != null) {

                value_document_signing_certificate_serial_number!!.text = docSigningCertificate.serialNumber.toString()
                value_document_signing_certificate_public_key_algorithm!!.text = docSigningCertificate.publicKey.algorithm
                value_document_signing_certificate_signature_algorithm!!.text = docSigningCertificate.sigAlgName

                try {
                    value_document_signing_certificate_thumbprint!!.text = StringUtils.bytesToHex(MessageDigest.getInstance("SHA-1").digest(
                            docSigningCertificate.encoded)).toUpperCase()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                value_document_signing_certificate_issuer!!.text = docSigningCertificate.issuerDN.name
                value_document_signing_certificate_subject!!.text = docSigningCertificate.subjectDN.name
                value_document_signing_certificate_valid_from!!.text = simpleDateFormat.format(docSigningCertificate.notBefore)
                value_document_signing_certificate_valid_to!!.text = simpleDateFormat.format(docSigningCertificate.notAfter)

            } else {
                card_view_document_signing_certificate!!.visibility = View.GONE
            }

        } else {
            card_view_document_signing_certificate!!.visibility = View.GONE
        }
    }




    private fun displayAuthenticationStatus(verificationStatus: VerificationStatus?, featureStatus: FeatureStatus) {

        if (featureStatus.hasBAC() == FeatureStatus.Verdict.PRESENT) {
            row_bac!!.visibility = View.VISIBLE
        } else {
            row_bac!!.visibility = View.GONE
        }

        if (featureStatus.hasAA() == FeatureStatus.Verdict.PRESENT) {
            row_active!!.visibility = View.VISIBLE
        } else {
            row_active!!.visibility = View.GONE
        }

        if (featureStatus.hasSAC() == FeatureStatus.Verdict.PRESENT) {
            row_pace!!.visibility = View.VISIBLE
        } else {
            row_pace!!.visibility = View.GONE
        }

        if (featureStatus.hasCA() == FeatureStatus.Verdict.PRESENT) {
            row_chip!!.visibility = View.VISIBLE
        } else {
            row_chip!!.visibility = View.GONE
        }

        if (featureStatus.hasEAC() == FeatureStatus.Verdict.PRESENT) {
            row_eac!!.visibility = View.VISIBLE
        } else {
            row_eac!!.visibility = View.GONE
        }

        displayVerificationStatusIcon(value_bac, verificationStatus!!.bac)
        displayVerificationStatusIcon(value_pace, verificationStatus.sac)
        displayVerificationStatusIcon(value_passive, verificationStatus.ht)
        displayVerificationStatusIcon(value_active, verificationStatus.aa)
        displayVerificationStatusIcon(value_document_signing, verificationStatus.ds)
        displayVerificationStatusIcon(value_country_signing, verificationStatus.cs)
        displayVerificationStatusIcon(value_chip, verificationStatus.ca)
        displayVerificationStatusIcon(value_eac, verificationStatus.eac)
    }

    private fun displayVerificationStatusIcon(imageView: ImageView?, verdict: VerificationStatus.Verdict?) {
        var verdict = verdict
        if (verdict == null) {
            verdict = VerificationStatus.Verdict.UNKNOWN
        }
        val resourceIconId: Int
        val resourceColorId: Int
        when (verdict) {
            VerificationStatus.Verdict.SUCCEEDED -> {
                resourceIconId = R.drawable.new_check
                resourceColorId = android.R.color.holo_green_light
            }
            VerificationStatus.Verdict.FAILED -> {
                resourceIconId = R.drawable.new_uncheck
                resourceColorId = android.R.color.holo_red_light
            }
            VerificationStatus.Verdict.NOT_PRESENT -> {
                resourceIconId = R.drawable.new_uncheck
                resourceColorId = android.R.color.darker_gray
            }
            VerificationStatus.Verdict.NOT_CHECKED -> {
                resourceIconId = R.drawable.ic_help_circle_outline
                resourceColorId = android.R.color.holo_orange_light
            }
            VerificationStatus.Verdict.UNKNOWN -> {
                resourceIconId = R.drawable.new_uncheck
                resourceColorId = android.R.color.darker_gray
            }
        }

        imageView!!.setImageResource(resourceIconId)
        imageView.setColorFilter(ContextCompat.getColor(requireActivity(), resourceColorId), android.graphics.PorterDuff.Mode.SRC_IN)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity
        if (activity is CanCuocDetailsFragmentListener) {
            canCuocDetailsFragmentListener = activity
        }
    }

    override fun onDetach() {
        canCuocDetailsFragmentListener = null
        super.onDetach()

    }

    interface CanCuocDetailsFragmentListener {
        fun onImageSelected(bitmap: Bitmap?)
    }


    private fun arrayToString(array: List<String>): String {
        var temp = ""
        val iterator = array.iterator()
        while (iterator.hasNext()) {
            temp += iterator.next() + "\n"
        }
        if (temp.endsWith("\n")) {
            temp = temp.substring(0, temp.length - "\n".length)
        }
        return temp
    }

    companion object {


        fun newInstance(canCuoc: CanCuoc): CanCuocDetailsFragment {
            val myFragment = CanCuocDetailsFragment()
            val args = Bundle()
            args.putParcelable(IntentData.KEY_PASSPORT, canCuoc)
            myFragment.arguments = args
            return myFragment
        }
    }
}
