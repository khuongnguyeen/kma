package io.kma.results.readercccd.ui.fragments

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import net.sf.scuba.smartcards.CardServiceException
import net.sf.scuba.smartcards.ISO7816


import org.jmrtd.AccessDeniedException
import org.jmrtd.BACDeniedException
import org.jmrtd.PACEException
import org.jmrtd.lds.icao.MRZInfo


import java.security.Security


import io.kma.results.readercccd.R
import io.kma.results.readercccd.common.IntentData
import io.kma.results.readercccd.data.CanCuoc
import io.kma.results.readercccd.utils.KeyStoreUtils
import io.kma.results.readercccd.utils.NFCDocumentTag
import io.reactivex.disposables.CompositeDisposable
import org.jmrtd.MRTDTrustStore


class NfcFragment : androidx.fragment.app.Fragment() {

    private var mrzInfo: MRZInfo? = null
    private var nfcFragmentListener: NfcFragmentListener? = null
    private var textViewPassportNumber: TextView? = null
    private var textViewDateOfBirth: TextView? = null
    private var textViewDateOfExpiry: TextView? = null
    private var progressBar: ProgressBar? = null
    private var mHandler = Handler(Looper.getMainLooper())
    var disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_nfc, container, false)!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments
        if (arguments!!.containsKey(IntentData.KEY_MRZ_INFO)) {
            mrzInfo = arguments.getSerializable(IntentData.KEY_MRZ_INFO) as MRZInfo
        }
        textViewPassportNumber = view.findViewById(R.id.value_passport_number)
        textViewDateOfBirth = view.findViewById(R.id.value_DOB)
        textViewDateOfExpiry = view.findViewById(R.id.value_expiration_date)
        progressBar = view.findViewById(R.id.progressBar)
    }

    fun handleNfcTag(intent: Intent?) {
        if (intent == null || intent.extras == null) {
            return
        }
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return

        val folder = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
        val keyStore = KeyStoreUtils().readKeystoreFromFile(folder)

        val mrtdTrustStore = MRTDTrustStore()
        if(keyStore!=null){
            val certStore = KeyStoreUtils().toCertStore(keyStore = keyStore)

            mrtdTrustStore.addAsCSCACertStore(certStore)
        }


        val subscribe = NFCDocumentTag().handleTag(requireContext(), tag, mrzInfo!!, mrtdTrustStore, object : NFCDocumentTag.CanCuocCallback {

            override fun onCanCuocReadStart() {
                onNFCSReadStart()
            }

            override fun onCanCuocReadFinish() {
                onNFCReadFinish()
            }

            override fun onCanCuocRead(canCuoc: CanCuoc?) {
                this@NfcFragment.onPassportRead(canCuoc)

            }

            override fun onAccessDeniedException(exception: AccessDeniedException) {
                Toast.makeText(context, getString(R.string.warning_authentication_failed), Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
                this@NfcFragment.onCardException(exception)

            }

            override fun onBACDeniedException(exception: BACDeniedException) {
                Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                this@NfcFragment.onCardException(exception)
            }

            override fun onPACEException(exception: PACEException) {
                Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                this@NfcFragment.onCardException(exception)
            }

            override fun onCardException(exception: CardServiceException) {
                val sw = exception.sw.toShort()
                when (sw) {
                    ISO7816.SW_CLA_NOT_SUPPORTED -> {
                        Toast.makeText(context, getString(R.string.warning_cla_not_supported), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                this@NfcFragment.onCardException(exception)
            }

            override fun onGeneralException(exception: Exception?) {
                Toast.makeText(context, exception!!.toString(), Toast.LENGTH_SHORT).show()
                this@NfcFragment.onCardException(exception)
            }
        })
        disposable.add(subscribe)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity
        if (activity is NfcFragmentListener) {
            nfcFragmentListener = activity
        }
    }

    override fun onDetach() {
        nfcFragmentListener = null
        super.onDetach()
    }


    override fun onResume() {
        super.onResume()

        textViewPassportNumber!!.text = getString(R.string.doc_number, mrzInfo!!.documentNumber)
        textViewDateOfBirth!!.text = getString(R.string.doc_dob, mrzInfo!!.dateOfBirth)
        textViewDateOfExpiry!!.text = getString(R.string.doc_expiry, mrzInfo!!.dateOfExpiry)
        if (nfcFragmentListener != null) {
            nfcFragmentListener!!.onEnableNfc()
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcFragmentListener != null) {
            nfcFragmentListener!!.onDisableNfc()
        }
    }

    override fun onDestroyView() {
        if (!disposable.isDisposed) {
            disposable.dispose();
        }
        super.onDestroyView()
    }

    private fun onNFCSReadStart() {
        Log.d(TAG, "onNFCSReadStart")
        mHandler.post { progressBar!!.visibility = View.VISIBLE }

    }

    private fun onNFCReadFinish() {
        Log.d(TAG, "onNFCReadFinish")
        mHandler.post { progressBar!!.visibility = View.GONE }
    }

    private fun onCardException(cardException: Exception?) {
        mHandler.post {
            if (nfcFragmentListener != null) {
                nfcFragmentListener!!.onCardException(cardException)
            }
        }
    }

    private fun onPassportRead(canCuoc: CanCuoc?) {
        mHandler.post {
            if (nfcFragmentListener != null) {
                nfcFragmentListener!!.onPassportRead(canCuoc)
            }
        }
    }

    interface NfcFragmentListener {
        fun onEnableNfc()
        fun onDisableNfc()
        fun onPassportRead(canCuoc: CanCuoc?)
        fun onCardException(cardException: Exception?)
    }



    companion object {
        private val TAG = NfcFragment::class.java.simpleName

        init {
            Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        }
        fun newInstance(mrzInfo: MRZInfo): NfcFragment {
            val myFragment = NfcFragment()
            val args = Bundle()
            args.putSerializable(IntentData.KEY_MRZ_INFO, mrzInfo)
            myFragment.arguments = args
            return myFragment
        }
    }
}
