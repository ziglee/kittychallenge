package net.cassiolandim.kittychallenge.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SaveFavoriteResponseNetworkModel(
    val id: String,
    val message: String
)