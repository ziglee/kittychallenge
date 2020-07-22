package net.cassiolandim.kittychallenge

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.domain.KittenDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.usecases.SearchUseCase
import net.cassiolandim.kittychallenge.utils.TestCoroutineRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SearchUseCaseUnitTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    lateinit var kittensRepository: KittensRepository

    @Test
    fun `Given repository is ok When searching Should return success`() {
        testCoroutineRule.runBlockingTest {
            // Initial setup
            Mockito.doReturn(listOf(
                KittenDomainModel(
                    id = "x",
                    url = "http"
                )
            )).`when`(kittensRepository).search(0)

            Mockito.doReturn(emptyList<FavoriteDomainModel>())
                .`when`(kittensRepository).favoritesInMemoryCache

            // Performing usecase
            val params = SearchUseCase.Params(
                page = 0
            )
            val useCase = SearchUseCase(kittensRepository)
            useCase.invoke(
                scope = this,
                params = params,
                onSuccess = { list ->
                    Assert.assertNotNull(list)
                    Assert.assertEquals(1, list.size)
                    list.first().let {
                        Assert.assertEquals("x", it.id)
                        Assert.assertEquals("http", it.url)
                        Assert.assertFalse(it.isFavorite)
                    }
                },
                onError = { error -> Assert.fail(error.message) }
            )

            // Verifying behaviour
            Mockito.verify(kittensRepository).search(0)
            Mockito.verify(kittensRepository).favoritesInMemoryCache
        }
    }

    @Test
    fun `Given repository has favorite in memory When searching Should return success favorited`() {
        testCoroutineRule.runBlockingTest {
            // Initial setup
            Mockito.doReturn(listOf(
                KittenDomainModel(
                    id = "kitty1",
                    url = "http"
                )
            )).`when`(kittensRepository).search(0)

            Mockito.doReturn(mutableListOf(
                FavoriteDomainModel(
                    id = "fav1",
                    imageId = "kitty1"
                )
            ))
                .`when`(kittensRepository).favoritesInMemoryCache

            // Performing usecase
            val params = SearchUseCase.Params(
                page = 0
            )
            val useCase = SearchUseCase(kittensRepository)
            useCase.invoke(
                scope = this,
                params = params,
                onSuccess = { list ->
                    Assert.assertNotNull(list)
                    Assert.assertEquals(1, list.size)
                    list.first().let {
                        Assert.assertTrue(it.isFavorite)
                        Assert.assertEquals("fav1", it.favoriteId)
                    }
                },
                onError = { error -> Assert.fail(error.message) }
            )

            // Verifying behaviour
            Mockito.verify(kittensRepository).search(0)
            Mockito.verify(kittensRepository).favoritesInMemoryCache
        }
    }

    @Test
    fun `Given repository is down When searching Should return error`() {
        testCoroutineRule.runBlockingTest {
            // Initial setup
            Mockito.doThrow(RuntimeException("Server is down"))
                .`when`(kittensRepository).search(0)

            // Performing usecase
            val params = SearchUseCase.Params(
                page = 0
            )
            val useCase = SearchUseCase(kittensRepository)
            useCase.invoke(
                scope = this,
                params = params,
                onSuccess = { _ -> Assert.fail("should not get in here") },
                onError = { error -> Assert.assertNotNull(error) }
            )

            // Verifying behaviour
            Mockito.verify(kittensRepository).search(0)
        }
    }
}