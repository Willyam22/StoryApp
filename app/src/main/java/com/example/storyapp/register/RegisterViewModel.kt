package com.example.storyapp.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.datastore.TokenPreference

class RegisterViewModel(private val pref:TokenPreference):ViewModel() {


    fun getValid() : LiveData<Boolean>{
        return pref.getValid().asLiveData()
    }
}
