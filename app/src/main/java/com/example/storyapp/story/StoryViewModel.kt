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

class StoryViewModel (private val storyRepository: StoryRepository): ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    var stories: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)


}