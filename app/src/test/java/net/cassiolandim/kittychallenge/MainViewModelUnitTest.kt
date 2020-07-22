package net.cassiolandim.kittychallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.usecases.DeleteFavoriteUseCase
import net.cassiolandim.kittychallenge.ui.usecases.FavoritesUseCase
import net.cassiolandim.kittychallenge.ui.usecases.SaveFavoriteUseCase
import net.cassiolandim.kittychallenge.ui.usecases.SearchUseCase
import net.cassiolandim.kittychallenge.utils.TestCoroutineRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Test
    fun `Given When Should`() {
        testCoroutineRule.runBlockingTest {
            val kittensRepository = mockk<KittensRepository>()
            coEvery { kittensRepository.favoritesLocal() } returns listOf(
                FavoriteDomainModel(
                    id = "fav1",
                    imageId = "img1"
                )
            )

            val favoritesUseCase = FavoritesUseCase(kittensRepository)
            val deleteFavoriteUseCase = mockk<DeleteFavoriteUseCase>()
            val saveFavoriteUseCase = mockk<SaveFavoriteUseCase>()
            val searchUseCase = mockk<SearchUseCase>()

            val viewModel = MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            )

            viewModel.favorites.observeForever{}

            Assert.assertEquals(listOf(
                FavoriteUiModel(
                    id = "fav1",
                    imageId = "img1"
                )
            ), viewModel.favorites.value)

            coVerify { kittensRepository.favoritesLocal() }
        }
    }
}