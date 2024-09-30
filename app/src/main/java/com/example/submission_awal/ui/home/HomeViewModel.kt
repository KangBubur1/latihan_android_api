package com.example.submission_awal.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission_awal.data.network.ApiConfig
import com.example.submission_awal.data.response.ListEventsItem
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val _listActiveEvent = MutableLiveData<List<ListEventsItem?>>()
    val listActiveEvent: LiveData<List<ListEventsItem?>> = _listActiveEvent

    private val _listPastEvent = MutableLiveData<List<ListEventsItem?>>()
    val listPastEvent: LiveData<List<ListEventsItem?>> = _listPastEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getActiveEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getActiveEvents()

                response.listEvents?.let { list ->
                    _isLoading.value = false
                    _listActiveEvent.value = list
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Event tidak ditemukan"
                }

        }
    }

    fun getPastEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getPastEvents()
                response.listEvents?.let { list ->
                    _isLoading.value = false
                    _listPastEvent.value = list
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Event tidak ditemukan"
            }
        }
    }

}

