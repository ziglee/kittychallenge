package net.cassiolandim.kittychallenge.ui.main.usecases

import net.cassiolandim.kittychallenge.di.BaseUseCase
import net.cassiolandim.kittychallenge.repository.KittensRepository
import java.io.File
import javax.inject.Inject

class DeleteFavoriteUseCase @Inject constructor(
    private val kittensRepository: KittensRepository
) : BaseUseCase<Unit, DeleteFavoriteUseCase.Params>() {

    data class Params(val id: String, val baseDirectory: File)

    override suspend fun run(params: Params) {
       kittensRepository.deleteFavorite(params.id, params.baseDirectory)
    }
}