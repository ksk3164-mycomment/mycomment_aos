package kr.beimsupicures.mycomment.common

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kr.beimsupicures.mycomment.api.loaders.UserLoader

fun accessUser() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("Main", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Log and toast
        task.result?.let { result ->
            val token = task.result
            Log.e("fcm", token)
            UserLoader.shared.accessUser(token)
        }
    })

//    FirebaseInstanceId.getInstance().instanceId
//        .addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("AccessUser", "getInstanceId failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new Instance ID token
//            task.result?.let { result ->
//                val token = result.token
//                Log.e("fcm", token)
//                UserLoader.shared.accessUser(token)
//            }
//        })
}