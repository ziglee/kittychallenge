package net.cassiolandim.kittychallenge.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.cassiolandim.kittychallenge.MyTestApplication
import net.cassiolandim.kittychallenge.PerformClickViewWithIdAction
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.not
import org.junit.Assert

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
        launchFragmentInContainer<CustomMainFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

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
        launchFragmentInContainer<CustomMainFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

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
        launchFragmentInContainer<CustomMainFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

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
        launchFragmentInContainer<CustomMainFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_empty_list_of_kittens_When_searching_Should_show_empty_state() {
        /* Given */
        kittens.postValue(emptyList())

        /* When */
        launchFragmentInContainer<CustomMainFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun given_list_of_kittens_When_click_to_favorite_Should_save_favorite() {
        val kitten = KittenUiModel(
            id = "id1",
            url = "url1",
            isFavorite = false
        )

        val list = listOf(
            kitten
        )

        every { viewModel.kittenList } returns list.toMutableList()
        every { viewModel.isLoading } returns true
        every { viewModel.saveFavorite(kitten.id, kitten.url) } returns Unit

                /* Given */
        kittens.postValue(list)

        /* When */
        launchFragmentInContainer<CustomMainFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        /* Then */
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<KittenViewHolder>(0, PerformClickViewWithIdAction(R.id.favoriteLayout)))

        verify { viewModel.kittenList }
        verify { viewModel.isLoading }
        verify { viewModel.saveFavorite(kitten.id, kitten.url) }

        Assert.assertTrue(kitten.isFavorite)
    }

    @Test
    fun given_list_of_kittens_When_click_to_unfavorite_Should_delete_favorite() {
        val app = ApplicationProvider.getApplicationContext<MyTestApplication>()
        val baseDirectory = app.getOutputDirectory()
        val kitten = KittenUiModel(
            id = "id1",
            url = "url1",
            isFavorite = true,
            favoriteId = "fav1"
        )

        val list = listOf(
            kitten
        )

        every { viewModel.kittenList } returns list.toMutableList()
        every { viewModel.isLoading } returns true
        every { viewModel.deleteFavorite(kitten.favoriteId!!, baseDirectory) } returns Unit

        /* Given */
        kittens.postValue(list)

        /* When */
        launchFragmentInContainer<CustomMainFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        /* Then */
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<KittenViewHolder>(0,
                PerformClickViewWithIdAction(R.id.favoriteLayout)))

        verify { viewModel.kittenList }
        verify { viewModel.isLoading }
        verify { viewModel.deleteFavorite(kitten.favoriteId!!, baseDirectory) }

        Assert.assertFalse(kitten.isFavorite)
    }
}
