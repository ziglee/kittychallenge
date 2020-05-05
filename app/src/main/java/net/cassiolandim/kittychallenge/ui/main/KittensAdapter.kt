package net.cassiolandim.kittychallenge.ui.main

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.RowItemBinding
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel

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

    fun bindTo(kitten: KittenUiModel) {
        binding.model = kitten
        binding.image

        Glide.with(binding.image)
            .load(kitten.url)
            .into(binding.image)

        binding.favoriteLayout.setOnClickListener {
            kitten.isFavorite = !kitten.isFavorite
            toggleFavoriteCallback(kitten)
            setupFavoriteButton(kitten.isFavorite)
        }

        setupFavoriteButton(kitten.isFavorite)
    }

    private fun setupFavoriteButton(isFavorite: Boolean) {
        val black = binding.root.resources.getColor(R.color.black, null)
        val allTimeNeonRed = binding.root.resources.getColor(R.color.allTimeNeonRed, null)
        if (isFavorite) {
            binding.favoriteIcon.setImageResource(R.drawable.ic_star_black)
            binding.favoriteIcon.imageTintList = ColorStateList.valueOf(allTimeNeonRed)
        } else {
            binding.favoriteIcon.setImageResource(R.drawable.ic_star_outline_black)
            binding.favoriteIcon.imageTintList = ColorStateList.valueOf(black)
        }
    }
}