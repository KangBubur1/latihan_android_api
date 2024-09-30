package com.example.submission_awal.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.example.submission_awal.databinding.FragmentHomeBinding
import com.example.submission_awal.utils.MarginItemDecoration


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by viewModels()
    private val activeEventAdapter by lazy {
        ActiveEventReviewAdapter(
            mutableListOf(),
            onItemClick = { selectedEvent ->
                val action = HomeFragmentDirections.actionHomeFragmentToDetailEventFragment(selectedEvent)
                findNavController().navigate(action)
            },
            onRegister = { url ->
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

        val itemDecoration = MarginItemDecoration(32)
        binding.rvActiveEvent.addItemDecoration(itemDecoration)

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.listActiveEvent.observe(viewLifecycleOwner) { activeEventList ->
            activeEventList?.let {
                activeEventAdapter.updateData(it)
            }
        }

        homeViewModel.listPastEvent.observe(viewLifecycleOwner) { pastEventList ->
            pastEventList?.let {
                val limitedPastEvents = it.take(5)
                pastEventAdapter.updateData(limitedPastEvents)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
