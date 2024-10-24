package com.example.submission_awal.data.network

import com.example.submission_awal.data.response.DicodingEvent
import retrofit2.http.*

interface ApiService {

    @GET("events?active=1")
    suspend fun getActiveEvents(): DicodingEvent

    @GET("events?active=0")
    suspend fun getPastEvents(): DicodingEvent

    @GET("events?active=-1&limit=1")
    suspend fun getUpcomingEvents(): DicodingEvent

}