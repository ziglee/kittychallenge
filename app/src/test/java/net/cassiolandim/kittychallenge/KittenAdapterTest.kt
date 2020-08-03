package net.cassiolandim.kittychallenge

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.cassiolandim.kittychallenge.ui.main.KittenViewHolder
import net.cassiolandim.kittychallenge.ui.main.KittensAdapter
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import org.junit.Assert
import org.junit.Test

class KittenAdapterTest {

    @Test
    fun getItemAtPosition() {
        val kitten = KittenUiModel(id = "id1", url = "url1")
        val holder = mockk<KittenViewHolder>()
        every { holder.bindTo(kitten) } returns Unit

        val list = listOf(kitten)
        val toggleFavoriteCallback = { _ : KittenUiModel -> }
        val adapter = KittensAdapter(list, toggleFavoriteCallback)
        adapter.onBindViewHolder(holder, 0)

        Assert.assertEquals(1, adapter.itemCount)
        verify { holder.bindTo(kitten) }
    }
}