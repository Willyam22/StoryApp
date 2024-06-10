package com.example.storyapp.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.LoadingStateAdapter
import com.example.storyapp.R
import com.example.storyapp.StoryAdapter
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.WelcomeActivity
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.Response.ListStoryItem
import com.example.storyapp.data.Response.StoryResponse
import com.example.storyapp.databinding.ActivityStoryBinding
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.datastore.dataStore
import com.example.storyapp.maps.MapsActivity
import com.example.storyapp.upload.UploadActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = TokenPreference.getInstance(application.dataStore)

        val dataViewModel = ViewModelProvider(this@StoryActivity, ViewModelFactory(this, "", pref)).get(
            DataViewModel::class.java
        )

        dataViewModel.getTokenKey().observe(this){
            Log.d(TAG, it)
        }

        dataViewModel.getValid().observe(this){isValid->
            if(isValid){
                dataViewModel.getTokenKey().observe(this){
                    Log.d(TAG, it)
//                    getStories(it)
                }
            }else{
                val intentWelcome = Intent(this@StoryActivity, WelcomeActivity::class.java)
                startActivity(intentWelcome)
            }
        }

        binding.fabLogout.setOnClickListener {
            dataViewModel.setValid(false)
            dataViewModel.setTokenKey("")
            val intentWelcome = Intent(this@StoryActivity, WelcomeActivity::class.java)
            startActivity(intentWelcome)
        }



        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menu1->{
                    val intentMap = Intent(this, MapsActivity::class.java)
                    startActivity(intentMap)
                    true
                }
                else-> false
            }
        }



        binding.fabAdd.setOnClickListener {
            val intentUpload = Intent(this, UploadActivity::class.java)
            startActivity(intentUpload)
        }

        onBackPressedDispatcher.addCallback(this@StoryActivity, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(startMain)
            }
        })

        binding.rvStory.layoutManager = LinearLayoutManager(this)

        getData()

    }





//    private fun getStories(auth: String){
//        showLoading(true)
//        val pref = TokenPreference.getInstance(application.dataStore)
//        val storyViewModel = ViewModelProvider(this@StoryActivity, ViewModelFactory(pref)).get(
//            StoryViewModel::class.java
//        )
//
//        storyViewModel.getTokenKey().observe(this){
//            val client =ApiConfig.getApiService(it).getstories()
//            client.enqueue(object: Callback<StoryResponse>{
//                override fun onResponse(
//                    call: Call<StoryResponse>,
//                    response: Response<StoryResponse>
//                ) {
//                    if(response.isSuccessful){
//                        showLoading(false)
//                        val responseBody =response.body()
//                        if(responseBody != null){
//                            Log.d(TAG, "${responseBody.listStory}")
//                            setStoryData(responseBody.listStory)
//                        }else{
//                            Log.d(TAG, "response kosong")
//                        }
//                    }else{
//                        showLoading(false)
//                        Log.d(TAG, "onFailure: ${response.message()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                    showLoading(false)
//                    Log.d(TAG, "onFailure: ${t.message}")
//                }
//
//            })
//        }
//
//    }

//    private fun setStoryData(Storylist: List<ListStoryItem>){
//        binding.rvStory.layoutManager = LinearLayoutManager(this)
//        val adapter =StoryAdapter()
//        adapter.submitList(Storylist)
//        binding.rvStory.adapter = adapter
//    }

    private fun getData(){
        val pref = TokenPreference.getInstance(application.dataStore)
        val dataViewModel = ViewModelProvider(this, ViewModelFactory(this@StoryActivity, "", pref)).get(
            DataViewModel::class.java
        )

        dataViewModel.getTokenKey().observe(this@StoryActivity){
            val storyViewModel = ViewModelProvider(this@StoryActivity, ViewModelFactory(this@StoryActivity, it, pref)).get(
                StoryViewModel::class.java
            )


            val adapter = StoryAdapter()
            binding.rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            storyViewModel.stories.observe(this) {listStory->
                Log.d(TAG, "paging data: ${listStory.toString()}")
                adapter.submitData(lifecycle, listStory )
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object{
        const val TAG = "StoryActivity"
    }
}


