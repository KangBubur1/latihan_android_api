package com.example.submission_awal.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.data.response.ListEventsItem

class EventDiffCallback(
    private val oldList: List<EventEntity?>,
    private val newList: List<EventEntity?>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]?.id == newList[newItemPosition]?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}