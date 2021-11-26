package io.kma.results.readercccd.ui

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import io.kma.results.readercccd.R

class SliderAdapter(private val context: Context,
//                     private val stringList: List<String>
                    private val stringList: List<Int>
) : PagerAdapter() {

    private val TAG = "SliderAdapter"
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount() = stringList.size

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.adapter_slider, view, false)!!
        val imageView = imageLayout.findViewById<ImageView>(R.id.image)
        Glide.with(imageView).load(stringList[position])
                .placeholder(R.drawable.new_banner_one)
                .error(R.drawable.new_banner_one)
                .into(imageView)
        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun isViewFromObject(view: View, any: Any) = view == any

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {

    }

    override fun saveState(): Parcelable? {
        return null
    }

}