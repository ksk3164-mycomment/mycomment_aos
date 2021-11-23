package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TMDBSearchModel(
    val page : Int? = null,
    val results: MutableList<TMDBDetailModel>? = null,
    val total_pages : Int? = null,
    val total_results : Int? = null
): Parcelable

@Parcelize
data class TMDBDetailModel(
    val backdrop_path : String? = null,
    val first_air_date: String? = null,
    val genre_ids: MutableList<Int>? = null,
    val id: Int? = null,
    val name: String? = null,
    val origin_country : MutableList<String>? = null,
    val original_language: String? = null,
    val original_name: String? = null,
    val overview: String? = null,
    val popularity: Float? = null,
    val poster_path: String? = null,
    val total_revote_averagesults : Float? = null,
    val vote_count : Int? = null,
): Parcelable