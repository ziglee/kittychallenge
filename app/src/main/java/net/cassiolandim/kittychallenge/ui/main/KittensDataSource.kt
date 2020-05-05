package net.cassiolandim.kittychallenge.ui.main

import androidx.paging.PageKeyedDataSource
import net.cassiolandim.kittychallenge.network.KittensNetworkModel
import net.cassiolandim.kittychallenge.network.TheCatApiService
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class KittensDataSource  @Inject constructor(
    private val theCatApiService: TheCatApiService
) : PageKeyedDataSource<Int, KittenUiModel>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, KittenUiModel>
    ) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, KittenUiModel>) {
        theCatApiService.search(params.key).enqueue(
            object : retrofit2.Callback<List<KittensNetworkModel>> {
                override fun onFailure(call: Call<List<KittensNetworkModel>>, t: Throwable) {
                    //TODO("Not yet implemented")
                }

                override fun onResponse(
                    call: Call<List<KittensNetworkModel>>,
                    response: Response<List<KittensNetworkModel>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        callback.onResult(data?.map {
                            KittenUiModel(
                                id = it.id,
                                url = it.url
                            )
                        }?.toMutableList() ?: mutableListOf(), params.key + 1)
                    }
                }
            }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, KittenUiModel>) {

    }
}