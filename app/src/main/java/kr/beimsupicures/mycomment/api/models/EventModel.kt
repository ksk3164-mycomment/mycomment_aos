package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventModel(
    val banner: String? = null,
    val link: String? = null,
    val live: Int? = null,
    val seq: Int?=null,
    val type: Int? = null
): Parcelable