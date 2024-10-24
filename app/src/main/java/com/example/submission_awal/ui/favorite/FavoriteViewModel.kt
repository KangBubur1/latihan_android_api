package com.example.submission_awal.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.data.repository.EventRepository


class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    val favoriteEvents: LiveData<List<EventEntity>> = eventRepository.getFavoriteEvents()


}