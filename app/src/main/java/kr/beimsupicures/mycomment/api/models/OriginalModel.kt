package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OriginalModel(
    val chapter: MutableList<OriginalDetailModel>? = null,
    val original_seq: Int? = null,
    val talk_id: Int? = null,
    val thumbnail_url: String?=null,
    val title: String? = null,
    var view_count: Int? = null,
    val description : String? = null,
    val content : String? = null
): Parcelable

@Parcelize
data class OriginalDetailModel(
    val chapter_seq: Int? = null,
    val live: Int?=null,
    val play_time: Int?=null,
    var view_count: Int=0,
    val sub_title: String?=null,
    val thumbnail_url: String?=null,
    val video_url : String?=null
) : Parcelable