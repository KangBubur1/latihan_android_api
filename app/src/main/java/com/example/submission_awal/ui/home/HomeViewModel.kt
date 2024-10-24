package com.example.submission_awal.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.data.repository.EventRepository
import com.example.submission_awal.data.response.DicodingEvent
import com.example.submission_awal.utils.Result
import kotlinx.coroutines.launch

class HomeViewModel(private val eventRepository: EventRepository): ViewModel() {
    private val _events = MediatorLiveData<Result<List<EventEntity>>>()
    val events: LiveData<Result<List<EventEntity>>>  = _events

    private val _listActiveEvent = MutableLiveData<List<EventEntity?>>()
    val listActiveEvent: LiveData<List<EventEntity?>> = _listActiveEvent

    private val _listPastEvent = MutableLiveData<List<EventEntity?>>()
    val listPastEvent: LiveData<List<EventEntity?>> = _listPastEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


        init {
            viewModelScope.launch {
                eventRepository.updateEvents().observeForever { result ->
                    _events.value = result
                }
            }
        }

        fun getActiveEvents() {
            _isLoading.value = true
            viewModelScope.launch {
                eventRepository.getActiveEvents().observeForever { result ->
                    when (result) {
                        is Result.Loading -> {
                            _isLoading.value = true
                        }
                        is Result.Success -> {
                            Log.d("HomeViewModel", "Active Events: ${result.data}")
                            _listActiveEvent.value = result.data
                            _isLoading.value = false
                        }
                        is Result.Error -> {
                            _errorMessage.value = result.error
                            _isLoading.value = false
                        }
                    }
                }
            }
        }


        fun getPastEvents() {
            _isLoading.value = true
            viewModelScope.launch {
                eventRepository.getPastEvents().observeForever { result ->
                    when (result) {
                        is Result.Loading -> {
                            _isLoading.value = true
                        }
                        is Result.Success -> {
                            _listPastEvent.value = result.data
                            _isLoading.value = false
                        }
                        is Result.Error -> {
                            _errorMessage.value = result.error
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
}



