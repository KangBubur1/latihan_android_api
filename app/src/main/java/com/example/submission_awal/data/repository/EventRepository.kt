package com.example.submission_awal.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.submission_awal.data.local.entity.EventEntity
import com.example.submission_awal.data.local.room.EventDao
import com.example.submission_awal.data.network.ApiService
import com.example.submission_awal.data.response.DicodingEvent
import com.example.submission_awal.utils.AppExecutors
import com.example.submission_awal.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {


    fun updateEvents(): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            fetchFromApi { apiService.getPastEvents() }
            fetchFromApi { apiService.getActiveEvents() }
        }


        val localData = eventDao.getAllEvents()
        result.addSource(localData) { events ->
            if (events.isNotEmpty()) {
                result.value = Result.Success(events)
                Log.d("EventRepository", "Fetched from local data: $events")
            } else {
                result.value = Result.Error("No events available")
            }
        }
        return result
    }




    private fun fetchFromApi(apiCall: suspend () -> DicodingEvent) {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        Log.d("EventRepository", "Fetching from API...")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall()
                Log.d("APIResponse", "Response: $response")

                val eventList = response.listEvents?.map { apiEvent ->
                    val currTime = System.currentTimeMillis()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val beginTimeString = apiEvent?.beginTime ?: ""
                    val beginTime: Long = try {
                        val parsedDate = dateFormat.parse(beginTimeString)
                        parsedDate?.time ?: currTime
                    } catch (e: Exception) {
                        Log.e("EventRepository", "Error parsing beginTime: ${e.message}")
                        currTime
                    }

                        val isEventActive = currTime < beginTime
                        val existingEvent = eventDao.getEventById(apiEvent?.id ?: 0)
                        val isFavorite = existingEvent?.isFavorite ?: false
                        EventEntity(
                            id = apiEvent?.id ?: 0,
                            name = apiEvent?.name ?: "",
                            summary = apiEvent?.summary,
                            mediaCover = apiEvent?.mediaCover,
                            registrants = apiEvent?.registrants,
                            imageLogo = apiEvent?.imageLogo,
                            link = apiEvent?.link,
                            description = apiEvent?.description,
                            ownerName = apiEvent?.ownerName,
                            cityName = apiEvent?.cityName,
                            quota = apiEvent?.quota,
                            beginTime = apiEvent?.beginTime,
                            endTime = apiEvent?.endTime,
                            category = apiEvent?.category,
                            isFavorite = isFavorite,
                            isActive = isEventActive,
                            lastUpdated = System.currentTimeMillis()
                        )
                }

                eventList?.let { events ->
                    for (event in events) {
                        val existingEvent = eventDao.getEventById(event.id)
                        if (existingEvent != null) {
                            if (event.lastUpdated > existingEvent.lastUpdated) {
                                eventDao.updateEvent(event)
                                Log.d("EventRepository", "Updated event: ${event.name} with isActive: ${event.isActive}")
                            }
                        } else {
                            eventDao.insertEvent(event)
                            Log.d("EventRepository", "Inserted new event: ${event.name} with isActive: ${event.isActive}")
                        }
                    }
                    result.postValue(Result.Success(events))

                } ?: run {
                    result.postValue(Result.Error("No events found"))
                }
            } catch (e: Exception) {
                result.postValue(Result.Error(e.message.toString()))
            }
        }
    }

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }
    fun setFavoriteEvent(event: EventEntity, favoriteState: Boolean) {
        appExecutors.diskIO.execute {
            event.isFavorite = favoriteState
            eventDao.updateEvent(event)
        }
    }

    fun getActiveEvents(): LiveData<Result<List<EventEntity>>> {
       val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading

        val localData = eventDao.getActiveEvents()
        result.addSource(localData) { events ->
            result.value = if (events.isNotEmpty()){
                Result.Success(events)
            } else {
                Result.Error("No active events found")
            }
            Log.d("EventRepository", "Fetched active events from database: $events")
        }

        return result
    }

    fun getPastEvents(): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading

        val localData = eventDao.getPastEvents()
        result.addSource(localData) { events ->
            result.value = if (events.isNotEmpty()) {
                Result.Success(events)
            } else {
                Result.Error("No past events found")
            }
            Log.d("EventRepository", "Fetched past events from database: $events")
        }

        return result
    }

    fun getSearchEvents(query: String): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading

        val localData = eventDao.getSearchEvents(query)
        result.addSource(localData) { events ->
            if (events.isNotEmpty()) {
                result.value = Result.Success(events)
                Log.d("EventRepository", "Search Success: ${result.value}")
            } else {
                result.value = Result.Error("No events matching query found")
                Log.d("EventRepository", "Search Error: ${result.value}")
            }

            Log.d("EventRepository", "Fetched search results from database: $events")
        }

        return result
    }

    fun updateEvent(event: EventEntity) {
        appExecutors.diskIO.execute {
            eventDao.updateEvent(event)
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }

}