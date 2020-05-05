package net.cassiolandim.kittychallenge.di

import dagger.Binds
import dagger.Module
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.repository.KittensRepositoryImpl

@Module
abstract class KittensModule {
    @Binds
    abstract fun provideRepository(repository: KittensRepositoryImpl): KittensRepository
}