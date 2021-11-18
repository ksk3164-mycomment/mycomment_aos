package kr.beimsupicures.mycomment.controllers.signs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.*
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

    override fun loadModel() {
        super.loadModel()

    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(callbackManager, object :
                FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.e("TAG", "${loginResult?.accessToken} ${loginResult?.accessToken?.userId}")
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
                        Log.e("TAG", "로그인 실패", error)
                    } else if (token != null) {
                        Log.i("TAG", "로그인 성공 ${token.accessToken}")
                        // 사용자 정보 요청 (기본)
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e("TAG", "사용자 정보 요청 실패", error)
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
//                                Log.e("tjdrnr", "로그인 실패", error)
//                            } else if (token != null) {
//                                Log.i("tjdrnr", "로그인 성공 ${token.accessToken}")
//                            }
//                        }
//                    }
// 로그인 조합 예제

// 로그인 공통 callback 구성
//                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//                    if (error != null) {
//                        Log.e("TAG", "로그인 실패", error)
//                    } else if (token != null) {
//                        Log.i("TAG", "로그인 성공 ${token.accessToken}")
//                        UserApiClient.instance.me { user, error ->
//                            if (error != null) {
//                                Log.e("TAG", "사용자 정보 요청 실패", error)
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
//// 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
//                if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
//                    UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
//                } else {
//                    UserApiClient.instance.loginWithKakaoAccount(
//                        requireContext(),
//                        callback = callback
//                    )
//                }

                //업뎃이전
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
        GraphRequest(token,
            "/$userId/",
            parameters,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject

                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }

                jsonObject.getString("email")?.let { email ->
                    jsonObject.getString("name")?.let { nickname ->

                        sign(email, nickname, UserModel.SocialProvider.facebook)
                    }
                }

            }).executeAsync()
    }
}

fun SignInFragment.sign(email: String, nickname: String, type: UserModel.SocialProvider) {
    // 닉네임 중복 확인
    Log.e("TAG", "${email}, ${nickname}, ${type}")
    UserLoader.shared.uniqueEmail(email) { result ->
        when (result) {
            true -> {
                val viewmodel = SignStep1ViewModel(email, nickname, type)
                val action =
                    SignInFragmentDirections.actionSignInFragmentToSignStep1Fragment(viewmodel)
                findNavController().navigate(action)
            }
            false -> {
                // 소셜회원가입 (중복인 경우 로그인)
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
