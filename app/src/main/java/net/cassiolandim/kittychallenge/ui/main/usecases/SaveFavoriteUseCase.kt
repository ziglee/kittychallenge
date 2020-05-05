package net.cassiolandim.kittychallenge.ui.main.usecases

import net.cassiolandim.kittychallenge.di.BaseUseCase
import net.cassiolandim.kittychallenge.di.FavoriteDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import java.io.File
import javax.inject.Inject

class SaveFavoriteUseCase @Inject constructor(
    private val kittensRepository: KittensRepository
) : BaseUseCase<FavoriteDomainModel, SaveFavoriteUseCase.Params>() {

    data class Params(val imageId: String, val url: String, val outputDirectory: File)

    override suspend fun run(params: Params) : FavoriteDomainModel {
       return kittensRepository.saveFavourite(params.imageId, params.url, params.outputDirectory)
    }
}