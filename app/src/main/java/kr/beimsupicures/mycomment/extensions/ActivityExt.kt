package kr.beimsupicures.mycomment.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.CustomDialog
import kr.beimsupicures.mycomment.components.dialogs.NoticeDialog
import java.text.SimpleDateFormat
import java.util.*

fun Activity.popup(message: String, title: String, cancel: (() -> Unit)? = null, confirm: (() -> Unit)? = null) {
    val builder = AlertDialog.Builder(this,R.style.AlertDialogStyle)

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setPositiveButton(getString(R.string.Confirm)) { _: DialogInterface, _: Int ->
            confirm?.invoke()
        }
        setNegativeButton(getString(R.string.Cancel)) { _: DialogInterface, _: Int ->
            cancel?.invoke()
        }

        show()
    }
}
//fun Activity.popup(message: String, title: String, cancel: (() -> Unit)? = null, confirm: (() -> Unit)? = null) {
//    val builder = CustomDialog(this)
//
//    with(builder)
//    {
//        setTitle(title)
//        setMessage(message)
//        setPositiveButton("확인") {
//            confirm?.invoke()
//        }
//        setNegativeButton("취소") {
//            cancel?.invoke()
//        }
//        show()
//        setBackground()
//    }
//
//}
fun Activity.notice(imageURL : String,cancel: (() -> Unit)? = null, confirm: (() -> Unit)? = null) {
    val builder = NoticeDialog(this)

    with(builder)
    {
        setImageURL(imageURL)
        setPositiveButton(getString(R.string.dialog_notice_btnToday)) {
            confirm?.invoke()
        }
        setNegativeButton(getString(R.string.Close)) {
            cancel?.invoke()
        }
        show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
//    builder.dialog?.setCancelable(false)
}

fun Activity.alert(message: String, title: String, confirm: () -> Unit) {
    val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setPositiveButton(getString(R.string.Confirm)) { _: DialogInterface, _: Int ->
            confirm.invoke()
        }

        show()
//        setBackground()
    }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}
