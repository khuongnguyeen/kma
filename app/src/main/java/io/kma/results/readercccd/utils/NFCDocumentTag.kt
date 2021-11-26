package io.kma.results.readercccd.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import io.kma.results.readercccd.data.AdditionalDocumentDetails
import io.kma.results.readercccd.data.AdditionalPersonDetails
import io.kma.results.readercccd.data.CanCuoc
import io.kma.results.readercccd.data.PersonDetails
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.sf.scuba.smartcards.CardService
import net.sf.scuba.smartcards.CardServiceException
import org.jmrtd.*
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.MRZInfo
import java.security.Security

class NFCDocumentTag {

    fun handleTag(context: Context, tag: Tag, mrzInfo: MRZInfo, mrtdTrustStore: MRTDTrustStore, canCuocCallback: CanCuocCallback):Disposable{
        return  Single.fromCallable {
            var canCuoc: CanCuoc? = null
            var cardServiceException: Exception? = null

            var ps: PassportService? = null
            try {
                val nfc = IsoDep.get(tag)
                nfc.timeout = 5 * 1000 //5 seconds timeout
                val cs = CardService.getInstance(nfc)
                ps = PassportService(cs, 256, 224, false, true)
                ps.open()

                val canCuocNFC = CanCuocNFC(ps, mrtdTrustStore, mrzInfo)
                val verifySecurity = canCuocNFC.verifySecurity()
                val features = canCuocNFC.features

                canCuoc = CanCuoc()

                canCuoc.featureStatus = canCuocNFC.features
                canCuoc.verificationStatus = canCuocNFC.verificationStatus


                canCuoc.sodFile = canCuocNFC.sodFile


                //Basic Information
                if (canCuocNFC.dg1File != null) {
                    val mrzInfo = (canCuocNFC.dg1File as DG1File).mrzInfo
                    val personDetails = PersonDetails()
                    personDetails.dateOfBirth = mrzInfo.dateOfBirth
                    personDetails.dateOfExpiry = mrzInfo.dateOfExpiry
                    personDetails.documentCode = mrzInfo.documentCode
                    personDetails.documentNumber = mrzInfo.documentNumber
                    personDetails.optionalData1 = mrzInfo.optionalData1
                    personDetails.optionalData2 = mrzInfo.optionalData2
                    personDetails.issuingState = mrzInfo.issuingState
                    personDetails.primaryIdentifier = mrzInfo.primaryIdentifier
                    personDetails.secondaryIdentifier = mrzInfo.secondaryIdentifier
                    personDetails.nationality = mrzInfo.nationality
                    personDetails.gender = mrzInfo.gender
                    canCuoc.personDetails = personDetails
                }

                //Picture
                if (canCuocNFC.dg2File != null) {
                    //Get the picture
                    try {
                        val faceImage = CanCuocNfcUtils.retrieveFaceImage(context, canCuocNFC.dg2File!!)
                        canCuoc.face = faceImage
                    } catch (e: Exception) {
                        //Don't do anything
                        e.printStackTrace()
                    }

                }


                //Portrait
                //Get the picture
                if (canCuocNFC.dg5File != null) {
                    //Get the picture
                    try {
                        val faceImage = CanCuocNfcUtils.retrievePortraitImage(context, canCuocNFC.dg5File!!)
                        canCuoc.portrait = faceImage
                    } catch (e: Exception) {
                        //Don't do anything
                        e.printStackTrace()
                    }

                }


                val dg11 = canCuocNFC.dg11File
                if (dg11 != null) {

                    val additionalPersonDetails = AdditionalPersonDetails()
                    additionalPersonDetails.custodyInformation = dg11.custodyInformation
                    additionalPersonDetails.fullDateOfBirth = dg11.fullDateOfBirth
                    additionalPersonDetails.nameOfHolder = dg11.nameOfHolder
                    additionalPersonDetails.otherNames = dg11.otherNames
                    additionalPersonDetails.otherNames = dg11.otherNames
                    additionalPersonDetails.otherValidTDNumbers = dg11.otherValidTDNumbers
                    additionalPersonDetails.permanentAddress = dg11.permanentAddress
                    additionalPersonDetails.personalNumber = dg11.personalNumber
                    additionalPersonDetails.personalSummary = dg11.personalSummary
                    additionalPersonDetails.placeOfBirth = dg11.placeOfBirth
                    additionalPersonDetails.profession = dg11.profession
                    additionalPersonDetails.proofOfCitizenship = dg11.proofOfCitizenship
                    additionalPersonDetails.tag = dg11.tag
                    additionalPersonDetails.tagPresenceList = dg11.tagPresenceList
                    additionalPersonDetails.telephone = dg11.telephone
                    additionalPersonDetails.title = dg11.title

                    canCuoc.additionalPersonDetails = additionalPersonDetails
                }


                //Finger prints
                //Get the pictures
                if (canCuocNFC.dg3File != null) {
                    //Get the picture
                    try {
                        val bitmaps = CanCuocNfcUtils.retrieveFingerPrintImage(context, canCuocNFC.dg3File!!)
                        canCuoc.fingerprints = bitmaps
                    } catch (e: Exception) {
                        //Don't do anything
                        e.printStackTrace()
                    }
                }
                if (canCuocNFC.dg7File != null) {
                    try {
                        val bitmap = CanCuocNfcUtils.retrieveSignatureImage(context, canCuocNFC.dg7File!!)
                        canCuoc.signature = bitmap
                    } catch (e: Exception) {
                        //Don't do anything
                        e.printStackTrace()
                    }
                }
                val dg12 = canCuocNFC.dg12File
                if (dg12 != null) {
                    val additionalDocumentDetails = AdditionalDocumentDetails()
                    additionalDocumentDetails.dateAndTimeOfPersonalization = dg12.dateAndTimeOfPersonalization
                    additionalDocumentDetails.dateOfIssue = dg12.dateOfIssue
                    additionalDocumentDetails.endorsementsAndObservations = dg12.endorsementsAndObservations
                    try {
                        val imageOfFront = dg12.imageOfFront
                        val bitmapImageOfFront = BitmapFactory.decodeByteArray(imageOfFront, 0, imageOfFront.size)
                        additionalDocumentDetails.imageOfFront = bitmapImageOfFront
                    } catch (e: Exception) {
                        Log.e(TAG, "Additional document image front: $e")
                    }

                    try {
                        val imageOfRear = dg12.imageOfRear
                        val bitmapImageOfRear = BitmapFactory.decodeByteArray(imageOfRear, 0, imageOfRear.size)
                        additionalDocumentDetails.imageOfRear = bitmapImageOfRear
                    } catch (e: Exception) {
                        Log.e(TAG, "Additional document image rear: $e")
                    }

                    additionalDocumentDetails.issuingAuthority = dg12.issuingAuthority
                    additionalDocumentDetails.namesOfOtherPersons = dg12.namesOfOtherPersons
                    additionalDocumentDetails.personalizationSystemSerialNumber = dg12.personalizationSystemSerialNumber
                    additionalDocumentDetails.taxOrExitRequirements = dg12.taxOrExitRequirements

                    canCuoc.additionalDocumentDetails = additionalDocumentDetails
                }

                //TODO EAC
            } catch (e: Exception) {
                cardServiceException = e
            } finally {
                try {
                    ps?.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            CanCuocDTO(canCuoc, cardServiceException)
        }
                .doOnSubscribe{
            canCuocCallback.onCanCuocReadStart()
        }
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { canCuocDTO ->
                    if (canCuocDTO.cardServiceException != null) {
                        when (val cardServiceException = canCuocDTO.cardServiceException) {
                            is AccessDeniedException -> {
                                canCuocCallback.onAccessDeniedException(cardServiceException)
                            }
                            is BACDeniedException -> {
                                canCuocCallback.onBACDeniedException(cardServiceException)
                            }
                            is PACEException -> {
                                canCuocCallback.onPACEException(cardServiceException)
                            }
                            is CardServiceException -> {
                                canCuocCallback.onCardException(cardServiceException)
                            }
                            else -> {
                                canCuocCallback.onGeneralException(cardServiceException)
                            }
                        }
                    } else {
                        canCuocCallback.onCanCuocRead(canCuocDTO.canCuoc)
                    }
                    canCuocCallback.onCanCuocReadFinish()
                }
    }

    data class CanCuocDTO(val canCuoc: CanCuoc? = null, val cardServiceException: Exception? = null)

    interface CanCuocCallback {
        fun onCanCuocReadStart()
        fun onCanCuocReadFinish()
        fun onCanCuocRead(canCuoc: CanCuoc?)
        fun onAccessDeniedException(exception: AccessDeniedException)
        fun onBACDeniedException(exception: BACDeniedException)
        fun onPACEException(exception: PACEException)
        fun onCardException(exception: CardServiceException)
        fun onGeneralException(exception: Exception?)
    }

    companion object {

        private val TAG = NFCDocumentTag::class.java.simpleName

        init {
            Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        }

        private val EMPTY_TRIED_BAC_ENTRY_LIST = emptyList<Any>()
        private val EMPTY_CERTIFICATE_CHAIN = emptyList<Any>()
    }
}