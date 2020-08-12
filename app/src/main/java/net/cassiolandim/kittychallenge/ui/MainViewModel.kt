package net.cassiolandim.kittychallenge.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import net.cassiolandim.kittychallenge.ui.usecases.*
import timber.log.Timber
import java.io.File

class MainViewModel @ViewModelInject constructor(
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

    private val _savedFavoriteIndex = MutableLiveData<Int>()
    val savedFavoriteIndex: LiveData<Int> = _savedFavoriteIndex

    private val _favorites = MutableLiveData<List<FavoriteUiModel>>()
    val favorites: LiveData<List<FavoriteUiModel>> = _favorites

    private val _deletedFavoriteIndex = MutableLiveData<Int>()
    val deletedFavoriteIndex: LiveData<Int> = _deletedFavoriteIndex

    init {
        fetchFavorites()
        firstPageSearch()
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

        executeUseCase(
            useCase = searchUseCase,
            params = SearchUseCase.Params(page),
            onError = {
                isLoading = false
                _networkState.value = NetworkState.error(it.message)
                Timber.e(it)
            },
            onSuccess = {
                isLoading = false
                kittenList.addAll(it)
                _kittens.value = it
                _networkState.value = NetworkState.LOADED
            }
        )
    }

    fun saveFavorite(id: String, url: String) {
        executeUseCase(
            useCase = saveFavoriteUseCase,
            params = SaveFavoriteUseCase.Params(id, url),
            onError = { Timber.e(it) },
            onSuccess = {
                val indexOf = kittenList.indexOfFirst { it.id == id }
                if (indexOf >= 0) {
                    kittenList[indexOf].let { kitten ->
                        kitten.favoriteId = it.id
                        kitten.isFavorite = true
                    }
                    _savedFavoriteIndex.value = indexOf
                }
                fetchFavorites()
            }
        )
    }

    fun deleteFavorite(id: String, baseDirectory: File) {
        executeUseCase(
            useCase = deleteFavoriteUseCase,
            params = DeleteFavoriteUseCase.Params(id, baseDirectory),
            onError = { Timber.e(it) },
            onSuccess = {
                val indexOf = kittenList
                    .indexOfFirst { it.favoriteId != null && it.favoriteId == id }
                if (indexOf >= 0) {
                    kittenList[indexOf].let {
                        it.favoriteId = null
                        it.isFavorite = false
                    }
                    _deletedFavoriteIndex.value = indexOf
                }
                fetchFavorites()
            }
        )
    }

    private fun fetchFavorites() {
        executeUseCase(
            useCase = favoritesUseCase,
            params = Unit,
            onSuccess = { _favorites.value = it },
            onError = { Timber.e(it) }
        )
    }
}
