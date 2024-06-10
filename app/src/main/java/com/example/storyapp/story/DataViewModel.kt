package com.example.storyapp.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.Response.ListStoryItem
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.datastore.TokenPreference
import kotlinx.coroutines.launch

class DataViewModel( private val pref: TokenPreference): ViewModel() {

    fun getTokenKey(): LiveData<String> {
        return pref.getTokenKey().asLiveData()
    }

    fun setTokenKey(token_auth:String){
        viewModelScope.launch {
            pref.setTokenKey(token_auth)
        }
    }

    fun getValid() : LiveData<Boolean>{
        return pref.getValid().asLiveData()
    }

    fun setValid(valid:Boolean){
        viewModelScope.launch {
            pref.setValid(valid)
        }
    }


}