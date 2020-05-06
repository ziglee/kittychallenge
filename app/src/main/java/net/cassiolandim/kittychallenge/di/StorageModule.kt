package net.cassiolandim.kittychallenge.di

import android.content.Context
import dagger.Module
import dagger.Provides
import net.cassiolandim.kittychallenge.database.AppDatabase
import javax.inject.Singleton

@Module
object StorageModule {

    @Provides
    @Singleton
    fun providesDatabase(context: Context): AppDatabase {
        return AppDatabase.buildDatabase(context)
    }

    @Provides
    fun providesFavoritesDao(database: AppDatabase) = database.favoritesDao()
}