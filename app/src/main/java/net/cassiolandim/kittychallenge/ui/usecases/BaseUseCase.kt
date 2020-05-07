package net.cassiolandim.kittychallenge.ui.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class BaseUseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Type

    open operator fun invoke(
        scope: CoroutineScope,
        params: Params,
        onSuccess: (Type) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val backgroundJob = scope.async { run(params) }
        scope.launch {
            try {
                val result = backgroundJob.await()
                onSuccess(result)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}