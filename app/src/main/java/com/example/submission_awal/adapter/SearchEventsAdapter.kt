package com.example.submission_awal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission_awal.data.response.ListEventsItem
import com.example.submission_awal.databinding.ItemRv1Binding
import com.example.submission_awal.utils.EventDiffCallback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchEventsAdapter(
    private val searchEvents: MutableList<ListEventsItem?>,
    private val onItemClick: (ListEventsItem) -> Unit,
    private val onRegister: (String) -> Unit
) : RecyclerView.Adapter<SearchEventsAdapter.SearchEventsViewHolder>() {

    class SearchEventsViewHolder(private val binding: ItemRv1Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(eventsItem: ListEventsItem, onItemClick: (ListEventsItem) -> Unit, onRegister: (String) -> Unit) {
            Glide.with(binding.root.context)
                .load(eventsItem.mediaCover)
                .into(binding.ivBanner)

            binding.tvTitle.text = eventsItem.name
            binding.tvClock.text = eventsItem.beginTime

            updateRegisterButtonState(eventsItem)

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

        private fun updateRegisterButtonState(eventsItem: ListEventsItem) {
            val eventDate = parseDate(eventsItem.beginTime)
            val currDate = Date()
            binding.btnRegister.apply {
                if (eventDate != null && currDate.after(eventDate)) {
                    text = "Expired"
                    isEnabled = false
                } else {
                    text = "Register"
                    isEnabled = true
                }
            }
        }

        private fun parseDate(dateString: String?): Date? {
            return try {
                if (dateString.isNullOrEmpty()) return null
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                format.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchEventsViewHolder {
        val binding = ItemRv1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchEventsViewHolder(binding)
    }

    override fun getItemCount() = searchEvents.size

    override fun onBindViewHolder(holder: SearchEventsViewHolder, position: Int) {
        searchEvents[position]?.let { holder.bind(it, onItemClick, onRegister) }
    }

    fun updateList(newList: List<ListEventsItem?>) {
        val result = DiffUtil.calculateDiff(EventDiffCallback(searchEvents, newList))
        searchEvents.clear()
        searchEvents.addAll(newList)
        result.dispatchUpdatesTo(this)
    }
}
