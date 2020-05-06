package net.cassiolandim.kittychallenge.ui.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.favorites_fragment.recyclerView
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.FavoritesFragmentBinding
import net.cassiolandim.kittychallenge.di.createFavoritesViewModel

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: FavoritesViewModel
    private val adapter = FavoritesAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = createFavoritesViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        (DataBindingUtil.inflate(
            inflater,
            R.layout.favorites_fragment,
            container,
            false
        ) as FavoritesFragmentBinding).apply {
            lifecycleOwner = this@FavoritesFragment
            viewModel = viewModel
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).also {
            it.orientation = LinearLayoutManager.VERTICAL
        }
        recyclerView.adapter = adapter

        with(viewModel) {
            favorites.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}