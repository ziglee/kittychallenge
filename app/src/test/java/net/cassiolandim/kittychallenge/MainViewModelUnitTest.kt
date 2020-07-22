package net.cassiolandim.kittychallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.domain.KittenDomainModel
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
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
    fun `Given a list of local favorites When initializing Should return local favorites`() {
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

    @Test
    fun `Given repo has one item When first page searching Should return success`() {
        testCoroutineRule.runBlockingTest {
            val kittensRepository = mockk<KittensRepository>()
            coEvery { kittensRepository.search(0) } returns listOf(
                KittenDomainModel(
                    id = "kitten1",
                    url = "kitten://"
                )
            )
            every { kittensRepository.favoritesInMemoryCache } returns mutableListOf()

            val favoritesUseCase = FavoritesUseCase(kittensRepository)
            val deleteFavoriteUseCase = mockk<DeleteFavoriteUseCase>()
            val saveFavoriteUseCase = mockk<SaveFavoriteUseCase>()
            val searchUseCase = SearchUseCase(kittensRepository)

            val viewModel = MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            )

            viewModel.firstPageSearch()

            viewModel.networkState.observeForever{}
            viewModel.kittens.observeForever{}

            val uiModelList = listOf(
                KittenUiModel(
                    id = "kitten1",
                    url = "kitten://"
                )
            )

            Assert.assertEquals(NetworkState.LOADED, viewModel.networkState.value)
            Assert.assertEquals(uiModelList, viewModel.kittens.value)
            Assert.assertEquals(uiModelList, viewModel.kittenList)
            Assert.assertFalse(viewModel.isLoading)

            coVerify { kittensRepository.search(0) }
        }
    }
}