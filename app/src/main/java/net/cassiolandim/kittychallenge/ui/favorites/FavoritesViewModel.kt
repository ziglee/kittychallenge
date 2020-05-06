package net.cassiolandim.kittychallenge.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.favorites.usecases.FavoritesUseCase
import timber.log.Timber
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _favorites = MutableLiveData<List<FavoriteUiModel>>()
    val favorites: LiveData<List<FavoriteUiModel>> = _favorites

    init {
        favoritesUseCase(
            scope = viewModelScope,
            params = Unit,
            onError = { Timber.e(it) },
            onSuccess = { _favorites.value = it }
        )
    }
}