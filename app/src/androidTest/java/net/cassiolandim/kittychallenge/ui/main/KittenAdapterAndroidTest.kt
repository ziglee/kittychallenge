package net.cassiolandim.kittychallenge.ui.main

import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.cassiolandim.kittychallenge.MainActivity
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.ui.MainActivityTestRule
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class KittenAdapterAndroidTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun bind_view_holder() {
        val list = listOf(
            KittenUiModel(id = "id1", url = "url1"),
            KittenUiModel(id =  "id2", url = "url2", isFavorite = true, favoriteId = "id2")
        )
        val toggleFavoriteCallback = { _ : KittenUiModel -> }
        val adapter = KittensAdapter(list, toggleFavoriteCallback)

        val scenario = launchActivity<MainActivity>()
        scenario.moveToState(Lifecycle.State.CREATED)

        scenario.onActivity { activity ->
            val viewHolder = adapter.onCreateViewHolder(FrameLayout(activity), 0)
            Assert.assertNotNull(viewHolder)

            adapter.onBindViewHolder(viewHolder, 0)
            adapter.onBindViewHolder(viewHolder, 1)
        }
    }
}