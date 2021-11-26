package io.kma.results.readercccd.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.kma.results.readercccd.R
import io.kma.results.readercccd.common.IntentData
import kotlinx.android.synthetic.main.fragment_photo.*

class CanCuocPhotoFragment : androidx.fragment.app.Fragment() {

    private var canCuocPhotoFragmentListener: CanCuocPhotoFragmentListener? = null

    private var bitmap: Bitmap? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments!!.containsKey(IntentData.KEY_IMAGE)) {
            bitmap = arguments.getParcelable<Bitmap>(IntentData.KEY_IMAGE)
        } else {
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData(bitmap)
    }

    private fun refreshData(bitmap: Bitmap?) {
        if (bitmap == null) {
            return
        }
        image?.setImageBitmap(bitmap)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity
        if (activity is CanCuocPhotoFragmentListener) {
            canCuocPhotoFragmentListener = activity
        }
    }

    override fun onDetach() {
        canCuocPhotoFragmentListener = null
        super.onDetach()

    }

    interface CanCuocPhotoFragmentListener

    companion object {

        fun newInstance(bitmap: Bitmap): CanCuocPhotoFragment {
            val myFragment = CanCuocPhotoFragment()
            val args = Bundle()
            args.putParcelable(IntentData.KEY_IMAGE, bitmap)
            myFragment.arguments = args
            return myFragment
        }
    }

}
