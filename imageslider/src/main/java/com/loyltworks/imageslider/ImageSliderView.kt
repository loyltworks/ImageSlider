package com.loyltworks.imageslider

import android.content.Context
import android.graphics.Color
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
) : LinearLayout(context, attrs), ImageAdapter.OnImageClickListener {

    private val viewPager: ViewPager2
    private val dotContainer: LinearLayout
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var imageList: ArrayList<SlideModel>
    private lateinit var adapter: ImageAdapter

    private var autoCycle: Boolean = true
    private var autoCycleDelay: Long = 4000
    private var autoCyclePeriod: Long = 4000

    private var dotSelectedColor: Int = Color.WHITE
    private var dotUnselectedColor: Int = Color.GRAY
    private var dotSize: Int = 16
    private var dotSpacing: Int = 8
    private var sliderPaddingStart: Int = 0
    private var sliderPaddingEnd: Int = 0
    private var sliderBackgroundColor: Int = Color.TRANSPARENT

    private var scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_XY

    var onImageClick: ((Int, SlideModel) -> Unit)? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_image_slider, this, true)
        viewPager = findViewById(R.id.viewPager)
        dotContainer = findViewById(R.id.dotContainer)

        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.ImageSliderView)
            autoCycle = ta.getBoolean(R.styleable.ImageSliderView_autoCycle, true)
            autoCycleDelay = ta.getInt(R.styleable.ImageSliderView_autoCycleDelay, 4000).toLong()
            autoCyclePeriod = ta.getInt(R.styleable.ImageSliderView_autoCyclePeriod, 4000).toLong()

            sliderBackgroundColor = ta.getColor(R.styleable.ImageSliderView_sliderBackgroundColor, sliderBackgroundColor)
            sliderPaddingStart = ta.getDimensionPixelSize(R.styleable.ImageSliderView_sliderPaddingStart, 0)
            sliderPaddingEnd = ta.getDimensionPixelSize(R.styleable.ImageSliderView_sliderPaddingEnd, 0)

            dotSelectedColor = ta.getColor(R.styleable.ImageSliderView_dotSelectedColor, dotSelectedColor)
            dotUnselectedColor = ta.getColor(R.styleable.ImageSliderView_dotUnselectedColor, dotUnselectedColor)
            dotSize = ta.getDimensionPixelSize(R.styleable.ImageSliderView_dotSize, dotSize)
            dotSpacing = ta.getDimensionPixelSize(R.styleable.ImageSliderView_dotSpacing, dotSpacing)

            scaleType = when(ta.getInt(R.styleable.ImageSliderView_imageScaleType, 0)) {
                1 -> ImageView.ScaleType.CENTER_CROP
                2 -> ImageView.ScaleType.FIT_CENTER
                else -> ImageView.ScaleType.FIT_XY
            }

            ta.recycle()
        }

        viewPager.setPadding(sliderPaddingStart, 0, sliderPaddingEnd, 0)
        viewPager.setBackgroundColor(sliderBackgroundColor)
    }

    fun setImageList(list: ArrayList<SlideModel>) {
        imageList = list
        adapter = ImageAdapter(imageList, this, scaleType)
        viewPager.adapter = adapter
        setupDots()
        if (autoCycle) startAutoScroll()
    }

    private fun setupDots() {
        dotContainer.removeAllViews()
        for (i in imageList.indices) {
            val dot = ImageView(context)
            dot.setImageResource(R.drawable.ic_dot_unselected) // keep using the drawable
            dot.background = null // ensure ImageView background is transparent

            // Tint the drawable for unselected state
            dot.setColorFilter(dotUnselectedColor)

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
            if (i == position) {
                // tint selected dot
                dot.setColorFilter(dotSelectedColor)
            } else {
                // tint unselected dot
                dot.setColorFilter(dotUnselectedColor)
            }
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
                handler.postDelayed(this, autoCyclePeriod)
            }
        }, autoCycleDelay)
    }

    override fun onImageClick(position: Int) {
        onImageClick?.invoke(position, imageList[position])
    }
}
/*
<com.loyltworks.imageslider.ImageSliderView
    android:id="@+id/imageSlider"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:autoCycle="true"
    app:autoCycleDelay="1000"
    app:autoCyclePeriod="6000"
    app:dotSelectedColor="@color/teal_700"
    app:dotUnselectedColor="@color/gray"
    app:dotSize="12dp"
    app:dotSpacing="6dp"
    app:sliderBackgroundColor="@color/black"
    app:imageScaleType="centerCrop"
    tools:ignore="MissingConstraints"/>
*/