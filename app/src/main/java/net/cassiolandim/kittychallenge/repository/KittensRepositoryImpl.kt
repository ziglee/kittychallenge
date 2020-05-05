package net.cassiolandim.kittychallenge.repository

import net.cassiolandim.kittychallenge.di.KittenDomainModel
import net.cassiolandim.kittychallenge.network.TheCatApiService
import javax.inject.Inject

class KittensRepositoryImpl @Inject constructor(
    private val theCatApiService: TheCatApiService
) : KittensRepository {

    override fun search(page: Int): List<KittenDomainModel> {
        return theCatApiService.search(page).map {
            KittenDomainModel(
                id = it.id,
                url = it.url
            )
        }
    }
}