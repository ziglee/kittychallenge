package net.cassiolandim.kittychallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.cassiolandim.kittychallenge.network.NetworkState
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
    fun `Given repo has one item When first page searching Should return success`() {
        testCoroutineRule.runBlockingTest {
            val favoritesUseCase = mockk<FavoritesUseCase>()
            val deleteFavoriteUseCase = mockk<DeleteFavoriteUseCase>()
            val saveFavoriteUseCase = mockk<SaveFavoriteUseCase>()
            val searchUseCase = mockk<SearchUseCase>()

            val uiModelList = listOf(
                KittenUiModel(
                    id = "kitten1",
                    url = "kitten://"
                )
            )

            coEvery { favoritesUseCase.run(Unit) } returns emptyList()
            coEvery { searchUseCase.run(SearchUseCase.Params(0)) } returns uiModelList

            val viewModel = MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            )

            viewModel.firstPageSearch()

            viewModel.networkState.observeForever{}
            viewModel.kittens.observeForever{}

            Assert.assertEquals(NetworkState.LOADED, viewModel.networkState.value)
            Assert.assertEquals(uiModelList, viewModel.kittens.value)
            Assert.assertEquals(uiModelList, viewModel.kittenList)
            Assert.assertFalse(viewModel.isLoading)

            coVerify { searchUseCase.run(SearchUseCase.Params(0)) }
        }
    }

    @Test
    fun `Given a list of local favorites When initializing Should return local favorites`() {
        testCoroutineRule.runBlockingTest {
            val favoritesUseCase = mockk<FavoritesUseCase>()
            val deleteFavoriteUseCase = mockk<DeleteFavoriteUseCase>()
            val saveFavoriteUseCase = mockk<SaveFavoriteUseCase>()
            val searchUseCase = mockk<SearchUseCase>()
            val list = listOf(
                FavoriteUiModel(
                    id = "fav1",
                    imageId = "img1"
                )
            )

            coEvery { favoritesUseCase.run(Unit) } returns list

            val viewModel = MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            )

            viewModel.favorites.observeForever{}
            Assert.assertEquals(list, viewModel.favorites.value)

            coVerify { favoritesUseCase.run(Unit) }
        }
    }
}