package com.example.submission_awal.di

import android.content.Context
import com.example.submission_awal.data.local.room.EventDatabase
import com.example.submission_awal.data.network.ApiConfig
import com.example.submission_awal.data.repository.EventRepository
import com.example.submission_awal.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, dao, appExecutors)
    }
}