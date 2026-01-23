package com.loyltworks.imageslider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private val imageList: ArrayList<SlideModel>,
    private val listener: OnImageClickListener
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    interface OnImageClickListener {
        fun onImageClick(position: Int)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_container, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val actualPosition = position % imageList.size

        Glide.with(holder.imageView.context)
            .load(imageList[actualPosition].imageUrl)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            listener.onImageClick(actualPosition)
        }
    }

    override fun getItemCount(): Int =
        if (imageList.size == 1) imageList.size else Int.MAX_VALUE
}
