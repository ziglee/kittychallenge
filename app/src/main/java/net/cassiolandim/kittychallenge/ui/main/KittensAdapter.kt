package net.cassiolandim.kittychallenge.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.cassiolandim.kittychallenge.databinding.RowItemBinding
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel

class KittensAdapter : PagedListAdapter<KittenUiModel, KittenViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<KittenUiModel>() {
            override fun areItemsTheSame(oldItem: KittenUiModel, newItem: KittenUiModel) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(
                oldItem: KittenUiModel, newItem: KittenUiModel) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KittenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowItemBinding.inflate(layoutInflater)
        return KittenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KittenViewHolder, position: Int) {
        val kitten: KittenUiModel? = getItem(position)
        holder.bindTo(kitten)
    }
}

class KittenViewHolder(private val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(kitten: KittenUiModel?) {
        kitten?.let {
            binding.model = it
        }
    }
}