package net.cassiolandim.kittychallenge.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.usecases.SaveFavoriteUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SaveFavoriteUseCaseUnitTest {

    @MockK
    lateinit var kittensRepository: KittensRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Given an image id When saving a favorite Should return the favorite model`() {
        runBlockingTest {
            // Initial setup
            coEvery {
                kittensRepository.saveFavorite(
                    imageId = "imageid1",
                    url = "url1"
                )
            } returns FavoriteDomainModel(
                id = "id1",
                imageId = "imageid1"
            )

            // Performing usecase
            val params = SaveFavoriteUseCase.Params(
                imageId = "imageid1",
                url = "url1"
            )
            val useCase = SaveFavoriteUseCase(kittensRepository)
            useCase.run(params).apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("id1", id)
                Assert.assertEquals("imageid1", imageId)
            }

            // Verifying behaviour
            coVerify {
                kittensRepository.saveFavorite(
                    imageId = "imageid1",
                    url = "url1"
                )
            }
        }
    }
}