package net.cassiolandim.kittychallenge.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.repository.KittensRepositoryImpl

@Module
@InstallIn(ApplicationComponent::class)
abstract class KittensModule {
    @Binds
    abstract fun provideRepository(repository: KittensRepositoryImpl): KittensRepository
}