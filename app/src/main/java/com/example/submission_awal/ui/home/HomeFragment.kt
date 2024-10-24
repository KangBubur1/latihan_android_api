package com.example.submission_awal.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission_awal.adapter.ActiveEventReviewAdapter
import com.example.submission_awal.adapter.PastEventReviewAdapter
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.databinding.FragmentHomeBinding
import com.example.submission_awal.utils.MarginItemDecoration
import com.example.submission_awal.utils.ViewModelFactory


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val activeEventAdapter by lazy {
        ActiveEventReviewAdapter(
            mutableListOf(),
            onItemClick = { selectedEvent ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToDetailEventFragment(selectedEvent)
                findNavController().navigate(action)
            },
            onRegister = { url -> handleRegisterClick(url) },
        )
    }

    private val pastEventAdapter by lazy {
        PastEventReviewAdapter(mutableListOf()) { selectedEvent ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailEventFragment(selectedEvent)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchEvents()

        observeViewModel()
    }

    private fun observeViewModel() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvActiveEvent.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.rvPastEvent.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        homeViewModel.listActiveEvent.observe(viewLifecycleOwner) { activeEventList ->
            handleActiveEventList(activeEventList)
        }

        homeViewModel.listPastEvent.observe(viewLifecycleOwner) { pastEventList ->
            handlePastEventList(pastEventList)
        }

    }

    private fun handleActiveEventList(activeEventList: List<EventEntity?>) {
        Log.d("HomeFragment", "Received Active Events: $activeEventList")
        if (activeEventList.isEmpty()) {
            binding.rvActiveEvent.visibility = View.GONE
            Log.d("ActiveEvent", "No active events found")
        } else {
            binding.rvActiveEvent.visibility = View.VISIBLE
            activeEventAdapter.updateData(activeEventList)
        }
    }

    private fun handlePastEventList(pastEventList: List<EventEntity?>) {
        pastEventList.let {
            val limitedPastEvents = it.take(5)
            pastEventAdapter.updateData(limitedPastEvents)
        }
    }

    private fun fetchEvents() {
        homeViewModel.getActiveEvents()
        homeViewModel.getPastEvents()
    }

    private fun setupRecyclerView() {
        binding.rvActiveEvent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activeEventAdapter
            addItemDecoration(MarginItemDecoration(32))
        }

        binding.rvPastEvent.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = pastEventAdapter
        }
    }

    private fun handleRegisterClick(url: String) {
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No application to handle this request", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
