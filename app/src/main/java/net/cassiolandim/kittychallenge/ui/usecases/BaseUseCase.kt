package net.cassiolandim.kittychallenge.ui.usecases

abstract class BaseUseCase<out Type, in Params> where Type : Any {
    abstract suspend fun run(params: Params): Type
}