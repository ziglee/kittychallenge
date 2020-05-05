package net.cassiolandim.kittychallenge.ui.main.usecases

import net.cassiolandim.kittychallenge.di.BaseUseCase
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val kittensRepository: KittensRepository
) : BaseUseCase<List<KittenUiModel>, SearchUseCase.Params>() {

    data class Params(val page: Int)

    override suspend fun run(params: Params) : List<KittenUiModel> {
       return kittensRepository.search(params.page).map {
            KittenUiModel(
                id = it.id,
                url = it.url
            )
        }
    }
}