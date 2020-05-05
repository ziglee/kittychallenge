package net.cassiolandim.kittychallenge

import android.app.Application
import net.cassiolandim.kittychallenge.di.AppComponent
import net.cassiolandim.kittychallenge.di.DaggerAppComponent

class MyApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}