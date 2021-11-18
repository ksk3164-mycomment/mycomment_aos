package kr.beimsupicures.mycomment.components.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.common.accessUser
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.LoadingDialog
import kr.beimsupicures.mycomment.extensions.alert
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.setUser

abstract class BaseFragment : Fragment() {

    lateinit var loadingDialog: LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadModel()
        loadViewModel()
        loadUI()

    }

    open fun loadModel() {

    }

    open fun loadViewModel() {

    }

    open fun loadUI() {

    }

    open fun reloadUI() {

    }

    open fun fetchModel() {

    }
}

fun BaseFragment.startLoadingUI() {
    fragmentManager?.let { fragmentManager ->
        view?.let { view ->

            loadingDialog = LoadingDialog()
            loadingDialog.show(fragmentManager, "")
        }
    }
}

fun BaseFragment.stopLoadingUI() {
    loadingDialog.dismiss()
}

fun BaseFragment.signin(completionHandler: (Boolean) -> Unit) {
    accessUser()
    UserLoader.shared.getUser { user ->
        if (user.banned_at != null) {
            activity?.let {
                it.alert(it.getString(R.string.Basefragment_loginban), it.getString(R.string.Notification)) {
                    completionHandler(false)
                }
            }
        } else {
            BaseApplication.shared.getSharedPreferences().setUser(user)
            completionHandler(true)
        }
    }
}