package io.kma.results.readercccd.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView
import io.kma.results.readercccd.R
import io.kma.results.readercccd.mlkit.OcrMrzDetectorProcessor
import io.kma.results.readercccd.mlkit.VisionProcessorBase.OcrListener
import io.kma.results.readercccd.task.OcrReaderTask
import org.jmrtd.lds.icao.MRZInfo

class CamMLKitFragment : Fragment() {
    protected var fotoapparat: Fotoapparat? = null
    protected var cameraView: CameraView? = null
    var frameProcessor: CustomFrameProcessor? = null
    private var ocrFrameProcessor: OcrMrzDetectorProcessor? = null

    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("En CamMLKitFragment.onCreateView")
        val view = inflater.inflate(R.layout.fragment_cam_mlkit, container, false)
        cameraView = view.findViewById(R.id.camera_ocr_preview)
        mStatusBar = view.findViewById(R.id.status_view_bottom)
        mStatusRead = view.findViewById(R.id.status_view_top)
        buildCamera(cameraView)
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri?) {
    }

    override fun onResume() {
        super.onResume()
        ocrFrameProcessor = OcrMrzDetectorProcessor()
        fotoapparat!!.start()
    }

    override fun onPause() {
        fotoapparat!!.stop()
        ocrFrameProcessor!!.stop()
        super.onPause()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity
        if (activity is CamMLKitFragmentListener) {
            camMLKitFragmentListener = activity
        }
    }

    override fun onDetach() {
        camMLKitFragmentListener = null
        super.onDetach()
    }

    private fun buildCamera(cameraView: CameraView?) {
        frameProcessor = CustomFrameProcessor()
        fotoapparat = Fotoapparat.with(this.requireActivity())
            .into(cameraView!!)
            .previewScaleType(ScaleType.CenterCrop)
            .photoResolution(highestResolution())
            .lensPosition(back())
            .focusMode(
                firstAvailable(
                    continuousFocusPicture(),
                    autoFocus(),
                    fixed()
                )
            )
            .frameProcessor(frameProcessor)
            .build()
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.permission_camera_rationale))
                    .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    class ErrorDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity: Activity? = activity
            return AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { dialogInterface, i -> activity!!.finish() }
                .create()
        }

        companion object {
            private const val ARG_MESSAGE = "message"
            fun newInstance(message: String?): ErrorDialog {
                val dialog = ErrorDialog()
                val args = Bundle()
                args.putString(ARG_MESSAGE, message)
                dialog.arguments = args
                return dialog
            }
        }
    }

    class ConfirmationDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parent = parentFragment
            return AlertDialog.Builder(activity)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    parent!!.requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_PERMISSION
                    )
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { dialog, which ->
                    val activity: Activity? = parent!!.activity
                    activity?.finish()
                }
                .create()
        }
    }

    interface CamMLKitFragmentListener {
        fun onPassportRead(mrzInfo: MRZInfo?)
        fun onError()
    }

    private var isDecoding = false
    private val mHandler = Handler(Looper.getMainLooper())
    private var camMLKitFragmentListener: CamMLKitFragmentListener? = null
    private var mStatusBar: TextView? = null
    private var mStatusRead: TextView? = null

    inner class CustomFrameProcessor : FrameProcessor {
        override fun process(frame: Frame) {
            if (!isDecoding) {
                isDecoding = true
                //Bitmap bitmap= createBitmap(frame);
                ocrFrameProcessor?.let {
                    OcrReaderTask(
                        context!!.applicationContext,
                        it,
                        frame,
                        object : OcrListener {
                            override fun onMRZRead(mrzInfo: MRZInfo, timeRequired: Long) {
                                mHandler.post {
                                    try {
                                        if (camMLKitFragmentListener != null) {
                                            camMLKitFragmentListener!!.onPassportRead(mrzInfo)
                                        }
                                    } catch (e: IllegalStateException) {
                                    }
                                }
                            }

                            override fun onMRZReadFailure(timeRequired: Long) {
                                mHandler.post {
                                    try {
                                    } catch (e: IllegalStateException) {
                                    }
                                }
                                isDecoding = false
                            }

                            override fun onFailure(e: Exception, timeRequired: Long) {
                                isDecoding = false
                                e.printStackTrace()
                                mHandler.post {
                                    if (camMLKitFragmentListener != null) {
                                        camMLKitFragmentListener!!.onError()
                                    }
                                }
                            }
                        })
                }?.execute()
            }
        }
    }
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val FRAGMENT_DIALOG = "CamMLKitFragment"
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): CamMLKitFragment {
            val fragment = CamMLKitFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            println("En CamMLKitFragment.newInstance")
            return fragment
        }
    }
}