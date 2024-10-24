package com.example.submission_awal.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "event")
data class EventEntity (
    @PrimaryKey
    val id: Int,
    val name: String?,
    val summary: String?,
    val mediaCover: String?,
    val registrants: Int?,
    val imageLogo: String?,
    val link: String?,
    val description: String?,
    val ownerName: String?,
    val cityName: String?,
    val quota: Int?,
    val beginTime: String?,
    val endTime: String?,
    val category: String?,

    @ColumnInfo(name ="isFavorite")
    var isFavorite: Boolean = false,

    @ColumnInfo(name ="isActive")
    val isActive: Boolean,

    @ColumnInfo(name ="lastUpdated")
    val lastUpdated: Long = System.currentTimeMillis()
) : Parcelable