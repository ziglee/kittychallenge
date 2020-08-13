package net.cassiolandim.kittychallenge.ui.main

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
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.mockk
import net.cassiolandim.kittychallenge.*
import net.cassiolandim.kittychallenge.di.KittensModule
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.domain.KittenDomainModel
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.MainActivityTestRule
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.not
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

    @Module
    @InstallIn(ApplicationComponent::class)
    object TestModule {

        @Singleton
        @Provides
        fun providesKittensRepository(): KittensRepository {
            val app = ApplicationProvider.getApplicationContext<BaseApplication>()
            val baseDirectory = app.getOutputDirectory()

            val repository = mockk<KittensRepository>()

            coEvery { repository.search(0) } returns listOf(
                KittenDomainModel(
                    id = "id1",
                    url = "url1"
                ),
                KittenDomainModel(
                    id = "id2",
                    url = "url2"
                )
            )

            coEvery { repository.favoritesLocal() } returns listOf(
                FavoriteDomainModel(
                    id = "id1",
                    imageId = "img1"
                )
            )

            coEvery { repository.favoritesInMemoryCache } returns mutableListOf(
                FavoriteDomainModel(
                    id = "id1",
                    imageId = "img1"
                )
            )

            coEvery {
                repository.saveFavorite("id2", "url2")
            } returns FavoriteDomainModel(
                id = "id2",
                imageId = "id2"
            )

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
    fun given_network_success_When_loading_Should_not_show_progress() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_list_of_kittens_When_searching_Should_show_list() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyStateLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun given_list_of_kittens_When_click_to_favorite_Should_save_favorite() {
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<KittenViewHolder>(
                0,
                PerformClickViewWithIdAction(R.id.favoriteLayout)
            ))

        // verify
    }

    @Test
    fun given_list_of_kittens_When_click_to_unfavorite_Should_delete_favorite() {
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<KittenViewHolder>(0,
                PerformClickViewWithIdAction(R.id.favoriteLayout)))

        // verify
    }
}
