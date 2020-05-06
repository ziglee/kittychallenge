package net.cassiolandim.kittychallenge.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.cassiolandim.kittychallenge.di.FavoriteDomainModel
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import net.cassiolandim.kittychallenge.ui.main.usecases.DeleteFavoriteUseCase
import net.cassiolandim.kittychallenge.ui.main.usecases.SaveFavoriteUseCase
import net.cassiolandim.kittychallenge.ui.main.usecases.SearchUseCase
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase
) : ViewModel() {

    private val _kittens = MutableLiveData<List<KittenUiModel>>()
    val kittens: LiveData<List<KittenUiModel>> = _kittens

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    private val _savedFavorite = MutableLiveData<FavoriteDomainModel>()
    val savedFavorite: LiveData<FavoriteDomainModel> = _savedFavorite

    fun search(page: Int) {
        _networkState.value = NetworkState.LOADING
        searchUseCase(
            scope = viewModelScope,
            params = SearchUseCase.Params(page),
            onError = {
                _networkState.value = NetworkState.error(it.message)
                Timber.e(it)
            },
            onSuccess = {
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
            onSuccess = { _savedFavorite.value = it }
        )
    }

    fun deleteFavorite(id: String, baseDirectory: File) {
        deleteFavoriteUseCase(
            scope = viewModelScope,
            params = DeleteFavoriteUseCase.Params(id, baseDirectory),
            onError = { Timber.e(it) },
            onSuccess = { Timber.d("deleted favorite #$id") }
        )
    }
}
