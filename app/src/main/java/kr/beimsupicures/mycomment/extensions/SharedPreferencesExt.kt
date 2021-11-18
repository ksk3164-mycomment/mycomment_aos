package kr.beimsupicures.mycomment.extensions

import android.content.SharedPreferences
import com.google.gson.Gson
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.application.BaseApplication

fun SharedPreferences.reset() {
    BaseApplication.shared.getSharedPreferences().edit().clear().commit()
}

fun SharedPreferences.setUser(value: UserModel) {
    BaseApplication.shared.getSharedPreferences().edit().putString("user", Gson().toJson(value))
        .commit()
}

fun SharedPreferences.setFeed(value: FeedModel) {
    BaseApplication.shared.getSharedPreferences().edit().putString("feed", Gson().toJson(value))
        .commit()
}

fun SharedPreferences.setTalk(value: TalkModel) {
    BaseApplication.shared.getSharedPreferences().edit().putString("talk", Gson().toJson(value))
        .commit()
}


fun SharedPreferences.getUser(): UserModel? {
    BaseApplication.shared.getSharedPreferences().getString("user", null)?.let { value ->
        return Gson().fromJson<UserModel>(value, UserModel::class.java)

    } ?: run {
        return null
    }
}

fun SharedPreferences.getTalk(): TalkModel? {
    BaseApplication.shared.getSharedPreferences().getString("talk", null)?.let { value ->
        return Gson().fromJson<TalkModel>(value, TalkModel::class.java)

    } ?: run {
        return null
    }
}

fun SharedPreferences.getFeed(): FeedModel? {
    BaseApplication.shared.getSharedPreferences().getString("feed", null)?.let { value ->
        return Gson().fromJson<FeedModel>(value, FeedModel::class.java)

    } ?: run {
        return null
    }
}

fun SharedPreferences.setFeedDetailUserId(value: Int) {
    BaseApplication.shared.getSharedPreferences().edit().putInt("feedUserId", value).commit()
}

fun SharedPreferences.setAccessToken(value: String) {
    BaseApplication.shared.getSharedPreferences().edit().putString("accessToken", value).commit()
}
fun SharedPreferences.setLocale(value: String) {
    BaseApplication.shared.getSharedPreferences().edit().putString("locale", value).commit()
}
fun SharedPreferences.getLocale(): String? {
    return BaseApplication.shared.getSharedPreferences().getString("locale", null)
}

fun SharedPreferences.setFeedId(value: Int) {
    BaseApplication.shared.getSharedPreferences().edit().putInt("feedId", value).commit()
}

fun SharedPreferences.getFeedId(): Int {
    return BaseApplication.shared.getSharedPreferences().getInt("feedId", 0)
}

fun SharedPreferences.getFeedDetailUserId(): Int {
    return BaseApplication.shared.getSharedPreferences().getInt("feedUserId", 0)
}
fun SharedPreferences.removeKey(key: String?) {
    BaseApplication.shared.getSharedPreferences().edit().remove(key).commit()
}

fun SharedPreferences.getAccessToken(): String? {
    return BaseApplication.shared.getSharedPreferences().getString("accessToken", null)
}

fun SharedPreferences.setTalkTime() {
    val value = System.currentTimeMillis() / 1000
    BaseApplication.shared.getSharedPreferences().edit().putLong("talkStartTime", value).commit()
}

fun SharedPreferences.getTalkTime(): Long? {
    return BaseApplication.shared.getSharedPreferences().getLong("talkStartTime", 0)
}

fun SharedPreferences.setWatchTime() {
    val value = System.currentTimeMillis() / 1000
    BaseApplication.shared.getSharedPreferences().edit().putLong("watchStartTime", value).commit()
}

fun SharedPreferences.getWatchTime(): Long? {
    return BaseApplication.shared.getSharedPreferences().getLong("watchStartTime", 0)
}

fun SharedPreferences.setCurrentTalkId(id: Int) {
    BaseApplication.shared.getSharedPreferences().edit().putInt("talkId", id).commit()
}

fun SharedPreferences.setPostTalkId(id: Int) {
    BaseApplication.shared.getSharedPreferences().edit().putInt("postTalkId", id).commit()
}

fun SharedPreferences.getPostTalkId(): Int? {
    return BaseApplication.shared.getSharedPreferences().getInt("postTalkId", -1)
}

fun SharedPreferences.getCurrentTalkId(): Int? {
    return BaseApplication.shared.getSharedPreferences().getInt("talkId", -1)
}

fun SharedPreferences.getCurrentPosition(): Long? {
    return BaseApplication.shared.getSharedPreferences().getLong("currentPosition", 0)
}

fun SharedPreferences.setCurrentPosition(id: Long) {
    BaseApplication.shared.getSharedPreferences().edit().putLong("currentPosition", id).commit()
}
fun SharedPreferences.setPlayPause(playPause: Boolean) {
    BaseApplication.shared.getSharedPreferences().edit().putBoolean("playPause", playPause).commit()
}
fun SharedPreferences.getPlayPause(): Boolean {
    return BaseApplication.shared.getSharedPreferences().getBoolean("playPause",false)
}
