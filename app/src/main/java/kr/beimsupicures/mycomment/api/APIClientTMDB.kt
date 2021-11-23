package kr.beimsupicures.mycomment.api

import android.os.Build
import kr.beimsupicures.mycomment.BuildConfig
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getAccessToken
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClientTMDB {
    companion object {
                val baseURL = "https://api.themoviedb.org/3/"


        private fun createOkHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
            builder.addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .header("User-Agent", "${Build.MODEL}/${BuildConfig.VERSION_NAME}").build()
                )
            }
            return builder.build()
        }

        var retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        fun <T> create(service: Class<T>): T {
            return retrofit.create(service)
        }

    }
}
