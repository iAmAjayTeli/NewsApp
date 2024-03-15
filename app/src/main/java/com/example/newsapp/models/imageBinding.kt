package com.example.newsapp.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.newsapp.R

class imageBinding {

    companion object {
        @JvmStatic
        @BindingAdapter("urlToImage")
        fun loadImage(view: ImageView, thumbnail: String?) {
            thumbnail?.let {
                Glide.with(view.context)
                    .load(it)
                    .into(view)
            }
        }
    }
}

