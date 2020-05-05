package net.cassiolandim.kittychallenge.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_fragment.*
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.MainFragmentBinding
import net.cassiolandim.kittychallenge.di.createMainViewModel
import net.cassiolandim.kittychallenge.getOutputDirectory
import net.cassiolandim.kittychallenge.network.Status
import net.cassiolandim.kittychallenge.ui.main.model.KittenUiModel

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var page = 0
    private val kittenList = mutableListOf<KittenUiModel>()
    private var isLoading = true
    private val adapter = KittensAdapter(kittenList) { kitten : KittenUiModel ->
        if (kitten.isFavorite) {
            viewModel.saveFavorite(kitten.id, kitten.url, requireContext().getOutputDirectory())
        } else {
            kitten.favoriteId?.let {
                viewModel.deleteFavorite(it)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = createMainViewModel().also {
            it.search(page)
        }
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
                kittenList.addAll(it)
                adapter.notifyDataSetChanged()
                isLoading = false
            })
            networkState.observe(viewLifecycleOwner, Observer {
                when(it.status) {
                    Status.RUNNING -> progressLayout.visibility = View.VISIBLE
                    Status.SUCCESS -> progressLayout.visibility = View.GONE
                    Status.FAILED -> {
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
                        isFavorite = true
                        favoriteId = it.id
                    }
                    adapter.notifyItemChanged(indexOf)
                }
            })
        }

        initScrollListener()
    }

    private fun initScrollListener() {
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading &&
                        linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() == kittenList.size - 1) {
                    viewModel.search(++page)
                    isLoading = true
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }
}
