package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivityWelcomeBinding
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.datastore.dataStore
import com.example.storyapp.login.LoginActivity
import com.example.storyapp.register.RegisterActivity
import com.example.storyapp.story.StoryActivity
import com.example.storyapp.upload.UploadViewModel

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = TokenPreference.getInstance(application.dataStore)
        val uploadViewModel = ViewModelProvider(this@WelcomeActivity, ViewModelFactory(this, "", pref)).get(
            WelcomeViewModel::class.java
        )

        uploadViewModel.getValid().observe(this){
            if(it){
                val intent = Intent(this, StoryActivity::class.java)
                startActivity(intent)
            }

        }

        binding.btnLogin.setOnClickListener {
            intentClass("login")
        }

        binding.btnRegister.setOnClickListener {
            intentClass("register")
        }

        onBackPressedDispatcher.addCallback(this@WelcomeActivity, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(startMain)
            }
        })
    }

    private fun intentClass(name:String){
        lateinit var intent:Intent
        when(name){
            "login" ->{
                intent = Intent(this, LoginActivity::class.java)
            }
            "register"-> {
                intent = Intent(this, RegisterActivity::class.java)
            }
        }
        startActivity(intent)
    }

    companion object{
        const val TAG = "WelcomeActivity"
    }
}