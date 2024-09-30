package com.example.submission_awal.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission_awal.data.network.ApiConfig
import com.example.submission_awal.data.response.DicodingEvent
import com.example.submission_awal.data.response.ListEventsItem
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<List<ListEventsItem?>>()
    val searchResults: MutableLiveData<List<ListEventsItem?>> get() = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun searchEvents(query: String) {
        viewModelScope.launch {
            try {
                val response : DicodingEvent = ApiConfig.getApiService().searchEvents(query)

                response.listEvents?.let {
                    _searchResults.postValue(it)
                } ?: run {
                    _searchResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.postValue(emptyList())
            }
        }
    }
}