package com.example.submission_awal.ui.upcoming

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
import com.example.submission_awal.databinding.FragmentUpcomingBinding
import com.example.submission_awal.ui.home.HomeViewModel
import com.example.submission_awal.utils.MarginItemDecoration
import com.example.submission_awal.utils.ViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }
    private val activeEventAdapter by lazy {
        ActiveEventReviewAdapter(
            mutableListOf(),
            onItemClick = { selectedEvent ->
                val action = UpcomingFragmentDirections.actionUpcomingFragmentToDetailEventFragment(selectedEvent)
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

            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.getActiveEvents()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


        viewModel.listActiveEvent.observe(viewLifecycleOwner) { activeEvents ->
            activeEvents?.let {
                activeEventAdapter.updateData(it)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvActiveEvent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activeEventAdapter
            addItemDecoration(MarginItemDecoration(32))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}