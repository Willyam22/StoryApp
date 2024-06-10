package com.example.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.detailed.DetailViewModel
import com.example.storyapp.di.Injection
import com.example.storyapp.login.LoginViewModel
import com.example.storyapp.maps.MapsViewModel
import com.example.storyapp.register.RegisterViewModel
import com.example.storyapp.story.DataViewModel
import com.example.storyapp.story.StoryViewModel
import com.example.storyapp.upload.UploadViewModel

class ViewModelFactory(private val context: Context, private val auth: String, private val pref : TokenPreference):ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(StoryViewModel::class.java)){
            return StoryViewModel(Injection.provideRepository(context, auth)) as T
        }else if(modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(DataViewModel::class.java)) {
            return DataViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}