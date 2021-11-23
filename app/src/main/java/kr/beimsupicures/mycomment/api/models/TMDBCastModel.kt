package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TMDBCreditModel(
    val cast: MutableList<TMDBCastModel>? = null,
    val crew: MutableList<TMDBCrewModel>? = null,
    val id: Int? = null
) : Parcelable

@Parcelize
data class TMDBCastModel(
    val adult: Boolean? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val known_for_department: String? = null,
    val name: String? = null,
    val original_name: String? = null,
    val popularity: Float? = null,
    val profile_path: String? = null,
    val character: String? = null,
    val credit_id: String? = null,
    val order: Int? = null,
) : Parcelable

@Parcelize
data class TMDBCrewModel(
    val adult: Boolean? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val known_for_department: String? = null,
    val name: String? = null,
    val original_name: String? = null,
    val popularity: Float? = null,
    val profile_path: String? = null,
    val department: String? = null,
    val job: String? = null,
) : Parcelable
