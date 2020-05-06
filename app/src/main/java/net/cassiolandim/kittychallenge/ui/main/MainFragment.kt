package net.cassiolandim.kittychallenge.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_fragment.*
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.MainFragmentBinding
import net.cassiolandim.kittychallenge.di.createMainViewModel
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.network.Status
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel
import java.io.File

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel


    private val baseDirectory : File by lazy {
        requireContext().getOutputDirectory()
    }

    private lateinit var adapter : KittensAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = createMainViewModel().also {
            adapter = KittensAdapter(it.kittenList) { kitten : KittenUiModel ->
                if (kitten.isFavorite) {
                    viewModel.saveFavorite(kitten.id, kitten.url)
                } else {
                    kitten.favoriteId?.let { id ->
                        viewModel.deleteFavorite(id, baseDirectory)
                    }
                }
            }
            it.firstPageSearch()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        (DataBindingUtil.inflate(
            inflater,
            R.layout.main_fragment,
            container,
            false
        ) as MainFragmentBinding).apply {
            lifecycleOwner = this@MainFragment
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
            kittens.observe(viewLifecycleOwner, Observer {
                if (it.isEmpty()) {
                    emptyStateLayout.visibility = View.VISIBLE
                } else {
                    kittenList.addAll(it)
                    adapter.notifyDataSetChanged()
                    emptyStateLayout.visibility = View.GONE
                }
            })
            networkState.observe(viewLifecycleOwner, Observer {
                when(it.status) {
                    Status.RUNNING -> {
                        progressLayout.visibility = View.VISIBLE
                        emptyStateLayout.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        progressLayout.visibility = View.GONE
                        emptyStateLayout.visibility = View.GONE
                    }
                    Status.FAILED -> {
                        emptyStateLayout.visibility = View.VISIBLE
                        progressLayout.visibility = View.GONE
                        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_LONG).show()
                    }
                }
            })
            savedFavorite.observe(viewLifecycleOwner, Observer {
                val indexOf = kittenList.indexOfFirst { kitten ->
                    kitten.id == it.imageId
                }
                if (indexOf >= 0) {
                    with(kittenList[indexOf]) {
                        favoriteId = it.id
                    }
                }
            })
            deletedFavoriteIndex.observe(viewLifecycleOwner, Observer {
                adapter.notifyItemChanged(it)
            })
        }

        tryAgainButton.setOnClickListener {
            viewModel.firstPageSearch()
        }

        initScrollListener()
    }

    private fun initScrollListener() {
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!viewModel.isLoading &&
                        linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() == viewModel.kittenList.size - 1) {
                    viewModel.increasePageAndSearch()
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorites_menu -> {
                val action = MainFragmentDirections.actionMainFragmentToFavoritesFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
