package net.cassiolandim.kittychallenge.ui.main

import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import net.cassiolandim.kittychallenge.MainActivity
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.junit.Assert
import org.junit.Test

class KittenAdapterAndroidTest {

    @Test
    fun getItemAtPosition() {
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
            adapter.onBindViewHolder(viewHolder, 0)

            Assert.assertNotNull(viewHolder)
        }
    }
}