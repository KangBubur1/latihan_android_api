package com.example.submission_awal.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.data.repository.EventRepository
import kotlinx.coroutines.launch
import com.example.submission_awal.utils.Result

class SearchViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _searchResults = MutableLiveData<List<EventEntity?>>()
    val searchResults: MutableLiveData<List<EventEntity?>> get() = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    fun searchEvents(query: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val response = eventRepository.getSearchEvents(query)
            response.observeForever { result ->
                when (result) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                    is Result.Success -> {
                        _isLoading.value = false
                        if (result.data.isEmpty()) {
                            _errorMessage.value = "No events matching query found"
                            _searchResults.value = emptyList()
                        } else {
                            _errorMessage.value = null
                            _searchResults.value = result.data
                        }
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = result.error
                    }
                }
            }
        }
    }
}