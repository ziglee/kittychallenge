package net.cassiolandim.kittychallenge.ui.favorites

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.favorites_fragment.*
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.FavoritesFragmentBinding
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.ui.MainViewModel

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter : FavoritesAdapter

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val favoriteId = intent.getStringExtra(EXTRA_FAVORITE_ID)!!
            adapter.notifyItemChangedByFavoriteId(favoriteId)
        }
    }

    companion object {
        const val ACTION = "net.cassiolandim.kittychallenge.IMAGE_DOWNLOADED"
        const val EXTRA_FAVORITE_ID = "EXTRA_FAVORITE_ID"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val filter = IntentFilter(ACTION)
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadcastReceiver)
        super.onDestroy()
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
            viewModel = this@FavoritesFragment.viewModel
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).also {
            it.orientation = LinearLayoutManager.VERTICAL
        }

        with(viewModel) {
            adapter = FavoritesAdapter { id ->
                viewModel.deleteFavorite(id, requireContext().getOutputDirectory())
            }
            recyclerView.adapter = adapter

            favorites.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
                if (it.isEmpty()) {
                    emptyStateMessage.visibility = View.VISIBLE
                } else {
                    emptyStateMessage.visibility = View.GONE
                }
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