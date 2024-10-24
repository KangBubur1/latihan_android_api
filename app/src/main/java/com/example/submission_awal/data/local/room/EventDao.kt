package com.example.submission_awal.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.submission_awal.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: EventEntity)

    @Update
    fun updateEvent(event: EventEntity)

    @Query("SELECT * FROM event WHERE isActive = 1")
    fun getActiveEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isActive = 0 ORDER BY id DESC")
    fun getPastEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE id = :eventId")
    fun getEventById(eventId: Int): EventEntity?

    @Query("SELECT * FROM event ORDER BY id DESC")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE name LIKE '%' || :query || '%'")
    fun getSearchEvents(query: String): LiveData<List<EventEntity>>

    @Query("DELETE FROM event")
    fun deleteAll()

    @Query("SELECT * FROM event WHERE isFavorite = 1")
     fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT * FROM event WHERE id = :id AND isFavorite = 1)")
    fun isEventFavorite(id: Int): Boolean

}