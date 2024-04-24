package com.bnaik.prashantadvait

import com.bnaik.prashantadvait.model.ApiImageItem
import com.bnaik.prashantadvait.model.Thumbnail
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ImageAPI {
    // Define custom exception classes
    class ApiException(message: String) : Exception(message)
    class NetworkException(message: String) : Exception(message)

    private val client = OkHttpClient()
    private val gson = Gson()

    // Base URL for the photos API
    private val baseURL = "https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100"

    /**
     * Fetch a list of thumbnails from the API.
     *
     * @return A list of Thumbnail objects, or an empty list in case of an error.
     */
    suspend fun fetchThumbnails(): List<Thumbnail> {
        return withContext(Dispatchers.IO) {

            // Create a request for the thumbnails from the API
            val request = Request.Builder()
                .url(baseURL)
                .build()

            try {
                // Execute the request and fetch the response
                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // Parse the JSON response using Gson
                    val jsonResponse = response.body.string()

                    if (jsonResponse.isBlank()) {
                        // If the response body is null, throw an exception
                        throw ApiException("Empty response body")
                    }

                    // Deserialize the JSON response into a list of Thumbnail objects
                    val thumbnailList: Array<ApiImageItem> = gson.fromJson(jsonResponse, Array<ApiImageItem>::class.java)
                    thumbnailList.map { it.thumbnail }.toList()
                } else {
                    // Throw an exception for an unsuccessful API response
                    throw ApiException("API error: ${response.code} - ${response.message}")
                }
            } catch (e: IOException) {
                // Throw a network exception in case of network errors
                throw NetworkException("Network error: ${e.message}")
            }
        }
    }
}

