package com.example.submission_awal.ui.detail

import androidx.lifecycle.ViewModel
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.data.repository.EventRepository

class DetailEventViewModel(private val eventRepository: EventRepository): ViewModel() {


    fun saveEvent(event: EventEntity) {
        eventRepository.setFavoriteEvent(event, true)
    }

    fun deleteEvent(event: EventEntity) {
        eventRepository.setFavoriteEvent(event, false)
    }
}