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

    private var autoCycle = true
    private var autoCycleDelay = 4000L
    private var autoCyclePeriod = 4000L

    private var dotSelectedColor = Color.WHITE
    private var dotUnselectedColor = Color.GRAY
    private var dotSize = 16
    private var dotSpacing = 8

    private var sliderPaddingStart = 0
    private var sliderPaddingEnd = 0
    private var sliderBackgroundColor = Color.TRANSPARENT

    private var scaleType = ImageView.ScaleType.FIT_XY

    // ✅ NEW (aspect ratio)
    private var enableAspectRatio = false
    private var ratioWidth = 16
    private var ratioHeight = 9

    var onImageClick: ((Int, SlideModel) -> Unit)? = null

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (::imageList.isInitialized && imageList.isNotEmpty()) {
                updateDots(position % imageList.size)
            }
        }
    }

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_image_slider, this, true)

        viewPager = findViewById(R.id.viewPager)
        dotContainer = findViewById(R.id.dotContainer)

        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.ImageSliderView)

            autoCycle = ta.getBoolean(R.styleable.ImageSliderView_autoCycle, true)
            autoCycleDelay =
                ta.getInt(R.styleable.ImageSliderView_autoCycleDelay, 4000).toLong()
            autoCyclePeriod =
                ta.getInt(R.styleable.ImageSliderView_autoCyclePeriod, 4000).toLong()

            sliderBackgroundColor =
                ta.getColor(R.styleable.ImageSliderView_sliderBackgroundColor, sliderBackgroundColor)
            sliderPaddingStart =
                ta.getDimensionPixelSize(R.styleable.ImageSliderView_sliderPaddingStart, 0)
            sliderPaddingEnd =
                ta.getDimensionPixelSize(R.styleable.ImageSliderView_sliderPaddingEnd, 0)

            dotSelectedColor =
                ta.getColor(R.styleable.ImageSliderView_dotSelectedColor, dotSelectedColor)
            dotUnselectedColor =
                ta.getColor(R.styleable.ImageSliderView_dotUnselectedColor, dotUnselectedColor)
            dotSize =
                ta.getDimensionPixelSize(R.styleable.ImageSliderView_dotSize, dotSize)
            dotSpacing =
                ta.getDimensionPixelSize(R.styleable.ImageSliderView_dotSpacing, dotSpacing)

            scaleType = when (ta.getInt(R.styleable.ImageSliderView_imageScaleType, 0)) {
                1 -> ImageView.ScaleType.CENTER_CROP
                2 -> ImageView.ScaleType.FIT_CENTER
                else -> ImageView.ScaleType.FIT_XY
            }

            // ✅ NEW attrs
            enableAspectRatio =
                ta.getBoolean(R.styleable.ImageSliderView_enableAspectRatio, false)

            ta.getString(R.styleable.ImageSliderView_sliderAspectRatio)?.let { ratio ->
                val parts = ratio.split(":")
                if (parts.size == 2) {
                    ratioWidth = parts[0].toIntOrNull() ?: ratioWidth
                    ratioHeight = parts[1].toIntOrNull() ?: ratioHeight
                }
            }

            ta.recycle()
        }

        viewPager.setPadding(sliderPaddingStart, 0, sliderPaddingEnd, 0)
        viewPager.setBackgroundColor(sliderBackgroundColor)
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (enableAspectRatio) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = width * ratioHeight / ratioWidth
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun setImageList(list: ArrayList<SlideModel>) {
        imageList = list
        adapter = ImageAdapter(imageList, this, scaleType)
        viewPager.adapter = adapter

        setupDots()

        if (autoCycle) {
            startAutoScroll()
        }
    }

    private fun setupDots() {
        dotContainer.removeAllViews()

        for (i in imageList.indices) {
            val dot = ImageView(context)
            dot.setImageResource(R.drawable.ic_dot_unselected)
            dot.setColorFilter(dotUnselectedColor)

            val lp = LayoutParams(dotSize, dotSize)
            lp.setMargins(dotSpacing, 0, dotSpacing, 0)

            dotContainer.addView(dot, lp)
        }

        updateDots(0)
    }

    private fun updateDots(position: Int) {
        for (i in 0 until dotContainer.childCount) {
            val dot = dotContainer.getChildAt(i) as ImageView
            dot.setColorFilter(
                if (i == position) dotSelectedColor else dotUnselectedColor
            )
        }
    }

    private fun startAutoScroll() {
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

}

/*
<com.loyltworks.imageslider.ImageSliderView
    android:id="@+id/imageSlider"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"

    <!-- Auto scroll settings -->
    app:autoCycle="true"
    app:autoCycleDelay="1000"
    app:autoCyclePeriod="6000"

    <!-- Dot indicator customization -->
    app:dotSelectedColor="@color/teal_700"
    app:dotUnselectedColor="@color/gray"
    app:dotSize="12dp"
    app:dotSpacing="6dp"

    <!-- Image scaling -->
    app:imageScaleType="centerCrop"

    <!-- Slider styling -->
    app:sliderBackgroundColor="@color/black"
    app:sliderPaddingStart="16dp"
    app:sliderPaddingEnd="16dp"

    <!-- Aspect ratio (optional) -->
    app:enableAspectRatio="true"
    app:sliderAspectRatio="16:9"

    tools:ignore="MissingConstraints" />
*/
