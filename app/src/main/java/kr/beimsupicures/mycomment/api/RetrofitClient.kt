package kr.beimsupicures.mycomment.api

import android.util.Log
import kr.beimsupicures.mycomment.extensions.Constants.TAG
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofitClient : Retrofit? = null

    fun getClient(baseUrl : String): Retrofit? {
        Log.d(TAG, "getClient: ")

        if (retrofitClient == null){


            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitClient
    }
}


