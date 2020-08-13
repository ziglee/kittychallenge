package net.cassiolandim.kittychallenge

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
open class MyApplication : BaseApplication() {

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }
}