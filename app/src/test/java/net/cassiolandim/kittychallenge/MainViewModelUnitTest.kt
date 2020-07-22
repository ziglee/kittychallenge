package net.cassiolandim.kittychallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.utils.TestCoroutineRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var networkStateObserver: Observer<NetworkState>

    @Test
    fun `Given When Should`() {
        testCoroutineRule.runBlockingTest {
            val viewModel = MainViewModel(

            )

            viewModel.networkState.observeForever(networkStateObserver)

            viewModel.networkState.removeObserver(networkStateObserver)
        }
    }
}