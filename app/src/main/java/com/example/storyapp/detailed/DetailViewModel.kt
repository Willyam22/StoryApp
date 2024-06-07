package com.example.storyapp.detailed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.datastore.TokenPreference

class DetailViewModel(private val pref: TokenPreference): ViewModel() {
    fun getTokenKey(): LiveData<String> {
        return pref.getTokenKey().asLiveData()
    }

    fun getValid() : LiveData<Boolean>{
        return pref.getValid().asLiveData()
    }

}