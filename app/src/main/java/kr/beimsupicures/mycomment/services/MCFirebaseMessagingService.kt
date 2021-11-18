package kr.beimsupicures.mycomment.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AnalyticsLoader
import kr.beimsupicures.mycomment.api.models.MentionModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.controllers.MainActivity
import kr.beimsupicures.mycomment.extensions.getCurrentTalkId
import kr.beimsupicures.mycomment.extensions.getSharedPreferences

data class ChannelInfo(val channelId: String, val channelName: String, val description: String, val importance: Int)
data class MessageInfo(val title: String, val body: String, val redirection: String, val payload: String)

class MCFirebaseMessagingService : FirebaseMessagingService() {

    enum class Redirection(val value: String) {
        TalkDetail("TalkDetailFragment"),
        TalkComment("TalkComment"),
        FeedComment("FeedComment"),
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

//
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d("fcm", "Message data payload: ${remoteMessage.data}")
//            sendNotification(remoteMessage.data)
//        }
//
//        remoteMessage.notification?.let {
//            Log.d("fcm2", "Message Notification Body: ${it.body}")
////            sendNotification(remoteMessage.notification)
//        }

        if (remoteMessage.notification != null) { //포그라운드
            Log.d("fcm2", "Message Notification title: ${remoteMessage.notification!!.title}")
            Log.d("fcm2", "Message Notification Body: ${remoteMessage.notification!!.body}")
            sendNotification(remoteMessage.data)
        }
        else if (remoteMessage.data.isNotEmpty()) { //백그라운드
            Log.d("fcm", "Message data payload: ${remoteMessage.data}")
            sendNotification(remoteMessage.data)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendNotification(data: MutableMap<String, String>) {
        Log.e("tjdrnr", "1")
        try {
            val title = data["title"].toString()
            val body = data["body"].toString()
            val redirection = data["redirection"].toString()
            val payload = data["payload"].toString()

            when (redirection) {
                Redirection.TalkDetail.value -> {
                    val talkModel = makeTalkModel(payload)
                    talkModel?.let {
                        val currentTalkId = BaseApplication.shared.getSharedPreferences().getCurrentTalkId()
                        if (it.id == currentTalkId) return
                    }
                    sendNotification(makeBookmarkChannel(), MessageInfo(title, body, redirection, payload))
                }
                Redirection.TalkComment.value -> {
                    val mentionModel = makeMentionModel(payload)
                    mentionModel?.let {
                        val currentTalkId = BaseApplication.shared.getSharedPreferences().getCurrentTalkId()
                        if (it.talk.id == currentTalkId) {
                            AnalyticsLoader.shared.reportMentionReceiveDisable(mentionModel.mention.id)
                            return
                        }
                        AnalyticsLoader.shared.reportMentionReceive(mentionModel.mention.id)
                    }
                    sendNotification(makeCommentChannel(), MessageInfo(title, body, redirection, payload))
                }
                Redirection.FeedComment.value -> {
                    val mentionModel = makeMentionModel(payload)
                    mentionModel?.let {
                        val currentTalkId = BaseApplication.shared.getSharedPreferences().getCurrentTalkId()
                        if (it.talk.id == currentTalkId) {
                            AnalyticsLoader.shared.reportMentionReceiveDisable(mentionModel.mention.id)
                            return
                        }
                        AnalyticsLoader.shared.reportMentionReceive(mentionModel.mention.id)
                    }
                    sendNotification(makeCommentChannel(), MessageInfo(title, body, redirection, payload))
                }
            }
        } catch (e: Exception) { }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun makeCommentChannel(): ChannelInfo {
        val channelId = "CommentNotification"
        val channelName = "나에게 답한 채팅 알림"
        val description = "나에게 답한 채팅 알림"
        val importance = NotificationManager.IMPORTANCE_HIGH
        return ChannelInfo(channelId, channelName, description, importance)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun makeBookmarkChannel(): ChannelInfo {
        val channelId = "BookmarkNotification"
        val channelName = "즐겨보는 드라마 시작 시간 알림"
        val description = "즐겨보는 드라마 시작 시간 알림"
        val importance = NotificationManager.IMPORTANCE_HIGH
        return ChannelInfo(channelId, channelName, description, importance)
    }

    private fun sendNotification(channelInfo: ChannelInfo, messageInfo: MessageInfo) {
        Log.e("tjdrnr", "2")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Redirection", messageInfo.redirection)
            putExtra("Payload", messageInfo.payload)
        }

        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelInfo.channelId, channelInfo.channelName, channelInfo.importance)
            channel.description = channelInfo.description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(this, (System.currentTimeMillis()/1000).toInt(), intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelInfo.channelId)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_noticon))
            .setSmallIcon(R.mipmap.ic_noticon)
            .setContentTitle(messageInfo.title)
            .setContentText(messageInfo.body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        notificationManager.notify((System.currentTimeMillis()/1000).toInt(), notificationBuilder.build())
    }
}

fun MCFirebaseMessagingService.makeTalkModel(payload: String): TalkModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, TalkModel::class.java)
}

fun MCFirebaseMessagingService.makeMentionModel(payload: String): MentionModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, MentionModel::class.java)
}