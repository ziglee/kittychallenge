package net.cassiolandim.kittychallenge.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.not

@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var fragmentFactory: FragmentFactory

    private val fragment = CustomMainFragment()
    private val networkState = MutableLiveData<NetworkState>()
    private val kittens = MutableLiveData<List<KittenUiModel>>()
    private val savedFavoriteIndex = MutableLiveData<Int>()
    private val deletedFavoriteIndex = MutableLiveData<Int>()
    private val favorites = MutableLiveData<List<FavoriteUiModel>>()

    @Before
    fun setUp() {
        viewModel = mockk()
        every { viewModel.networkState } returns networkState
        every { viewModel.kittens } returns kittens
        every { viewModel.savedFavoriteIndex } returns savedFavoriteIndex
        every { viewModel.deletedFavoriteIndex } returns deletedFavoriteIndex
        every { viewModel.favorites } returns favorites
        every { viewModel.firstPageSearch() } returns Unit
        every { viewModel.kittenList } returns mutableListOf()

        fragment.viewModel = viewModel

        fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                return fragment
            }
        }
    }

    @Test
    fun given_network_loading_When_loading_Should_show_progress() {
        /* Given */
        networkState.postValue(NetworkState.LOADING)

        /* When */
        launchFragmentInContainer<CustomMainFragment>(factory = fragmentFactory)

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun given_network_success_When_loading_Should_not_show_progress() {
        /* Given */
        networkState.postValue(NetworkState.LOADED)

        /* When */
        launchFragmentInContainer<CustomMainFragment>(factory = fragmentFactory)

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_network_fail_When_request_returns_error_Should_show_empty_state() {
        /* Given */
        networkState.postValue(NetworkState.error(""))

        /* When */
        launchFragmentInContainer<CustomMainFragment>(factory = fragmentFactory)

        /* Then */
        onView(withId(R.id.emptyStateLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.progressLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_list_of_kittens_When_searching_Should_show_list() {
        /* Given */
        kittens.postValue(listOf(
            KittenUiModel(
                id = "id1",
                url = "url1",
                isFavorite = false
            )
        ))

        /* When */
        launchFragmentInContainer<CustomMainFragment>(factory = fragmentFactory)

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_empty_list_of_kittens_When_searching_Should_show_empty_state() {
        /* Given */
        kittens.postValue(emptyList())

        /* When */
        launchFragmentInContainer<CustomMainFragment>(factory = fragmentFactory)

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(isDisplayed()))
    }
}
