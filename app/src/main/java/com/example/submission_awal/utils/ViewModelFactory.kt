package com.example.submission_awal.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submission_awal.data.repository.EventRepository
import com.example.submission_awal.di.Injection
import com.example.submission_awal.ui.detail.DetailEventViewModel
import com.example.submission_awal.ui.favorite.FavoriteViewModel
import com.example.submission_awal.ui.home.HomeViewModel
import com.example.submission_awal.ui.search.SearchViewModel

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val creators: Map<Class<out ViewModel>, () -> ViewModel>
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]
            ?: creators.asSequence().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        return creator() as T
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: createFactory(context).also { instance = it }
            }
        }

        private fun createFactory(context: Context): ViewModelFactory {
            val eventRepository = Injection.provideRepository(context)
            val creators: Map<Class<out ViewModel>, () -> ViewModel> = mapOf(
                HomeViewModel::class.java to { HomeViewModel(eventRepository) },
                SearchViewModel::class.java to { SearchViewModel(eventRepository)},
                FavoriteViewModel::class.java to { FavoriteViewModel(eventRepository)},
                DetailEventViewModel::class.java to { DetailEventViewModel(eventRepository) },
            )
            return ViewModelFactory(eventRepository, creators)
        }
    }
}