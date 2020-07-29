package net.cassiolandim.kittychallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule // TODO verificar pacote
import androidx.lifecycle.Observer
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
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
import java.io.File
import java.lang.RuntimeException

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

            MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            ).apply {
                firstPageSearch()
                increasePageAndSearch()

                val networkSateObserver = Observer<NetworkState> {}
                val kittensObserver = Observer<List<KittenUiModel>> {}
                try {
                    networkState.observeForever(networkSateObserver)
                    kittens.observeForever(kittensObserver)

                    Assert.assertEquals(NetworkState.LOADED, networkState.value)
                    Assert.assertEquals(listOf(kitten2), kittens.value)
                    Assert.assertEquals(listOf(kitten1, kitten2), kittenList)
                    Assert.assertFalse(isLoading)
                } finally {
                    networkState.removeObserver(networkSateObserver)
                    kittens.removeObserver(kittensObserver)
                }
            }

            coVerify { searchUseCase.run(SearchUseCase.Params(0)) }
            coVerify { searchUseCase.run(SearchUseCase.Params(1)) }
        }
    }

    @Test
    fun `Given repo is down When first page searching Should return error`() {
        testCoroutineRule.runBlockingTest {
            val errorMessage = "simulating server down"
            coEvery {
                searchUseCase.run(SearchUseCase.Params(0))
            } throws RuntimeException(errorMessage)

            MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            ).apply {
                firstPageSearch()

                val networkSateObserver = Observer<NetworkState> {}
                try {
                    networkState.observeForever(networkSateObserver)

                    Assert.assertEquals(NetworkState.error(errorMessage), networkState.value)
                    Assert.assertFalse(isLoading)
                } finally {
                    networkState.removeObserver(networkSateObserver)
                }
            }

            coVerify { searchUseCase.run(SearchUseCase.Params(0)) }
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

            MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            ).apply {

                val favoritesObserver = Observer<List<FavoriteUiModel>> {}
                try {
                    favorites.observeForever(favoritesObserver)
                    Assert.assertEquals(list, favorites.value)
                } finally {
                    favorites.removeObserver(favoritesObserver)
                }
            }

            coVerify { favoritesUseCase.run(Unit) }
        }
    }

    @Test
    fun `Given a kitten When saving favorite Should return update its model`() {
        testCoroutineRule.runBlockingTest {
            val params = SaveFavoriteUseCase.Params(
                imageId = "kitten1",
                url = "url1"
            )

            coEvery {
                saveFavoriteUseCase.run(params)
            } returns FavoriteDomainModel(
                id = "kitten1",
                imageId = "url1"
            )

            MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            ).apply {
                val kitten = KittenUiModel(
                    id = "kitten1",
                    url = "url1",
                    isFavorite = false,
                    favoriteId = null
                )
                kittenList.add(kitten)

                saveFavorite(
                    id = kitten.id,
                    url = kitten.url
                )

                val observer = Observer<Int> {}
                try {
                    savedFavoriteIndex.observeForever(observer)
                    Assert.assertEquals(0, savedFavoriteIndex.value)

                    kitten.apply {
                        Assert.assertTrue(isFavorite)
                        Assert.assertNotNull(favoriteId)
                        Assert.assertEquals("kitten1", favoriteId)
                    }
                } finally {
                    savedFavoriteIndex.removeObserver(observer)
                }
            }

            coVerify { saveFavoriteUseCase.run(params) }
        }
    }

    @Test
    fun `Given a kitten When deleting favorite Should return update its model`() {
        testCoroutineRule.runBlockingTest {
            val baseDirectory = File("")
            val params = DeleteFavoriteUseCase.Params(
                id = "kitten1",
                baseDirectory = baseDirectory
            )

            coEvery {
                deleteFavoriteUseCase.run(params)
            } returns Unit

            MainViewModel(
                favoritesUseCase = favoritesUseCase,
                deleteFavoriteUseCase = deleteFavoriteUseCase,
                saveFavoriteUseCase = saveFavoriteUseCase,
                searchUseCase = searchUseCase
            ).apply {
                val kitten = KittenUiModel(
                    id = "kitten1",
                    url = "url1",
                    isFavorite = true,
                    favoriteId = "kitten1"
                )
                kittenList.add(kitten)

                deleteFavorite(
                    id = kitten.id,
                    baseDirectory = baseDirectory
                )

                val observer = Observer<Int> {}
                try {
                    deletedFavoriteIndex.observeForever(observer)
                    Assert.assertEquals(0, deletedFavoriteIndex.value)

                    kitten.apply {
                        Assert.assertFalse(isFavorite)
                        Assert.assertNull(favoriteId)
                    }
                } finally {
                    deletedFavoriteIndex.removeObserver(observer)
                }
            }

            coVerify { deleteFavoriteUseCase.run(params) }
        }
    }
}