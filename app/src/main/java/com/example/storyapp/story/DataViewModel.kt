package com.example.storyapp.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.datastore.TokenPreference

class DataViewModel(private val pref: TokenPreference): ViewModel() {
    fun getTokenKey(): LiveData<String> {
        return pref.getTokenKey().asLiveData()
    }
}