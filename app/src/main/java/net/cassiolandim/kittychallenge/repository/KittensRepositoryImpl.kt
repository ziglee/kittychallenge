package net.cassiolandim.kittychallenge.repository

import androidx.work.*
import net.cassiolandim.kittychallenge.database.FavoriteDatabaseModel
import net.cassiolandim.kittychallenge.database.FavoritesDao
import net.cassiolandim.kittychallenge.di.FavoriteDomainModel
import net.cassiolandim.kittychallenge.di.KittenDomainModel
import net.cassiolandim.kittychallenge.network.SaveFavoriteRequestNetworkModel
import net.cassiolandim.kittychallenge.network.TheCatApiService
import net.cassiolandim.kittychallenge.work.ImageDownloadWorker
import java.io.File
import javax.inject.Inject

class KittensRepositoryImpl @Inject constructor(
    private val theCatApiService: TheCatApiService,
    private val workManager: WorkManager,
    private val favoritesDao: FavoritesDao
) : KittensRepository {

    override var favoritesInMemoryCache = mutableListOf<FavoriteDomainModel>()

    override suspend fun search(page: Int): List<KittenDomainModel> {
        if (favoritesInMemoryCache.isEmpty()) {
            favoritesInMemoryCache = favorites().toMutableList()
        }

        return theCatApiService.search(page).map {
            KittenDomainModel(
                id = it.id,
                url = it.url
            )
        }
    }

    override suspend fun saveFavourite(imageId: String, url: String) : FavoriteDomainModel {
        val response = theCatApiService
            .saveFavourite(SaveFavoriteRequestNetworkModel(imageId))
        val model = FavoriteDomainModel(
            id = response.id,
            imageId = imageId
        )
        favoritesInMemoryCache.add(model)

        favoritesDao.insert(FavoriteDatabaseModel(
            id = model.id,
            imageId = model.imageId
        ))

        enqueueSaveImageWorker(model.id, url)

        return model
    }

    private fun enqueueSaveImageWorker(favoriteId: String, url: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val inputData = workDataOf(
            ImageDownloadWorker.KEY_URL to url,
            ImageDownloadWorker.KEY_FAVORITE_ID to favoriteId
        )
        val work = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(work)
    }

    override suspend fun deleteFavorite(favoriteId: String, baseDirectory: File) {
        theCatApiService.deleteFavorite(favoriteId)
        favoritesDao.delete(FavoriteDatabaseModel(id = favoriteId, imageId = ""))
        favoritesInMemoryCache.removeIf { it.id == favoriteId }
        val file = File(baseDirectory, "$favoriteId.jpg")
        if (file.exists()) file.delete()
    }

    override suspend fun favorites(): List<FavoriteDomainModel> {
        val favourites = theCatApiService.favourites()

        favoritesDao.deleteAllAndInsertAll(favourites.map {
            FavoriteDatabaseModel(
                id = it.id,
                imageId = it.image_id
            )
        })

        return favourites.map {
            FavoriteDomainModel(
                id = it.id,
                imageId = it.image_id
            )
        }
    }

    override suspend fun favoritesLocal(): List<FavoriteDomainModel> {
        return favoritesDao.findAll().map { dbModel ->
            FavoriteDomainModel(
                id = dbModel.id,
                imageId = dbModel.imageId
            )
        }
    }
}