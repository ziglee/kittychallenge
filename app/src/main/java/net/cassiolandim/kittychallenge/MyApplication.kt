package net.cassiolandim.kittychallenge

import android.app.Application
import net.cassiolandim.kittychallenge.di.AppComponent
import net.cassiolandim.kittychallenge.di.DaggerAppComponent
import timber.log.Timber

open class MyApplication : Application() {

    open val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}