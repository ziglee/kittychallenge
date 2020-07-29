package net.cassiolandim.kittychallenge.work

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.cassiolandim.kittychallenge.copyInputStreamToFile
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.ui.favorites.ImageDownloadedBroadcastReceiver
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
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

            Timber.d("Downloading image #$url")

            try {
                val request = Request.Builder().url(url).build()
                val response = OkHttpClient.Builder().build().newCall(request).execute()
                response.body?.let{ body ->
                    File(outputDirectory, "$favoriteId.jpg")
                        .copyInputStreamToFile(body.byteStream())
                }
            } catch (e: Exception) {
                return@withContext Result.failure()
            }

            Intent().also { intent ->
                intent.action = ImageDownloadedBroadcastReceiver.ACTION
                intent.putExtra(ImageDownloadedBroadcastReceiver.EXTRA_FAVORITE_ID, favoriteId)
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent)
            }

            return@withContext Result.success()
        }
    }
}