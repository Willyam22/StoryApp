package com.example.storyapp.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.WelcomeActivity
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.Response.AddStoryResponse
import com.example.storyapp.databinding.ActivityUploadBinding
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.datastore.dataStore
import com.example.storyapp.getImageUri
import com.example.storyapp.login.LoginActivity
import com.example.storyapp.reduceFileImage
import com.example.storyapp.story.StoryActivity
import com.example.storyapp.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class UploadActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUploadBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnUpload.setOnClickListener {
            val desc = binding.edtDesc.text.toString()
            val pref = TokenPreference.getInstance(application.dataStore)
            val uploadViewModel = ViewModelProvider(this@UploadActivity, ViewModelFactory(this, "", pref)).get(
                UploadViewModel::class.java
            )

            uploadViewModel.getValid().observe(this){isValid->
                if(isValid){
                    uploadViewModel.getTokenKey().observe(this){
                        uploadImage(it, desc)
                    }
                }else{
                    val intentWelcome = Intent(this@UploadActivity, WelcomeActivity::class.java)
                    startActivity(intentWelcome)
                }
            }


        }

        onBackPressedDispatcher.addCallback(this@UploadActivity, object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val intentStory = Intent(this@UploadActivity, StoryActivity::class.java)
                startActivity(intentStory)
            }
        })

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadImage(token_auth:String, desc:String){
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = desc
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val pref = TokenPreference.getInstance(application.dataStore)
            val uploadViewModel = ViewModelProvider(this@UploadActivity, ViewModelFactory(this, "this", pref)).get(
                UploadViewModel::class.java
            )

            uploadViewModel.getTokenKey().observe(this){
                val client = ApiConfig.getApiService(it).uploadStories( multipartBody, requestBody)
                client.enqueue(object: Callback<AddStoryResponse>{
                    override fun onResponse(
                        call: Call<AddStoryResponse>,
                        response: Response<AddStoryResponse>
                    ) {
                        if(response.isSuccessful){
                            showLoading(false)
                            val response = response.body()
                            if(response != null){
                                showDialog("{${response.message}}", true)
                            }
                        }else{
                            showLoading(false)
                            val errorBody = (response?.errorBody() as ResponseBody).string()
                            showDialog(errorBody, false)
                        }
                    }
                    override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                        showLoading(false)
                        showDialog("{${t.message}}", false)
                    }

                })
            }



        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imgResultPhoto.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDialog(message : String, status : Boolean){
        val alertdialogBuilder = AlertDialog.Builder(this@UploadActivity)
        with(alertdialogBuilder){
            if(status){
                setTitle("Success")
                setMessage("$message")
                setCancelable(false)
                setPositiveButton("okay"){_, _->
                    val intent = Intent(this@UploadActivity, StoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }else{
                setTitle("Error")
                setMessage("$message")
                setCancelable(false)
                setNegativeButton("okay"){dialog, _->dialog.cancel()}
            }

        }
        val alertDialog = alertdialogBuilder.create()
        alertDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}