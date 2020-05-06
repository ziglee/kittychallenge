package net.cassiolandim.kittychallenge.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.cassiolandim.kittychallenge.copyInputStreamToFile
import net.cassiolandim.kittychallenge.getOutputDirectory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class ImageDownloadWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_URL = "KEY_URL"
        const val KEY_FAVORITE_ID = "KEY_FAVORITE_ID"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val outputDirectory = appContext.getOutputDirectory()
            val url = inputData.getString(KEY_URL)!!
            val favoriteId = inputData.getString(KEY_FAVORITE_ID)!!

            val request = Request.Builder().url(url).build()
            val response = OkHttpClient.Builder().build().newCall(request).execute()
            response.body?.let{ body ->
                File(outputDirectory, "$favoriteId.jpg")
                    .copyInputStreamToFile(body.byteStream())
            }

            return@withContext Result.success()
        }
    }
}