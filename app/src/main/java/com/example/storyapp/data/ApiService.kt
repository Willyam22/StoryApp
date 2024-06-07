package com.example.storyapp.data


import com.example.storyapp.data.Response.AddStoryResponse
import com.example.storyapp.data.Response.DetailStoryResponse
import com.example.storyapp.data.Response.ListStoryItem
import com.example.storyapp.data.Response.LoginResponse
import com.example.storyapp.data.Response.RegisterResponse
import com.example.storyapp.data.Response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email")email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getstories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): List<ListStoryItem>

    @Multipart
    @POST("stories")
    fun uploadStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: String,
    ): Call<DetailStoryResponse>

    @GET("stories")
    fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): Call<StoryResponse>

}