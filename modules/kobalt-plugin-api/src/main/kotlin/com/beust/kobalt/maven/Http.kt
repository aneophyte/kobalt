package com.beust.kobalt.maven

import com.beust.kobalt.KobaltException
import com.beust.kobalt.internal.KobaltSettings
import com.beust.kobalt.misc.CountingFileRequestBody
import com.beust.kobalt.misc.log
import com.google.inject.Inject
import okhttp3.*
import java.io.File
import java.io.IOException
import javax.inject.Singleton

@Singleton
class Http @Inject constructor(val settings:KobaltSettings) {
    companion object {
        // HTTP statuses
        val CREATED = 201
    }
    class TypedFile(val mimeType: String, val file: File) {
        override fun toString() = file.name
    }

    fun get(user: String?, password: String?, url: String) : Response {
        val client = OkHttpClient.Builder().proxy(settings.proxyConfig?.toProxy()).build()
        val request = Request.Builder().url(url)
        if (user != null) {
            request.header("Authorization", Credentials.basic(user, password))
        }

        try {
            return client.newCall(request.build()).execute()
        } catch(ex: IOException) {
            throw KobaltException("Could not load URL $url, error: " + ex.message, ex)
        }
    }

    fun get(url: String) : Response {
        return get(null, null, url)
    }

    fun percentProgressCallback(totalSize: Long) : (Long) -> Unit {
        return { num: Long ->
            val progress = num * 100 / totalSize
            log(1, "\rUploaded: $progress%", newLine = false)
        }
    }

    val DEFAULT_ERROR_RESPONSE = { r: Response ->
        error("Couldn't upload file: " + r.message())
    }

    fun uploadFile(user: String? = null, password: String? = null, url: String, file: TypedFile,
            post: Boolean,
            progressCallback: (Long) -> Unit = {},
            headers: Headers = Headers.of(),
            success: (Response) -> Unit = {},
            error: (Response) -> Unit = DEFAULT_ERROR_RESPONSE) {

        val fullHeaders = Headers.Builder()
        fullHeaders.set("Content-Type", file.mimeType)
        headers.names().forEach { fullHeaders.set(it, headers.get(it)) }

        user?.let {
            fullHeaders.set("Authorization", Credentials.basic(user, password))
        }

        val requestBuilder = Request.Builder()
                .headers(fullHeaders.build())
                .url(url)
        val request =
            (if (post)
                requestBuilder.post(CountingFileRequestBody(file.file, file.mimeType, progressCallback))
            else
                requestBuilder.put(CountingFileRequestBody(file.file, file.mimeType, progressCallback)))
            .build()

        log(2, "Uploading $file to $url")
        val response = OkHttpClient.Builder().proxy(settings.proxyConfig?.toProxy()).build().newCall(request).execute()
        if (! response.isSuccessful) {
            error(response)
        } else {
            success(response)
        }
    }
}
