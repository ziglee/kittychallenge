package net.cassiolandim.kittychallenge.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.cassiolandim.kittychallenge.di.FavoriteDomainModel
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.usecases.FavoritesUseCase
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import net.cassiolandim.kittychallenge.ui.usecases.DeleteFavoriteUseCase
import net.cassiolandim.kittychallenge.ui.usecases.SaveFavoriteUseCase
import net.cassiolandim.kittychallenge.ui.usecases.SearchUseCase
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val favoritesUseCase: FavoritesUseCase,
    private val searchUseCase: SearchUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase
) : ViewModel() {

    val kittenList = mutableListOf<KittenUiModel>()
    private var page = 0
    var isLoading = true

    private val _kittens = MutableLiveData<List<KittenUiModel>>()
    val kittens: LiveData<List<KittenUiModel>> = _kittens

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    private val _savedFavorite = MutableLiveData<FavoriteDomainModel>()
    val savedFavorite: LiveData<FavoriteDomainModel> = _savedFavorite

    private val _favorites = MutableLiveData<List<FavoriteUiModel>>()
    val favorites: LiveData<List<FavoriteUiModel>> = _favorites

    private val _deletedFavoriteIndex = MutableLiveData<Int>()
    val deletedFavoriteIndex: LiveData<Int> = _deletedFavoriteIndex

    init {
        fetchFavorites()
    }

    fun firstPageSearch() {
        isLoading = true
        kittenList.clear()
        page = 0
        search(page)
    }

    fun increasePageAndSearch() {
        isLoading = true
        search(++page)
    }

    private fun search(page: Int) {
        _networkState.value = NetworkState.LOADING
        searchUseCase(
            scope = viewModelScope,
            params = SearchUseCase.Params(page),
            onError = {
                isLoading = false
                _networkState.value = NetworkState.error(it.message)
                Timber.e(it)
            },
            onSuccess = {
                isLoading = false
                _kittens.value = it
                _networkState.value = NetworkState.LOADED
            }
        )
    }

    fun saveFavorite(id: String, url: String) {
        saveFavoriteUseCase(
            scope = viewModelScope,
            params = SaveFavoriteUseCase.Params(id, url),
            onError = { Timber.e(it) },
            onSuccess = {
                _savedFavorite.value = it
                fetchFavorites()
            }
        )
    }

    fun deleteFavorite(id: String, baseDirectory: File) {
        deleteFavoriteUseCase(
            scope = viewModelScope,
            params = DeleteFavoriteUseCase.Params(id, baseDirectory),
            onError = { Timber.e(it) },
            onSuccess = {
                fetchFavorites()
                val indexOf = kittenList.indexOfFirst { it.favoriteId != null && it.favoriteId == id }
                if (indexOf >= 0) {
                    kittenList[indexOf].let {
                        it.favoriteId = null
                        it.isFavorite = false
                    }
                    _deletedFavoriteIndex.value = indexOf
                }
            }
        )
    }

    private fun fetchFavorites() {
        favoritesUseCase(
            scope = viewModelScope,
            params = Unit,
            onError = { Timber.e(it) },
            onSuccess = { _favorites.value = it }
        )
    }
}
