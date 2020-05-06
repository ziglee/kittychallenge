package net.cassiolandim.kittychallenge.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.FavoriteItemBinding
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.ui.favorites.model.FavoriteUiModel
import java.io.File

class FavoritesAdapter(
    private val deleteCallback: (String) -> Unit
) : ListAdapter<FavoriteUiModel, KittenViewHolder>(DIFF_CALLBACK) {

    private var currentList : List<FavoriteUiModel>? = null

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
        return KittenViewHolder(binding, deleteCallback)
    }

    override fun onBindViewHolder(holder: KittenViewHolder, position: Int) {
        val model: FavoriteUiModel = getItem(position)
        holder.bindTo(model)
    }

    override fun submitList(list: List<FavoriteUiModel>?) {
        super.submitList(list)
        currentList = list
    }

    fun notifyItemChangedByFavoriteId(favoriteId: String) {
        currentList?.let { list ->
            val indexOf = list.indexOfFirst { it.id == favoriteId }
            if (indexOf >= 0) notifyItemChanged(indexOf)
        }
    }

}

class KittenViewHolder(
    private val binding: FavoriteItemBinding,
    private val deleteCallback: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(model: FavoriteUiModel) {
        binding.model = model

        binding.deleteLayout.setOnClickListener {
            deleteCallback(model.id)
        }

        val baseDirectory = binding.root.context.getOutputDirectory()
        val file = File(baseDirectory, "${model.id}.jpg")
        Glide.with(binding.image)
            .load(file)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.img_cat_placeholder)
            .into(binding.image)
    }
}