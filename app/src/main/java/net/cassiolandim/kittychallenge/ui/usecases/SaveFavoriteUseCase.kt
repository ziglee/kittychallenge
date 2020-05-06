package net.cassiolandim.kittychallenge.ui.usecases

import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import javax.inject.Inject

class SaveFavoriteUseCase @Inject constructor(
    private val kittensRepository: KittensRepository
) : BaseUseCase<FavoriteDomainModel, SaveFavoriteUseCase.Params>() {

    data class Params(val imageId: String, val url: String)

    override suspend fun run(params: Params) : FavoriteDomainModel {
       return kittensRepository.saveFavorite(params.imageId, params.url)
    }
}