package com.example.storyapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.datastore.TokenPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref:TokenPreference):ViewModel() {
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