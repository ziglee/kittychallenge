package net.cassiolandim.kittychallenge.ui.favorites

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
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
import net.cassiolandim.kittychallenge.BaseApplication
import net.cassiolandim.kittychallenge.PerformClickViewWithIdAction
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.di.KittensModule
import net.cassiolandim.kittychallenge.domain.FavoriteDomainModel
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.repository.KittensRepository
import net.cassiolandim.kittychallenge.ui.MainActivityTestRule
import net.cassiolandim.kittychallenge.ui.main.KittenViewHolder
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.*
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

    @Module
    @InstallIn(ApplicationComponent::class)
    object TestModule {

        @Singleton
        @Provides
        fun providesKittensRepository(): KittensRepository {
            val app = ApplicationProvider.getApplicationContext<BaseApplication>()
            val baseDirectory = app.getOutputDirectory()

            val repository = mockk<KittensRepository>()

            coEvery { repository.favoritesLocal() } returns listOf(
                FavoriteDomainModel(
                    id = "id1",
                    imageId = "img1"
                )
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
    fun given_list_of_favorites_When_searching_Should_show_list() {
        onView(withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.emptyStateMessage))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.recyclerView)).check(RecyclerViewItemCountAssertion(1));
    }

    @Test
    fun given_list_of_favorites_When_click_to_unfavorite_Should_delete() {
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<KittenViewHolder>(0,
                PerformClickViewWithIdAction(R.id.deleteLayout)
            ))

        // verify
    }
}

class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        val recyclerView: RecyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter?.itemCount, `is`(expectedCount))
    }

}