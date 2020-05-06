package net.cassiolandim.kittychallenge.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.FavoriteItemBinding
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import java.io.File

class FavoritesAdapter : ListAdapter<FavoriteUiModel, KittenViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteUiModel>() {
            override fun areItemsTheSame(oldItem: FavoriteUiModel, newItem: FavoriteUiModel) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(
                oldItem: FavoriteUiModel, newItem: FavoriteUiModel) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KittenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FavoriteItemBinding.inflate(layoutInflater, parent,false)
        return KittenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KittenViewHolder, position: Int) {
        val model: FavoriteUiModel = getItem(position)
        holder.bindTo(model)
    }
}

class KittenViewHolder(
    private val binding: FavoriteItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(kitten: FavoriteUiModel) {
        binding.model = kitten

        val baseDirectory = binding.root.context.getOutputDirectory()
        val file = File(baseDirectory, "${kitten.id}.jpg")
        Glide.with(binding.image)
            .load(file)
            .placeholder(R.drawable.img_cat_placeholder)
            .into(binding.image)
    }
}