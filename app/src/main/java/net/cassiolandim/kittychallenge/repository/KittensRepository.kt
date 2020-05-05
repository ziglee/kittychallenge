package net.cassiolandim.kittychallenge.repository

import net.cassiolandim.kittychallenge.di.FavoriteDomainModel
import net.cassiolandim.kittychallenge.di.KittenDomainModel
import java.io.File

interface KittensRepository {
    var favoritesCache: MutableList<FavoriteDomainModel>
    suspend fun search(page: Int): List<KittenDomainModel>
    suspend fun saveFavourite(
        imageId: String,
        url: String,
        outputDirectory: File
    ) : FavoriteDomainModel
    suspend fun deleteFavorite(favoriteId: String)
    suspend fun favorites(): List<FavoriteDomainModel>
}