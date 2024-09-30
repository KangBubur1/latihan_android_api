package com.example.submission_awal.ui.finished

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.submission_awal.adapter.PastEventReviewAdapter
import com.example.submission_awal.databinding.FragmentFinishedBinding
import com.example.submission_awal.ui.home.HomeViewModel

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val pastEventAdapter by lazy {
        PastEventReviewAdapter(mutableListOf()) { selectedEvent ->
            val action = FinishedFragmentDirections.actionFinishedFragmentToDetailEventFragment(selectedEvent)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.getPastEvents()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


        viewModel.listPastEvent.observe(viewLifecycleOwner) { pastEventList ->
            pastEventList?.let {
                pastEventAdapter.updateData(it)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvPastEvent.apply {
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layoutManager = staggeredGridLayoutManager
            adapter = pastEventAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}