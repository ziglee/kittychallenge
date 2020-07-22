package net.cassiolandim.kittychallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    lateinit var favoritesUseCase: FavoritesUseCase
    @MockK
    lateinit var deleteFavoriteUseCase: DeleteFavoriteUseCase
    @MockK
    lateinit var saveFavoriteUseCase: SaveFavoriteUseCase
    @MockK
    lateinit var searchUseCase: SearchUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Given repo has one item When first page searching Should return success`() {
        testCoroutineRule.runBlockingTest {
            val viewModel = MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            )

            val kitten1 = KittenUiModel(
                id = "kitten1",
                url = "kitten1"
            )
            val kitten2 = KittenUiModel(
                id = "kitten2",
                url = "kitten2"
            )

            coEvery { searchUseCase.run(SearchUseCase.Params(0)) } returns listOf(kitten1)
            coEvery { searchUseCase.run(SearchUseCase.Params(1)) } returns listOf(kitten2)

            viewModel.firstPageSearch()
            viewModel.increasePageAndSearch()

            viewModel.networkState.observeForever{}
            viewModel.kittens.observeForever{}

            Assert.assertEquals(NetworkState.LOADED, viewModel.networkState.value)
            Assert.assertEquals(listOf(kitten2), viewModel.kittens.value)
            Assert.assertEquals(listOf(kitten1, kitten2), viewModel.kittenList)
            Assert.assertFalse(viewModel.isLoading)

            coVerify { searchUseCase.run(SearchUseCase.Params(0)) }
            coVerify { searchUseCase.run(SearchUseCase.Params(1)) }
        }
    }

    @Test
    fun `Given a list of local favorites When initializing Should return local favorites`() {
        testCoroutineRule.runBlockingTest {
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