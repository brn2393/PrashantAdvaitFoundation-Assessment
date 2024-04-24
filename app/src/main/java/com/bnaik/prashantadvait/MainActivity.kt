package com.bnaik.prashantadvait

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imageLoader: ImageLoader
    private lateinit var imageCache: ImageCache
    private lateinit var imageAPI: ImageAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize cache
        imageCache = ImageCache(cacheDir)
        // Initialize image loader
        imageLoader = ImageLoader(imageCache)
        // Initialize image API
        imageAPI = ImageAPI()

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3-column grid

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Fetch thumbnails asynchronously
                val thumbnails = imageAPI.fetchThumbnails()
                // Create and set adapter
                imageAdapter = ImageAdapter(thumbnails, imageLoader)
                recyclerView.adapter = imageAdapter
            } catch (e: ImageAPI.ApiException) {
                // Handle API error
                Toast.makeText(this@MainActivity, "API error: ${e.message}", Toast.LENGTH_SHORT).show()
                // Show error message to the user or perform other error handling
            } catch (e: ImageAPI.NetworkException) {
                // Handle network error
                Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                // Show error message to the user or perform other error handling
            }
        }
    }
}
