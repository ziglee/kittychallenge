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
import net.cassiolandim.kittychallenge.ui.usecases.FavoritesUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoritesUseCaseUnitTest {

    @MockK
    lateinit var kittensRepository: KittensRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Given repository has local items When listing favorites Should return list`() {
        runBlockingTest {
            // Initial setup
            coEvery { kittensRepository.favoritesLocal() } returns listOf(
                FavoriteDomainModel(
                    id = "id1",
                    imageId = "imageid1"
                )
            )

            // Performing usecase
            val useCase = FavoritesUseCase(kittensRepository)
            useCase.run(Unit).apply {
                Assert.assertNotNull(this)
                Assert.assertEquals(1, size)
                first().let {
                    Assert.assertEquals("id1", it.id)
                    Assert.assertEquals("imageid1", it.imageId)
                }
            }

            // Verifying behaviour
            coVerify{ kittensRepository.favoritesLocal() }
        }
    }
}