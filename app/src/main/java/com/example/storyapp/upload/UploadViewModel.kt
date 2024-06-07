package com.example.storyapp.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.datastore.TokenPreference

class UploadViewModel(private val pref: TokenPreference): ViewModel()  {
    fun getTokenKey(): LiveData<String> {
        return pref.getTokenKey().asLiveData()
    }
    fun getValid() : LiveData<Boolean>{
        return pref.getValid().asLiveData()
    }
}