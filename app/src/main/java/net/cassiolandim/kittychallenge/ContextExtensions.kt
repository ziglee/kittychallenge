package net.cassiolandim.kittychallenge

import android.content.Context
import java.io.File

/** Use external media if it is available, our app's file directory otherwise */
fun Context.getOutputDirectory(): File {
    val appContext = applicationContext
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, appContext.resources.getString(R.string.images_folder)).apply { mkdirs() } }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else appContext.filesDir
}