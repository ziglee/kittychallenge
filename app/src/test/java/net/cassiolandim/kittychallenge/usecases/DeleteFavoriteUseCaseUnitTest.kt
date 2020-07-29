package net.cassiolandim.kittychallenge.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.usecases.DeleteFavoriteUseCase
import org.junit.Before
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class DeleteFavoriteUseCaseUnitTest {

    @MockK
    lateinit var kittensRepository: KittensRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Given a favorite id When deleting a favorite Should result ok`() {
        runBlockingTest {
            val baseDirectory = File("")
            // Initial setup
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