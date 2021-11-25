package kr.beimsupicures.mycomment.components.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.dialog_notice.view.*
import kotlinx.android.synthetic.main.dialog_popup.view.*
import kr.beimsupicures.mycomment.R


class NoticeDialog(private val context: Context) {

    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }

    private val view: View by lazy {
        View.inflate(context, R.layout.dialog_notice, null)
    }

    var dialog: AlertDialog? = null

    // 터치 리스너 구현
    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            Handler(Looper.getMainLooper()).postDelayed({
                dismiss()
            }, 5)
        }
        false
    }

    fun setPositiveButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): NoticeDialog {
        view.btnToday.apply {
            text = context.getText(textId)
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setPositiveButton(text: CharSequence, listener: (view: View) -> (Unit)): NoticeDialog {
        view.btnToday.apply {
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setNegativeButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): NoticeDialog {
        view.negativeButton.apply {
            text = context.getText(textId)
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setNegativeButton(text: CharSequence, listener: (view: View) -> (Unit)): NoticeDialog {
        view.btnClose.apply {
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun create() {
        dialog = builder.create()
    }

    fun show() {
        dialog = builder.create()
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun setImageURL(imageURL: String) {
        Glide.with(context).load(imageURL)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.color.colorGrey)
            .into(view.ivNotice)
    }

    fun setBackground() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = 800
        params?.height = 800
        dialog?.window?.attributes = params

    }

    fun setLayout(width: Int, height: Int) {
        dialog?.window?.setLayout(width, height)
    }

}