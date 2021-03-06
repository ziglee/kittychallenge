package net.cassiolandim.kittychallenge.ui.main

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.RowItemBinding
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import java.io.File

class KittensAdapter(
    private val kittenList: List<KittenUiModel>,
    private val toggleFavoriteCallback: (KittenUiModel) -> Unit
) : ListAdapter<KittenUiModel, KittenViewHolder>(DIFF_CALLBACK) {

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
        val binding = RowItemBinding.inflate(layoutInflater, parent,false)
        return KittenViewHolder(binding, toggleFavoriteCallback)
    }

    override fun onBindViewHolder(holder: KittenViewHolder, position: Int) {
        val kitten: KittenUiModel = kittenList[position]
        holder.bindTo(kitten)
    }

    override fun getItemCount() = kittenList.size
}

class KittenViewHolder(
    private val binding: RowItemBinding,
    private val toggleFavoriteCallback: (KittenUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(model: KittenUiModel) {
        binding.model = model

        if (model.favoriteId != null) {
            val baseDirectory = binding.root.context.getOutputDirectory()
            val file = File(baseDirectory, "${model.favoriteId}.jpg")
            if (file.exists()) {
                Glide.with(binding.image)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.img_cat_placeholder)
                    .into(binding.image)
            } else {
                loadImageFromNetwork(model)
            }
        } else {
            loadImageFromNetwork(model)
        }

        binding.favoriteLayout.setOnClickListener {
            model.isFavorite = !model.isFavorite
            toggleFavoriteCallback(model)
            setupFavoriteButton(model.isFavorite)
        }

        setupFavoriteButton(model.isFavorite)
    }

    private fun loadImageFromNetwork(model: KittenUiModel) {
        Glide.with(binding.image)
            .load(model.url)
            .placeholder(R.drawable.img_cat_placeholder)
            .into(binding.image)
    }

    private fun setupFavoriteButton(isFavorite: Boolean) {
        val grey = binding.root.resources.getColor(R.color.grey, null)
        val allTimeNeonRed = binding.root.resources.getColor(R.color.allTimeNeonRed, null)
        if (isFavorite) {
            binding.favoriteIcon.setImageResource(R.drawable.ic_star_black)
            binding.favoriteIcon.imageTintList = ColorStateList.valueOf(allTimeNeonRed)
        } else {
            binding.favoriteIcon.setImageResource(R.drawable.ic_star_outline_black)
            binding.favoriteIcon.imageTintList = ColorStateList.valueOf(grey)
        }
    }
}