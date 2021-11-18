package kr.beimsupicures.mycomment.controllers.splash

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.kakao.sdk.common.util.Utility
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.signin
import kr.beimsupicures.mycomment.extensions.getAccessToken
import kr.beimsupicures.mycomment.extensions.getSharedPreferences


class SplashFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        Log.e("getKeyHash", Utility.getKeyHash(requireContext()))

        BaseApplication.shared.getSharedPreferences().getAccessToken()?.let {
            Log.e("tjdrnr", "재로그인")
            signin { result ->
                when (result) {
                    true -> {
                        view?.let { view ->
//                            SearchLoader.shared.searchTalk("") { talk ->
                                Handler().postDelayed({
                                    val action =
                                        NavigationDirections.actionGlobalTalkFragment2(null)
                                    Navigation.findNavController(
                                        requireActivity(),
                                        R.id.nav_host_fragment
                                    )
                                        .navigate(action)
                                }, 1000)
//                            }
                        }
                    }
                }
            }
        } ?: run {
            view?.let { view ->
                Log.e("tjdrnr", "로그인")
//                SearchLoader.shared.searchTalk("") { talk ->
                    Handler().postDelayed({
                        val action =
                            NavigationDirections.actionGlobalTalkFragment2(null)
                        Navigation.findNavController(
                            requireActivity(),
                            R.id.nav_host_fragment
                        )
                            .navigate(action)
                    }, 1000)
//                }
            }
        }
    }
}

