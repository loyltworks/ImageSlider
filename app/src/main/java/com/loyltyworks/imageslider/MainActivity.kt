package com.loyltyworks.imageslider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loyltworks.imageslider.SlideModel
import com.loyltyworks.imageslider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var imageList: ArrayList<SlideModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Initialize list
        imageList = ArrayList()

        // ✅ Add image URLs
        imageList.add(
            SlideModel("https://picsum.photos/800/400?image=10")
        )
        imageList.add(
            SlideModel("https://picsum.photos/800/400?image=-0")
        )
        imageList.add(
            SlideModel("https://picsum.photos/800/400?image=30")
        )


        // ✅ Pass list to library
        binding.imageSlider.setImageList(imageList)


        binding.imageSlider.onImageClick = { position, model ->
            // model.imageUrl available here
        }
    }
}