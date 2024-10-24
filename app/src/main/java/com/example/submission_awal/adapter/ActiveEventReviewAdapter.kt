package com.example.submission_awal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission_awal.R
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.data.response.ListEventsItem
import com.example.submission_awal.databinding.ItemRv1Binding
import com.example.submission_awal.utils.EventDiffCallback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActiveEventReviewAdapter(
    private var responseEvent: MutableList<EventEntity?>,
    private val onItemClick: (EventEntity) -> Unit,
    private val onRegister: (String) -> Unit): RecyclerView.Adapter<ActiveEventReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val binding: ItemRv1Binding): RecyclerView.ViewHolder(binding.root) {
        fun bind(eventsItem: EventEntity, onItemClick: (EventEntity) -> Unit, onRegister: (String) -> Unit) {
            Glide
                .with(binding.root.context)
                .load(eventsItem.mediaCover)
                .into(binding.ivBanner)
            binding.tvTitle.text = eventsItem.name
            binding.tvClock.text = eventsItem.beginTime

            val eventDate = eventsItem.beginTime?.let { parseDate(it) }
            val currDate = Date()

            if (eventDate != null && currDate.after(eventDate)) {
                binding.btnRegister.text = "Expired"
                binding.btnRegister.isEnabled = false
            } else {
                binding.btnRegister.text = "Register"
                binding.btnRegister.isEnabled = true
            }

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

        private fun parseDate(dateString: String): Date? {
            return try {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                format.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemRv1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun getItemCount() = responseEvent.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        responseEvent[position]?.let { event ->
            holder.bind(event, onItemClick, onRegister)



        }
    }

    fun updateData(newEvents: List<EventEntity?>) {
        val result = DiffUtil.calculateDiff(EventDiffCallback(responseEvent, newEvents))
        responseEvent.clear()
        responseEvent.addAll(newEvents)
        result.dispatchUpdatesTo(this)
    }
}