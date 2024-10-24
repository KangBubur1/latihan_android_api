package com.example.submission_awal.ui.favorite

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission_awal.adapter.ActiveEventReviewAdapter
import com.example.submission_awal.databinding.FragmentFavoriteBinding
import com.example.submission_awal.ui.upcoming.UpcomingFragmentDirections
import com.example.submission_awal.utils.MarginItemDecoration
import com.example.submission_awal.utils.ViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val activeEventAdapter by lazy {
        ActiveEventReviewAdapter(
            mutableListOf(),
            onItemClick = { selectedEvent ->
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToDetailEventFragment(selectedEvent)
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



    private val viewModel: FavoriteViewModel by viewModels{
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            binding.progressBar.visibility = View.GONE
            if ( events.isNotEmpty()) {
                activeEventAdapter.updateData(events)
            } else{
                activeEventAdapter.updateData(emptyList())
                binding.noEventsFoundTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvFavEvent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activeEventAdapter
            addItemDecoration(MarginItemDecoration(32))
        }
    }
}