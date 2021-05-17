package com.chansax.places.features.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import com.chansax.places.R
import com.chansax.places.databinding.FragmentListBinding
import com.chansax.places.features.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var binding: FragmentListBinding
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            recyclerView.apply {
                searchAdapter = SearchAdapter(object: SearchAdapter.FavoriteCallback {
                    override fun updateFavorite(id: String, isFav: Boolean) {
                        viewModel.updateFavorite(id, isFav)
                    }

                    override fun isFavorite(placeId: String): Boolean? {
                        return viewModel.isFavorite(placeId)
                    }
                })
                adapter = searchAdapter
            }

            buttonMap.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.action_list_to_map);
            }
        }

        viewModel.location.observe(viewLifecycleOwner) {
            handleLocation()
        }

        viewModel.business.observe(viewLifecycleOwner) {
            displayResults()
        }
    }

    private fun handleLocation() {
        viewModel.location.value?.let {
            when (viewModel.business.value) {
                null -> viewModel.search()
                else -> displayResults()
            }
        }
    }

    private fun displayResults() {
        viewModel.business.value?.let {
            searchAdapter.submitList(it)
        }
    }
}
