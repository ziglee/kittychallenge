package net.cassiolandim.kittychallenge.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.domain.KittenDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.usecases.SearchUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class SearchUseCaseUnitTest {

    @MockK
    lateinit var kittensRepository: KittensRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Given repository is ok When searching Should return success`() {
        runBlockingTest {
            // Initial setup
            coEvery { kittensRepository.search(0) } returns listOf(
                KittenDomainModel(
                    id = "x",
                    url = "http"
                )
            )

            every { kittensRepository.favoritesInMemoryCache } returns mutableListOf<FavoriteDomainModel>()

            // Performing usecase
            val params = SearchUseCase.Params(
                page = 0
            )
            val useCase = SearchUseCase(kittensRepository)
            useCase.run(params).apply {
                Assert.assertNotNull(this)
                Assert.assertEquals(1, size)
                first().let {
                    Assert.assertEquals("x", it.id)
                    Assert.assertEquals("http", it.url)
                    Assert.assertFalse(it.isFavorite)
                }
            }

            // Verifying behaviour
            coVerify{ kittensRepository.search(0) }
            coVerify{ kittensRepository.favoritesInMemoryCache }
        }
    }

    @Test
    fun `Given repository has favorite in memory When searching Should return success favorited`() {
        runBlockingTest {
            // Initial setup
            coEvery { kittensRepository.search(0) } returns listOf(
                KittenDomainModel(
                    id = "kitty1",
                    url = "http"
                )
            )

            coEvery { kittensRepository.favoritesInMemoryCache } returns mutableListOf(
                FavoriteDomainModel(
                    id = "fav1",
                    imageId = "kitty1"
                )
            )

            // Performing usecase
            val params = SearchUseCase.Params(
                page = 0
            )
            val useCase = SearchUseCase(kittensRepository)
            useCase.run(params).apply {
                Assert.assertNotNull(this)
                Assert.assertEquals(1, size)
                first().let {
                    Assert.assertTrue(it.isFavorite)
                    Assert.assertEquals("fav1", it.favoriteId)
                }
            }

            // Verifying behaviour
            coVerify { kittensRepository.search(0) }
            coVerify { kittensRepository.favoritesInMemoryCache }
        }
    }

    @Test
    fun `Given repository is down When searching Should return error`() {
        runBlockingTest {
            // Initial setup
            coEvery { kittensRepository.search(0) } throws RuntimeException("Server is down")

            // Performing usecase
            val useCase = SearchUseCase(kittensRepository)
            try {
                useCase.run(SearchUseCase.Params(page = 0))
                Assert.fail("should not get in here")
            } catch (e: Exception) {
                Assert.assertNotNull(e)
            }

            // Verifying behaviour
            coVerify { kittensRepository.search(0) }
        }
    }
}