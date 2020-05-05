package net.cassiolandim.kittychallenge.repository

import net.cassiolandim.kittychallenge.copyInputStreamToFile
import net.cassiolandim.kittychallenge.di.FavoriteDomainModel
import net.cassiolandim.kittychallenge.di.KittenDomainModel
import net.cassiolandim.kittychallenge.network.SaveFavoriteRequestNetworkModel
import net.cassiolandim.kittychallenge.network.TheCatApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import javax.inject.Inject


class KittensRepositoryImpl @Inject constructor(
    private val theCatApiService: TheCatApiService
) : KittensRepository {

    override var favoritesCache = mutableListOf<FavoriteDomainModel>()

    override suspend fun search(page: Int): List<KittenDomainModel> {
        if (favoritesCache.isEmpty()) {
            favoritesCache = favorites().toMutableList()
        }

        return theCatApiService.search(page).map {
            KittenDomainModel(
                id = it.id,
                url = it.url
            )
        }
    }

    override suspend fun saveFavourite(
        imageId: String,
        url: String,
        outputDirectory: File
    ) : FavoriteDomainModel {
        val response = theCatApiService
            .saveFavourite(SaveFavoriteRequestNetworkModel(imageId))
        val model = FavoriteDomainModel(
            id = response.id,
            imageId = imageId
        )
        favoritesCache.add(model)

        saveImage(imageId, url, outputDirectory)

        return model
    }

    private fun saveImage(imageId: String, url: String, outputDirectory: File) {
        val request = Request.Builder().url(url).build()
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        response.body?.let{ body ->
            File(outputDirectory, "$imageId.jpg")
                .copyInputStreamToFile(body.byteStream())
        }
    }

    override suspend fun deleteFavorite(favoriteId: String) {
        theCatApiService.deleteFavorite(favoriteId)
        favoritesCache.removeIf { it.id == favoriteId }
    }

    override suspend fun favorites(): List<FavoriteDomainModel> {
        return theCatApiService.favourites().map {
            FavoriteDomainModel(
                id = it.id,
                imageId = it.image_id
            )
        }
    }
}