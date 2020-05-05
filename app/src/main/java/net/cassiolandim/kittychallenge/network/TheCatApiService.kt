package net.cassiolandim.kittychallenge.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TheCatApiService {

    companion object {
        const val API_KEY = "MTkxNzY4"
        const val API_ENDPOINT = "https://api.thecatapi.com/v1/images/"
    }

    @GET("search")
    fun search(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 30,
        @Query("order") order: String = "Desc"
    ): Call<List<KittensNetworkModel>>
}