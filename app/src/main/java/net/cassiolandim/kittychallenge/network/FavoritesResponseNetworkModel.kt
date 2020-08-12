package net.cassiolandim.kittychallenge.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoritesResponseNetworkModel(
    val id: String,
    val image_id: String
)