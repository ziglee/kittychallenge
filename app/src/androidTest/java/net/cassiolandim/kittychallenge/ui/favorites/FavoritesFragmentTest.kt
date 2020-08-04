package net.cassiolandim.kittychallenge.ui.favorites

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesFragmentTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var fragmentFactory: FragmentFactory

    private val fragment = CustomFavoritesFragment()
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
    fun given_network_ok_When_loading_Should_show_progress() {
        /* Given */
        networkState.postValue(NetworkState.LOADING)

        /* When */
        launchFragmentInContainer<CustomFavoritesFragment>(factory = fragmentFactory)

        /* Then */
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.emptyStateMessage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun given_list_of_favorites_When_searching_Should_show_list() {
        /* Given */
        favorites.postValue(listOf(
            FavoriteUiModel(
                id = "id1",
                imageId = "img1"
            )
        ))

        /* When */
        launchFragmentInContainer<CustomFavoritesFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory)

        /* Then */
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.emptyStateMessage))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun given_empty_list_of_favorites_When_searching_Should_show_empty_state_message() {
        /* Given */
        favorites.postValue(
            emptyList()
        )

        /* When */
        launchFragmentInContainer<CustomFavoritesFragment>(
            themeResId = R.style.AppTheme,
            factory = fragmentFactory)

        /* Then */
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.emptyStateMessage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}