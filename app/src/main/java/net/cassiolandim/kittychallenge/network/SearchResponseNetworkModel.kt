package net.cassiolandim.kittychallenge.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponseNetworkModel(
    val id: String,
    val url: String
)