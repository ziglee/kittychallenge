package net.cassiolandim.kittychallenge.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.cassiolandim.kittychallenge.*
import net.cassiolandim.kittychallenge.di.KittensModule
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.network.NetworkState
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.MainActivityTestRule
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(KittensModule::class)
@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = MainActivityTestRule(R.id.navigation_main)

    private lateinit var viewModel: MainViewModel
    private lateinit var fragmentFactory: FragmentFactory

    private val fragment = MainFragment()
    private val networkState = MutableLiveData<NetworkState>()
    private val kittens = MutableLiveData<List<KittenUiModel>>()
    private val savedFavoriteIndex = MutableLiveData<Int>()
    private val deletedFavoriteIndex = MutableLiveData<Int>()
    private val favorites = MutableLiveData<List<FavoriteUiModel>>()

    @Module
    @InstallIn(ApplicationComponent::class)
    object TestModule {

        @Singleton
        @Provides
        fun providesKittensRepository(): KittensRepository {
            val repository = mockk<KittensRepository>()
            coEvery { repository.favoritesLocal() } returns listOf(
                FavoriteDomainModel(
                    id = "id1",
                    imageId = "img1"
                )
            )
            val app = ApplicationProvider.getApplicationContext<BaseApplication>()
            val baseDirectory = app.getOutputDirectory()
            coEvery {
                repository.deleteFavorite("id1", baseDirectory = baseDirectory)
            } returns Unit

            return repository
        }
    }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun given_network_loading_When_loading_Should_show_progress() {
        /* Given */
        networkState.postValue(NetworkState.LOADING)

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun given_network_success_When_loading_Should_not_show_progress() {
        /* Given */
        networkState.postValue(NetworkState.LOADED)

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_network_fail_When_request_returns_error_Should_show_empty_state() {
        /* Given */
        networkState.postValue(NetworkState.error(""))

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

        /* Then */
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_empty_list_of_kittens_When_searching_Should_show_empty_state() {
        /* Given */
        kittens.postValue(emptyList())

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
        val app = ApplicationProvider.getApplicationContext<MyApplication>()
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
