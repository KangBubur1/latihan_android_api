package com.example.submission_awal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.databinding.ItemRv2Binding
import com.example.submission_awal.utils.EventDiffCallback

class PastEventReviewAdapter(
    private var responseEvent: MutableList<EventEntity?>,
    private val onItemClick: (EventEntity) -> Unit): RecyclerView.Adapter<PastEventReviewAdapter.ReviewViewHolder>() {



    class ReviewViewHolder(private val binding: ItemRv2Binding): RecyclerView.ViewHolder(binding.root) {
        fun bind(eventsItem: EventEntity, onItemClick: (EventEntity) -> Unit) {
            Glide
                .with(binding.root.context)
                .load(eventsItem.mediaCover)
                .into(binding.ivBanner)
            binding.tvTitle.text = eventsItem.name
            binding.root.setOnClickListener {
                onItemClick(eventsItem)
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemRv2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun getItemCount() = responseEvent.size


    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        responseEvent[position]?.let { holder.bind(it, onItemClick) }
    }

    fun updateData(newEvents: List<EventEntity?>) {
        val result = DiffUtil.calculateDiff(EventDiffCallback(responseEvent, newEvents))
        responseEvent.clear()
        responseEvent.addAll(newEvents)
        result.dispatchUpdatesTo(this)

    }


}