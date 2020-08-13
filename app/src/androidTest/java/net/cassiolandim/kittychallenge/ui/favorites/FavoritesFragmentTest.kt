package net.cassiolandim.kittychallenge.ui.favorites

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.cassiolandim.kittychallenge.*
import net.cassiolandim.kittychallenge.di.KittensModule
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.MainActivityTestRule
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.main.KittenViewHolder
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(KittensModule::class)
@RunWith(AndroidJUnit4::class)
class FavoritesFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = MainActivityTestRule(R.id.navigation_favorites)

    @BindValue
    lateinit var repository: KittensRepository

    private lateinit var viewModel: MainViewModel
    private lateinit var fragmentFactory: FragmentFactory

    private val fragment = FavoritesFragment()
    private val networkState = MutableLiveData<NetworkState>()
    private val kittens = MutableLiveData<List<KittenUiModel>>()
    private val savedFavoriteIndex = MutableLiveData<Int>()
    private val deletedFavoriteIndex = MutableLiveData<Int>()
    private val favorites = MutableLiveData<List<FavoriteUiModel>>()

    @Before
    fun setUp() {
        hiltRule.inject()

        viewModel = mockk()
        every { viewModel.networkState } returns networkState
        every { viewModel.kittens } returns kittens
        every { viewModel.savedFavoriteIndex } returns savedFavoriteIndex
        every { viewModel.deletedFavoriteIndex } returns deletedFavoriteIndex
        every { viewModel.favorites } returns favorites
        every { viewModel.firstPageSearch() } returns Unit
        every { viewModel.kittenList } returns mutableListOf()

        //fragment.viewModel = viewModel

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

        /* Then */
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.emptyStateMessage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun given_list_of_favorites_When_click_to_unfavorite_Should_delete() {

        /* Given */
        val app = ApplicationProvider.getApplicationContext<BaseApplication>()
        val baseDirectory = app.getOutputDirectory()
        val item = FavoriteUiModel(
            id = "id1",
            imageId = "img1"
        )

        every { viewModel.deleteFavorite(item.id, baseDirectory) } returns Unit

        favorites.postValue(listOf(
            item
        ))

        /* Then */
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<KittenViewHolder>(0,
                PerformClickViewWithIdAction(R.id.deleteLayout)))

        verify { viewModel.deleteFavorite(item.id, baseDirectory) }
    }
}