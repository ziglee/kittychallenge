package net.cassiolandim.kittychallenge.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import net.cassiolandim.kittychallenge.R
import net.cassiolandim.kittychallenge.databinding.MainFragmentBinding
import net.cassiolandim.kittychallenge.di.createMainViewModel
import timber.log.Timber

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private val adapter = KittensAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = createMainViewModel().also {
            it.search(1)
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

        with(viewModel) {
            kittens.observe(viewLifecycleOwner, Observer {
                Timber.d(it.toString())
                adapter.submitList(it)
            })
        }
    }
}
