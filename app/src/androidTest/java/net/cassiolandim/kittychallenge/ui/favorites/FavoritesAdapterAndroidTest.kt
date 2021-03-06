package net.cassiolandim.kittychallenge.ui.favorites

import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import net.cassiolandim.kittychallenge.MainActivity
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import org.junit.Assert
import org.junit.Test

class FavoritesAdapterAndroidTest {

    @Test
    fun bind_view_holder_and_notify_item_changed() {
        val list = listOf(
            FavoriteUiModel(id = "id1", imageId = "img1"),
            FavoriteUiModel(id =  "id2", imageId = "img2")
        )
        val deleteCallback = { _ : String -> }
        val adapter = FavoritesAdapter(deleteCallback)

        val scenario = launchActivity<MainActivity>()
        scenario.moveToState(Lifecycle.State.CREATED)

        scenario.onActivity { activity ->
            adapter.submitList(list)

            val viewHolder = adapter.onCreateViewHolder(FrameLayout(activity), 0)
            adapter.onBindViewHolder(viewHolder, 0)
            adapter.notifyItemChangedByFavoriteId("id2")

            Assert.assertNotNull(viewHolder)
        }
    }
}