package kr.beimsupicures.mycomment.api

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    @GET("users/list")
    fun searchPhotos(@Query("query") searchTerm : String) : Call<JsonElement>

    fun searchUsers()

}