package com.example.submission_awal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission_awal.data.response.ListEventsItem
import com.example.submission_awal.databinding.ItemRv1Binding
import com.example.submission_awal.utils.EventDiffCallback

class ActiveEventReviewAdapter(
    private var responseEvent: MutableList<ListEventsItem?>,
    private val onItemClick: (ListEventsItem) -> Unit,
    private val onRegister: (String) -> Unit): RecyclerView.Adapter<ActiveEventReviewAdapter.ReviewViewHolder>() {



    class ReviewViewHolder(private val binding: ItemRv1Binding): RecyclerView.ViewHolder(binding.root) {
        fun bind(eventsItem: ListEventsItem, onItemClick: (ListEventsItem) -> Unit, onRegister: (String) -> Unit) {
            Glide
                .with(binding.root.context)
                .load(eventsItem.mediaCover)
                .into(binding.ivBanner)
            binding.tvTitle.text = eventsItem.name
            binding.tvClock.text = eventsItem.beginTime

            binding.root.setOnClickListener {
                onItemClick(eventsItem)
            }

            binding.btnRegister.setOnClickListener {
                val url = eventsItem.link ?: "Missing Link"
                if (url.isNotEmpty()) {
                    onRegister(url)
                }
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemRv1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun getItemCount() = responseEvent.size


    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        responseEvent[position]?.let { holder.bind(it, onItemClick, onRegister) }
    }

    fun updateData(newEvents: List<ListEventsItem?>) {
        val result = DiffUtil.calculateDiff(EventDiffCallback(responseEvent, newEvents))
        responseEvent.clear()
        responseEvent.addAll(newEvents)
        result.dispatchUpdatesTo(this)
    }


}