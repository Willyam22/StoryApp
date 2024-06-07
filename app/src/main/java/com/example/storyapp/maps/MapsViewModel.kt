package com.example.storyapp.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.Response.ListStoryItem
import com.example.storyapp.data.Response.StoryResponse
import com.example.storyapp.datastore.TokenPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: TokenPreference): ViewModel() {
    private val _StoryList =MutableLiveData<List<ListStoryItem>>()

    val storyList: LiveData<List<ListStoryItem>> get() = _StoryList


    fun setStory(listStory: List<ListStoryItem>){
        viewModelScope.launch {
            _StoryList.value = listStory
        }
    }

    fun getTokenKey(): LiveData<String>{
        return pref.getTokenKey().asLiveData()
    }

    fun getLocation(auth: String){
        val client = ApiConfig.getApiService("$auth").getStoriesWithLocation()
        client.enqueue(object: Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if(response.isSuccessful){
                    val responseBody =response.body()
                    if(responseBody != null){
                        Log.d(MapsActivity.TAG, "${responseBody.listStory}")
                        viewModelScope.launch {
                            _StoryList.value = responseBody.listStory
                        }
                    }else{
                        Log.d(MapsActivity.TAG, "response kosong")
                    }
                }else{
                    Log.d(MapsActivity.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.d(MapsActivity.TAG, "onFailure: ${t.message}")
            }

        })
    }
}