package net.cassiolandim.kittychallenge.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.usecases.DeleteFavoriteUseCase
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class DeleteFavoriteUseCaseUnitTest {

    @Test
    fun `Given repository is ok When searching Should return success`() {
        runBlockingTest {
            val baseDirectory = File("")
            // Initial setup
            val kittensRepository = mockk<KittensRepository>()
            coEvery {
                kittensRepository.deleteFavorite(
                    favoriteId = "imageid1",
                    baseDirectory = baseDirectory
                )
            } returns Unit

            // Performing usecase
            val params = DeleteFavoriteUseCase.Params(
                id = "imageid1",
                baseDirectory = baseDirectory
            )
            DeleteFavoriteUseCase(kittensRepository).run(params)

            // Verifying behaviour
            coVerify {
                kittensRepository.deleteFavorite(
                    favoriteId = "imageid1",
                    baseDirectory = baseDirectory
                )
            }
        }
    }
}