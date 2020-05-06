package net.cassiolandim.kittychallenge.repository

import net.cassiolandim.kittychallenge.di.FavoriteDomainModel
import net.cassiolandim.kittychallenge.di.KittenDomainModel
import java.io.File

interface KittensRepository {
    var favoritesInMemoryCache: MutableList<FavoriteDomainModel>
    suspend fun search(page: Int): List<KittenDomainModel>
    suspend fun saveFavourite(imageId: String, url: String) : FavoriteDomainModel
    suspend fun deleteFavorite(favoriteId: String, baseDirectory: File)
    suspend fun favorites(): List<FavoriteDomainModel>
    suspend fun favoritesLocal(): List<FavoriteDomainModel>
}