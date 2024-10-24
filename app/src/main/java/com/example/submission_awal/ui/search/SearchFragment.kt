package com.example.submission_awal.ui.search

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission_awal.adapter.SearchEventsAdapter
import com.example.submission_awal.databinding.FragmentSearchBinding
import com.example.submission_awal.utils.ViewModelFactory

class SearchFragment : Fragment() {


    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchEventsAdapter by lazy {
        SearchEventsAdapter(mutableListOf(),
            onItemClick = { selectedItem ->
                val action = SearchFragmentDirections.actionSearchFragmentToDetailEventFragment(selectedItem)
                findNavController().navigate(action)
            },
            onRegister = { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        )
    }

    private val viewModel: SearchViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerView()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { events ->
            Log.d("SearchFragment", "Received events: $events")
            searchEventsAdapter.updateList(events)
            if (events.isEmpty() && viewModel.errorMessage.value == null) {
                searchEventsAdapter.updateList(emptyList())
                binding.noEventsFoundTextView.visibility = View.VISIBLE
            } else {
                binding.noEventsFoundTextView.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.noEventsFoundTextView.visibility = View.VISIBLE
                binding.noEventsFoundTextView.text = errorMessage
            } else {
                binding.noEventsFoundTextView.visibility = View.GONE
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = searchView.text.toString().trim()
                if(query.isNotEmpty()){
                    viewModel.searchEvents(query)
                    searchBar.setText(query)
                    searchView.hide()
                }
                false
            }
        }


    }

    private fun setupRecyclerView() {
        binding.rvSearch.layoutManager = LinearLayoutManager(context)
        binding.rvSearch.adapter = searchEventsAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}