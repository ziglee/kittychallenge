package net.cassiolandim.kittychallenge

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import net.cassiolandim.kittychallenge.ui.MainViewModel
import net.cassiolandim.kittychallenge.ui.main.MainFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @Test
    fun given_When_first_displayed_Should_show_progress() {
        val mainViewModel = mockk<MainViewModel>()
        val app = ApplicationProvider.getApplicationContext<MyApplication>()
        app.appComponent.mainViewModel = mainViewModel

        val scenario = launchFragmentInContainer<MainFragment>()
        onView(withId(R.id.progressLayout)).check(matches(isDisplayed()))
    }
}