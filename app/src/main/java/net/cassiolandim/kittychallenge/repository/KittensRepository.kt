package net.cassiolandim.kittychallenge.repository

import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.domain.KittenDomainModel
import java.io.File

interface KittensRepository {
    var favoritesInMemoryCache: MutableList<FavoriteDomainModel>
    suspend fun search(page: Int): List<KittenDomainModel>
    suspend fun saveFavorite(imageId: String, url: String) : FavoriteDomainModel
    suspend fun deleteFavorite(favoriteId: String, baseDirectory: File)
    suspend fun favorites(): List<FavoriteDomainModel>
    suspend fun favoritesLocal(): List<FavoriteDomainModel>
}