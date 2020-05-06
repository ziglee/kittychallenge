package net.cassiolandim.kittychallenge.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object UtilsModule {

    @Singleton
    @Provides
    fun provideWorkManager(context: Context) = WorkManager.getInstance(context)
}