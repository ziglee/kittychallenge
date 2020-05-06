package net.cassiolandim.kittychallenge.ui.usecases

import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import javax.inject.Inject

class FavoritesUseCase @Inject constructor(
    private val kittensRepository: KittensRepository
) : BaseUseCase<List<FavoriteUiModel>, Unit>() {

    override suspend fun run(params: Unit) : List<FavoriteUiModel> {
        return kittensRepository.favoritesLocal().map {
            FavoriteUiModel(
                id = it.id,
                imageId = it.imageId
            )
        }
    }
}