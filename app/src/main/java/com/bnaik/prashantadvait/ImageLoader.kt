package com.bnaik.prashantadvait

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ImageLoader(private val imageCache: ImageCache) {

    private val client = OkHttpClient()

    // Load image asynchronously
    suspend fun loadImage(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            // Check memory cache
            val cachedImage = imageCache.getFromMemoryCache(url)
            if (cachedImage != null) {
                return@withContext cachedImage
            }

            // Check disk cache
            val diskImage = imageCache.getFromDiskCache(url)
            if (diskImage != null) {
                imageCache.putInMemoryCache(url, diskImage) // Update memory cache
                return@withContext diskImage
            }

            // Download image from network
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val bitmap = BitmapFactory.decodeStream(response.body.byteStream())
                // Cache the image
                bitmap?.let {
                    imageCache.putInMemoryCache(url, it)
                    imageCache.putInDiskCache(url, it)
                }
                bitmap
            } else {
                null
            }
        }
    }
}
