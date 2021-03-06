package kr.beimsupicures.mycomment.controllers.signs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.signin
import kr.beimsupicures.mycomment.viewmodels.signs.SignStep1ViewModel

class SignInFragment : BaseFragment() {

    lateinit var callbackManager: CallbackManager

    lateinit var btnFacebook: TextView
    lateinit var btnGoogle: TextView
    lateinit var btnKakao: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            FacebookSdk.sdkInitialize(getApplicationContext())
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(callbackManager, object :
                FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.e(
                        "TAG",
                        "facebook Login = ${loginResult?.accessToken} ${loginResult?.accessToken?.userId}"
                    )
                    getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
                }

                override fun onCancel() {

                }

                override fun onError(exception: FacebookException) {

                }
            })


//            Session.getCurrentSession().addCallback(object : ISessionCallback {
//                override fun onSessionOpenFailed(exception: KakaoException?) {
//                    Log.e("onSessionOpened", "SessionStatusCallback.onSessionOpenFailed exception: $exception")
//                }
//
//                override fun onSessionOpened() {
//                    Log.e("onSessionOpened", "SessionStatusCallback.onSessionOpened")
//
//                    UserManagement.getInstance().me(object : MeV2ResponseCallback() {
//                        override fun onSuccess(result: MeV2Response?) {
//                            Log.e("onSuccess", "success to update profile. msg = $result")
//
//                            val kakaoAccount = JSONObject(result.toString()).getJSONObject("kakao_account")
//                            val properties = JSONObject(result.toString()).getJSONObject("properties")
//
//                            kakaoAccount?.let { kakaoAccount ->
//                                properties?.let { properties ->
//
//                                    val nickname = properties.getString("nickname")
//                                    val email = kakaoAccount.getString("email")
//
//                                    UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
//                                        override fun onCompleteLogout() {
//                                            sign(email, nickname, UserModel.SocialProvider.kakao)
//                                        }
//                                    })
//                                }
//                            }
//                        }
//
//                        override fun onSessionClosed(errorResult: ErrorResult?) {
//                            Log.e("onSessionClosed", "failed to update profile. msg = $errorResult")
//                        }
//
//                    })
//                }
//            })
//            Session.getCurrentSession().checkAndImplicitOpen()

            btnFacebook = view.findViewById(R.id.btnFacebook)
            btnFacebook.setOnClickListener {
                LoginManager.getInstance().logInWithReadPermissions(
                    activity,
                    listOf("user_birthday", "public_profile", "user_gender", "email")
                )
            }
            btnGoogle = view.findViewById(R.id.btnGoogle)
            btnGoogle.setOnClickListener {
                activity?.let { activity ->

                    // Configure Google Sign In
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    activity.startActivityForResult(
                        GoogleSignIn.getClient(
                            activity,
                            gso
                        ).signInIntent, 99
                    )
// Configure Google Sign In
//                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestIdToken(getString(R.string.default_web_client_id))
//                        .requestEmail()
//                        .build()
//
//                    val googleSignInClient = GoogleSignIn.getClient(activity, gso)
//
//                    val signInIntent = googleSignInClient.signInIntent
//                    activity.startActivityForResult(signInIntent, 99)

                }
            }
            btnKakao = view.findViewById(R.id.btnKakao)
            btnKakao.setOnClickListener {

                UserApiClient.instance.loginWithKakaoAccount(
                    requireContext(),
                    prompts = listOf(Prompt.LOGIN)
                ) { token, error ->
                    if (error != null) {
                        Log.e("TAG", "????????? ??????", error)
                    } else if (token != null) {
                        Log.i("TAG", "????????? ?????? ${token.accessToken}")
                        // ????????? ?????? ?????? (??????)
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e("TAG", "????????? ?????? ?????? ??????", error)
                            } else if (user != null) {
                                user.kakaoAccount?.email?.let { it1 ->
                                    user.kakaoAccount?.profile?.nickname?.let { it2 ->
                                        sign(
                                            it1,
                                            it2, UserModel.SocialProvider.kakao
                                        )
                                    }
                                }
                            }
                        }

                    }
                }

//                    context?.let { it1 ->
//                        Log.e("tjdrnr", "click")
//                        UserApiClient.instance.loginWithKakaoAccount(it1) { token, error ->
//                            Log.e("tjdrnr", "123")
//                            if (error != null) {
//                                Log.e("tjdrnr", "????????? ??????", error)
//                            } else if (token != null) {
//                                Log.i("tjdrnr", "????????? ?????? ${token.accessToken}")
//                            }
//                        }
//                    }
// ????????? ?????? ??????

// ????????? ?????? callback ??????
//                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//                    if (error != null) {
//                        Log.e("TAG", "????????? ??????", error)
//                    } else if (token != null) {
//                        Log.i("TAG", "????????? ?????? ${token.accessToken}")
//                        UserApiClient.instance.me { user, error ->
//                            if (error != null) {
//                                Log.e("TAG", "????????? ?????? ?????? ??????", error)
//                            } else if (user != null) {
//                                user.kakaoAccount?.email?.let { it1 ->
//                                    user.kakaoAccount?.profile?.nickname?.let { it2 ->
//                                        sign(
//                                            it1,
//                                            it2, UserModel.SocialProvider.kakao
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//// ??????????????? ???????????? ????????? ?????????????????? ?????????, ????????? ????????????????????? ?????????
//                if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
//                    UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
//                } else {
//                    UserApiClient.instance.loginWithKakaoAccount(
//                        requireContext(),
//                        callback = callback
//                    )
//                }

                //????????????
//                UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
//                    override fun onCompleteLogout() {
//                        Session.getCurrentSession().open(AuthType.KAKAO_TALK, activity)
//                    }
//                })
            }

        }
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(
            token,
            "/$userId/",
            parameters,
            HttpMethod.GET
        ) { response ->
            val jsonObject = response.jsonObject

            // Facebook Access Token
            // You can see Access Token only in Debug mode.
            // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.

            if (BuildConfig.DEBUG) {
                FacebookSdk.setIsDebugEnabled(true)
                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
            }

            //facebook login email ?????????
            if (jsonObject.has("email")) {
                sign(
                    jsonObject.getString("email"),
                    jsonObject.getString("name"),
                    UserModel.SocialProvider.facebook
                )
            } else {
                sign(
                    "$userId@facebook.com",
                    jsonObject.getString("name"),
                    UserModel.SocialProvider.facebook
                )
            }

        }.executeAsync()
    }
}

fun SignInFragment.sign(email: String, nickname: String, type: UserModel.SocialProvider) {
    // ????????? ?????? ??????
    Log.e("tjdrnr", "${email}, ${nickname}, ${type}")

    UserLoader.shared.uniqueEmail(email) { result ->
        when (result) {
            true -> {
                val viewmodel = SignStep1ViewModel(email, nickname, type)
                val action =
                    SignInFragmentDirections.actionSignInFragmentToSignStep1Fragment(viewmodel)
                findNavController().navigate(action)
            }
            false -> {
                // ?????????????????? (????????? ?????? ?????????)
                UserLoader.shared.addUser(email = email, sns = type, nickname = nickname) {
                    UserLoader.shared.getUser { user ->
                        signin {
                            view?.findNavController()?.navigate(R.id.action_global_splashFragment)
                        }
                    }
                }
            }
        }
    }
}
