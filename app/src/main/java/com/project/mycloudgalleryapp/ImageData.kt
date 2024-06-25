package com.project.mycloudgalleryapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageData(
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
