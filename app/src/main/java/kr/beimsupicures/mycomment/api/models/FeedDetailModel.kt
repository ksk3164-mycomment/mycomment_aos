package kr.beimsupicures.mycomment.api.models

data class FeedDetailModel(
    var feed_seq: Int,
    var type: String?,
    var talk_id: Int,
    var user_id: Int,
    var title: String?,
    var nickname: String?,
    var profile_image_url : String?,
    var view_cnt : String?,
    var imgs: String?,
    var content: String?,
    var c_ts: String?,
    var m_ts: String?
)