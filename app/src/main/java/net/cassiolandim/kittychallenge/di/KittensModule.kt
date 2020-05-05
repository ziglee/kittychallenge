package net.cassiolandim.kittychallenge.di

import androidx.paging.PageKeyedDataSource
import dagger.Binds
import dagger.Module
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.repository.KittensRepositoryImpl
import net.cassiolandim.kittychallenge.ui.main.KittensDataSource

@Module
abstract class KittensModule {
    @Binds
    abstract fun provideRepository(repository: KittensRepositoryImpl): KittensRepository

    @Binds
    abstract fun provideDataSource(dataSource: KittensDataSource): KittensDataSource
}