package net.cassiolandim.kittychallenge.network

import retrofit2.http.*

interface TheCatApiService {

    companion object {
        const val API_KEY = "212be267-748c-42c3-96dd-7e4631fe68c1"
        const val API_ENDPOINT = "https://api.thecatapi.com/v1/"
    }

    @GET("images/search")
    suspend fun search(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("order") order: String = "Desc"
    ): List<SearchResponseNetworkModel>

    @GET("favourites")
    suspend fun favourites(): List<FavoritesResponseNetworkModel>

    @POST("favourites")
    suspend fun saveFavourite(
        @Body body: SaveFavoriteRequestNetworkModel
    ): SaveFavoriteResponseNetworkModel

    @DELETE("favourites/{id}")
    suspend fun deleteFavorite(
        @Path("id") id: String
    ): DeleteFavoriteResponseNetworkModel
}