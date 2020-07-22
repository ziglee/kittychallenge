package net.cassiolandim.kittychallenge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.cassiolandim.kittychallenge.ui.usecases.BaseUseCase

fun <Type, Params> ViewModel.executeUseCase(
    useCase: BaseUseCase<Type, Params>,
    params: Params,
    onSuccess: (Type) -> Unit,
    onError: (Exception) -> Unit
) where Type : Any {
    val backgroundJob = viewModelScope.async { useCase.run(params) }
    viewModelScope.launch {
        try {
            val result = backgroundJob.await()
            onSuccess(result)
        } catch (e: Exception) {
            onError(e)
        }
    }
}