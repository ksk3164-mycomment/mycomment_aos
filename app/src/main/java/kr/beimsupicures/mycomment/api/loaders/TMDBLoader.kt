package kr.beimsupicures.mycomment.api.loaders

import android.util.Log
import kr.beimsupicures.mycomment.api.APIClientTMDB
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.TMDBCreditModel
import kr.beimsupicures.mycomment.api.models.TMDBSearchModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {

    @GET("search/tv")
    fun getSearch(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("query") query: String,
    ): Call<TMDBSearchModel>

    @GET("tv/{id}/credits")
    fun getCredit(
        @Path("id") id: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String
    ): Call<TMDBCreditModel>
}

class TMDBLoader : BaseLoader<TMDBService> {

    companion object {
        val shared = TMDBLoader()
    }

    constructor() {
        api = APIClientTMDB.create(TMDBService::class.java)
    }

    fun getSearch(
        api_key: String,
        language: String,
        query: String,
        completionHandler: (response: TMDBSearchModel) -> Unit
    ) {
        api.getSearch(api_key, language, query)
            .enqueue(object : Callback<TMDBSearchModel> {
                override fun onResponse(
                    call: Call<TMDBSearchModel>,
                    response: Response<TMDBSearchModel>
                ) {

                    if (response.isSuccessful) {
                        response.body()?.let { completionHandler(it) }
                    }
                }
                override fun onFailure(call: Call<TMDBSearchModel>, t: Throwable) {
                    Log.e("TAG", "throwable: ${t.message}")
                }

            })
    }

    fun getCredit(
        id: Int,
        api_key: String,
        language: String,
        completionHandler: (response: TMDBCreditModel) -> Unit
    ) {
        api.getCredit(id, api_key, language)
            .enqueue(object : Callback<TMDBCreditModel> {
                override fun onResponse(
                    call: Call<TMDBCreditModel>,
                    response: Response<TMDBCreditModel>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { completionHandler(it) }
                    }
                }
                override fun onFailure(call: Call<TMDBCreditModel>, t: Throwable) {
                    Log.e("TAG", "throwable: ${t.message}")
                }
            })
    }

}
