package com.example.storyapp.detailed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.WelcomeActivity
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.Response.DetailStoryResponse
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.datastore.dataStore
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val pref = TokenPreference.getInstance(application.dataStore)
        val detailViewModel = ViewModelProvider(this@DetailActivity, ViewModelFactory(this, "", pref)).get(
            DetailViewModel::class.java
        )

        detailViewModel.getValid().observe(this){
            if(it){
                detailViewModel.getTokenKey().observe(this){
                    val id_story =intent.getStringExtra(EXTRA_ID)
                    val id_story1 = id_story?.split("{")
                    val id_story2 = id_story1?.get(1)?.split("}")
                    val id_story_final = id_story2?.get(0)
                    if (id_story != null) {
                        getDetailStory(it, "$id_story_final")
                        Log.d(TAG, "$id_story_final ")
                    }
                }
            }else{
                val intentWelcome = Intent(this@DetailActivity, WelcomeActivity::class.java)
                startActivity(intentWelcome)
            }
        }



    }

    private fun getDetailStory(token_auth: String, id_story:String){
        showLoading(true)
        val pref = TokenPreference.getInstance(application.dataStore)
        val detailViewModel = ViewModelProvider(this@DetailActivity, ViewModelFactory(this@DetailActivity, "", pref)).get(
            DetailViewModel::class.java
        )
        detailViewModel.getTokenKey().observe(this){
            val client = ApiConfig.getApiService(it).getDetailStory( id_story)
            client.enqueue(object: Callback<DetailStoryResponse>{
                override fun onResponse(
                    call: Call<DetailStoryResponse>,
                    response: Response<DetailStoryResponse>
                ) {
                    if(response.isSuccessful){
                        showLoading(false)
                        val responseBody = response.body()
                        if(responseBody != null){
                            setData("${responseBody.story?.photoUrl}", "${responseBody.story?.name}", "${responseBody.story?.description}")
                        }
                    }else{
                        val errorBody = (response?.errorBody() as ResponseBody).string()
                        showDialog(errorBody)
                    }
                }

                override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                    showDialog("${t.message}")
                }

            })
        }

    }

    private fun setData(imgUrl: String, username:String, desc:String){
        Glide.with(this)
            .load(imgUrl)
            .into(binding.imgDetail)

        binding.txtUsername.text = username
        binding.txtDesc.text = desc
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDialog(message: String){
        val alertdialogBuilder = AlertDialog.Builder(this)
        with(alertdialogBuilder){
            setTitle("ERROR")
            setMessage(message)
            setCancelable(false)
            setNegativeButton("okay"){dialog, _->dialog.cancel()}
        }
        val alertDialog = alertdialogBuilder.create()
        alertDialog.show()
    }

    companion object{
        const val EXTRA_ID = "extra_id"
        const val TAG = "DetailActivity"
    }
}