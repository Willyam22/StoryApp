package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.StoryDatabase
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.datastore.TokenPreference

object Injection {
    fun provideRepository(context: Context, auth: String): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(auth)
        return StoryRepository(database, apiService)
    }
}