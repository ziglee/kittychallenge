package net.cassiolandim.kittychallenge.ui.main.model

data class KittenUiModel(
    val id: String,
    val url: String,
    var isFavorite: Boolean = false,
    var favoriteId: String? = null
)