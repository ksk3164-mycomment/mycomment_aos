package kr.beimsupicures.mycomment.api.loaders

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers.io
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.api.models.WatchModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchService {

    @GET("talk/search")
    fun searchTalk(
        @Header("Authorization") accessToken: String?,
        @Query("keyword") keyword: String
    ): Call<APIResult<MutableList<TalkModel>>>

    @GET("talk/search")
    fun searchWatch(
        @Header("Authorization") accessToken: String?,
        @Query("keyword") keyword: String
    ): Observable<APIResult<MutableList<TalkModel>>>
}

class SearchLoader : BaseLoader<SearchService> {

    companion object {
        val shared = SearchLoader()
    }

    constructor() {
        api = APIClient.create(SearchService::class.java)
    }


    // 키워드 검색 결과
    fun searchTalk(keyword: String, completionHandler: (MutableList<TalkModel>) -> Unit) {
        api.searchTalk(APIClient.accessToken, keyword)
            .enqueue(object : Callback<APIResult<MutableList<TalkModel>>> {
                override fun onFailure(
                    call: Call<APIResult<MutableList<TalkModel>>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<TalkModel>>>,
                    response: Response<APIResult<MutableList<TalkModel>>>
                ) {
                    if (response.isSuccessful) {
                        val newValue = response.body()?.result
                        newValue?.let { newValue ->
                            completionHandler(newValue)
                        }
                    }
                }

            })
    }

}
