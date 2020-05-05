package net.cassiolandim.kittychallenge.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import net.cassiolandim.kittychallenge.ui.main.usecases.SearchUseCase
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _kittens = MutableLiveData<List<KittenUiModel>>()
    val kittens: LiveData<List<KittenUiModel>> = _kittens

    fun search(page: Int) {
        searchUseCase(
            scope = viewModelScope,
            params = SearchUseCase.Params(page = page),
            onError = { Timber.e(it) },
            onSuccess = { _kittens.value = it }
        )
    }
}
