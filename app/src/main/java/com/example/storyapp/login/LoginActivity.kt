package com.example.storyapp.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.WelcomeActivity
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.Response.LoginResponse
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.datastore.dataStore
import com.example.storyapp.story.StoryActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = TokenPreference.getInstance(application.dataStore)
        val loginViewModel =ViewModelProvider(this@LoginActivity, ViewModelFactory(this, "", pref)).get(
            LoginViewModel::class.java
        )


        loginViewModel.getValid().observe(this){
            if(it){
                val intent = Intent(this, StoryActivity::class.java)
                startActivity(intent)
            }
        }

        playAnimation()

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()



            login(email,password)
        }

        onBackPressedDispatcher.addCallback(this@LoginActivity, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val intentWelcome = Intent(this@LoginActivity, WelcomeActivity::class.java)
                startActivity(intentWelcome)

            }
        })
    }

    private fun login(email: String, password: String){
        showLoading(true)


        val client = ApiConfig.getApiService("noauth").login(email, password)
        client.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>)
            {
                if(response.isSuccessful){
                    showLoading(false)
                    val responseBody = response.body()
                    if(responseBody != null){
                        val intentStory = Intent(this@LoginActivity, StoryActivity::class.java)
                        val pref = TokenPreference.getInstance(application.dataStore)
                        val loginViewModel =ViewModelProvider(this@LoginActivity, ViewModelFactory(this@LoginActivity, "", pref)).get(
                            LoginViewModel::class.java
                        )
                        loginViewModel.setValid(true)
                        loginViewModel.setTokenKey("${responseBody.loginResult.token}")
                        startActivity(intentStory)
                    }else{
                        showDialog("there is something error on the response")
                    }
                }else{
                    showLoading(false)
                    val errorBody = (response?.errorBody() as ResponseBody).string()
                    val error1 = errorBody.split("{")
                    val error2 = error1[1].split("}")
                    val error3 = error2[0].split(",")
                    val error4 = error3[1].split(":")
                    val error5 = error4[1].split("\"")
                    val errorfinal = error5[1]
                    showDialog("$errorfinal")
                    Log.e(TAG, "onFailure1: $errorfinal")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                showDialog("${t.message}")
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }



    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.loginDi, View.TRANSLATION_X, -40f, 40f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE

        }.start()

        val email = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(100)
        val password = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(email, password)
            start()
        }
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
        private const val TAG = "LoginActivity"
    }
}