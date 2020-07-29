package net.cassiolandim.kittychallenge.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.usecases.SaveFavoriteUseCase
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class SaveFavoriteUseCaseUnitTest {

    @Test
    fun `Given repository is ok When searching Should return success`() {
        runBlockingTest {
            // Initial setup
            val kittensRepository = mockk<KittensRepository>()
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