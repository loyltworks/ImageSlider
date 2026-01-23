package com.loyltworks.imageslider

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2

class ImageSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs),
    ImageAdapter.OnImageClickListener {

    private val viewPager: ViewPager2
    private val dotContainer: LinearLayout
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var imageList: ArrayList<SlideModel>
    private lateinit var adapter: ImageAdapter

    var onImageClick: ((Int, SlideModel) -> Unit)? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_image_slider, this, true)
        viewPager = findViewById(R.id.viewPager)
        dotContainer = findViewById(R.id.dotContainer)
    }

    fun setImageList(list: ArrayList<SlideModel>) {
        imageList = list
        adapter = ImageAdapter(imageList, this)
        viewPager.adapter = adapter

        setupDots()
        startAutoScroll()
    }

    private fun setupDots() {
        dotContainer.removeAllViews()
        for (i in imageList.indices) {
            val dot = ImageView(context)
            dot.setImageResource(R.drawable.ic_dot_unselected)
            val lp = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(8, 0, 8, 0)
            dotContainer.addView(dot, lp)
        }
        updateDots(0)
    }

    private fun updateDots(position: Int) {
        for (i in 0 until dotContainer.childCount) {
            val dot = dotContainer.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == position) R.drawable.ic_dot_selected
                else R.drawable.ic_dot_unselected
            )
        }
    }

    private fun startAutoScroll() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position % imageList.size)
            }
        })

        handler.postDelayed(object : Runnable {
            override fun run() {
                viewPager.currentItem += 1
                handler.postDelayed(this, 4000)
            }
        }, 4000)
    }

    override fun onImageClick(position: Int) {
        onImageClick?.invoke(position, imageList[position])
    }
}
