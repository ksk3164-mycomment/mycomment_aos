package kr.beimsupicures.mycomment.api

import android.util.Log
import com.google.gson.JsonElement
import kr.beimsupicures.mycomment.extensions.Constants.TAG
import retrofit2.Call
import retrofit2.Response
import retrofit2.create

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    private val iRetrofit: IRetrofit? = RetrofitClient.getClient("")?.create(IRetrofit::class.java)

    fun searchPhotos(searchTerm: String?, completion: (String) -> Unit) {

        val term = searchTerm ?: ""

        val call = iRetrofit?.searchPhotos(searchTerm = term).let {
            it
        } ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                completion(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }

        })

    }

}