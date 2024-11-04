package io.metamask.androidsdk

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

internal class HttpClient(private val logger: Logger = DefaultLogger) {
    private val client = OkHttpClient()
    private var additionalHeaders: Headers = Headers.headersOf("Accept", "application/json", "Content-Type", "application/json")

    fun addHeaders(headers: Map<String, String>) {
        additionalHeaders = additionalHeaders.newBuilder().apply {
            headers.forEach { (key, value) ->
                add(key, value)
            }
        }.build()
    }

    fun newCall(baseUrl: String, parameters: Map<String, Any>? = null, callback: ((String?, IOException?) -> Unit)? = null) {
        val params: Map<String, Any> = parameters ?: mapOf()
        val json = JSONObject(params).toString()

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(baseUrl)
            .headers(additionalHeaders)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                logger.error("HttpClient: error ${e.message}")
                if (callback != null) {
                    callback(null, e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    // Handle the response asynchronously
                    if (callback != null) {
                        callback(it.body?.string(), null)
                    }
                }
            }
        })
    }
}