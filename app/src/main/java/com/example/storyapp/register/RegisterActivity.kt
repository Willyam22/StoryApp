package com.example.storyapp.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.WelcomeActivity
import com.example.storyapp.WelcomeViewModel
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.Response.RegisterResponse
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.datastore.dataStore
import com.example.storyapp.login.LoginActivity
import com.example.storyapp.story.StoryActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            Registering(name, email, password)
            Log.e(TAG, "dites")
        }

        onBackPressedDispatcher.addCallback(this@RegisterActivity, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val intentWelcome = Intent(this@RegisterActivity, WelcomeActivity::class.java)
                startActivity(intentWelcome)
            }
        })

        val pref = TokenPreference.getInstance(application.dataStore)
        val uploadViewModel = ViewModelProvider(this@RegisterActivity, ViewModelFactory(this, "", pref)).get(
            WelcomeViewModel::class.java
        )

        uploadViewModel.getValid().observe(this){
            val intent = Intent(this, StoryActivity::class.java)
            startActivity(intent)
        }

    }

    private  fun Registering(name: String, email:String, password:String){
        showLoading(true)
        val client = ApiConfig.getApiService("noauth").register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if(response.isSuccessful){
                    showLoading(false)
                    val responseBody =response.body()
                    if(responseBody != null){
                        if(responseBody.error == false){
                            showDialog("${responseBody.message}", true)

                        }
                    }
                }else{
                    val errorBody = (response?.errorBody() as ResponseBody).string()
                    val error1 = errorBody.split("{")
                    val error2 = error1[1].split("}")
                    val error3 = error2[0].split(",")
                    val error4 = error3[1].split(":")
                    val error5 = error4[1].split("\"")
                    val errorfinal = error5[1]
                    showDialog("$errorfinal", false)
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                showDialog("${t.message} ada yang salah dengan api", false)
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDialog(message : String, status : Boolean){
        val alertdialogBuilder = AlertDialog.Builder(this@RegisterActivity)
        with(alertdialogBuilder){
            if(status){
                setTitle("Success")
            }else{
                setTitle("Error")
            }
            setMessage("$message, apakah anda ingin ke halaman login?")
            setCancelable(false)
            setPositiveButton("yes"){_, _->
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton("no"){dialog, _->dialog.cancel()}
        }
        val alertDialog = alertdialogBuilder.create()
        alertDialog.show()
    }

    companion object{
        private const val TAG = "RegisterActivity"
    }
}