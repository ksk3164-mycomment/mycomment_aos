package kr.beimsupicures.mycomment.extensions

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kr.beimsupicures.mycomment.extensions.Constants.TAG

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun EditText.textChangesToFlow(): Flow<CharSequence?> {

    return callbackFlow<CharSequence?> {

        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =Unit
            override fun afterTextChanged(s: Editable?) = Unit

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(text).isSuccess
            }
        }
        addTextChangedListener(listener)
        awaitClose {
            removeTextChangedListener(listener)
        }

    }.onStart {
        emit(text)
    }
}