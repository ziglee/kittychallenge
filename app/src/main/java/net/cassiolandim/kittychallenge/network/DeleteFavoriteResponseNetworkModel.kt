package net.cassiolandim.kittychallenge.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteFavoriteResponseNetworkModel(
    val message: String
)