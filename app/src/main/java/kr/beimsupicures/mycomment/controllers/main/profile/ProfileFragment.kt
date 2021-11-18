package kr.beimsupicures.mycomment.controllers.main.profile

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import gun0912.tedimagepicker.builder.TedImagePicker
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.AmazonS3Loader
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.api.models.isMe
import kr.beimsupicures.mycomment.api.models.nameOnly
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.IntroDialog
import kr.beimsupicures.mycomment.components.dialogs.NicknameDialog
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI
import kr.beimsupicures.mycomment.extensions.*
import java.util.*


class ProfileFragment : BaseFragment() {

    lateinit var user: UserModel

    lateinit var profileBackgroundView: ImageView
    lateinit var profileView: ImageView

    lateinit var btnProfileBackground: ImageView
    lateinit var btnProfile: TextView
    lateinit var btnHistory: TextView

    lateinit var likeCountLabel: TextView
    lateinit var nameLabel: TextView

    lateinit var nicknameWrapperView: LinearLayout
    lateinit var nicknameLabel: TextView
    lateinit var introLabel: TextView

    lateinit var optionWrapperView: LinearLayout
    lateinit var btnSignOut: TextView
    lateinit var btnChangePassword: TextView
    lateinit var btnGuide: TextView
    lateinit var btnTerm: TextView
    lateinit var btnPrivacy: TextView
    lateinit var btnSecession: TextView

    lateinit var tvAlarmSetting: TextView

    lateinit var languageSetting: LinearLayout

    lateinit var tvLanguageSetting: TextView
    lateinit var tvLanguage: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            profileBackgroundView = view.findViewById(R.id.profileBackgroundView)
            profileView = view.findViewById(R.id.profileView)
            btnProfileBackground = view.findViewById(R.id.btnProfileBackground)
            btnProfileBackground.setOnClickListener {

                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->

                        startLoadingUI()
                        AmazonS3Loader.shared.uploadImage("badge", uri) { url ->

                            UserLoader.shared.updateProfileBadgeImage(url) { user ->
                                this.user = user

                                stopLoadingUI()
                                user.profile_badge_image_url?.let { profile_badge_image_url ->
                                    Glide.with(this).load(profile_badge_image_url)
                                        .transform(CenterCrop())
                                        .into(profileBackgroundView)

                                } ?: run {
                                    profileBackgroundView.setImageResource(android.R.color.transparent)
                                }
                            }
                        }
                    }
                }
            }
            btnProfile = view.findViewById(R.id.btnProfile)
            btnProfile.setOnClickListener {
                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->
                        startLoadingUI()
                        AmazonS3Loader.shared.uploadImage("profile", uri) { url ->
                            UserLoader.shared.updateProfileImage(url) { user ->
                                this.user = user

                                stopLoadingUI()
                                user.profile_image_url?.let { profile_image_url ->
                                    Glide.with(this).load(profile_image_url)
                                        .transform(CenterCrop(), CircleCrop())
                                        .into(profileView)

                                } ?: run {
                                    profileView.setImageResource(android.R.color.transparent)
                                }
                            }
                        }
                    }
                }
            }
            btnHistory = view.findViewById(R.id.btnHistory)
            likeCountLabel = view.findViewById(R.id.bookmarkCountLabel)
            nameLabel = view.findViewById(R.id.nameLabel)
            nicknameWrapperView = view.findViewById(R.id.nicknameWrapperView)
            nicknameLabel = view.findViewById(R.id.nicknameLabel)
            introLabel = view.findViewById(R.id.introLabel)

            introLabel.setOnClickListener { view ->

                when (user.isMe()) {
                    true -> {
                        context?.let { context ->
                            IntroDialog(view.context) { user ->
                                this.user = user
                                introLabel.text = "${user.intro}"

                            }.show()
                        }
                    }
                }
            }

            optionWrapperView = view.findViewById(R.id.optionWrapperView)
            btnSignOut = view.findViewById(R.id.btnSignOut)
            btnSignOut.setOnClickListener { view ->
                activity?.let{
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

                        mGoogleSignInClient.signOut().addOnCompleteListener {
                            Log.e("tjdrnr", "로그아웃성공")
                        }

                        // 로그아웃
//                    UserApiClient.instance.logout { error ->
//                        if (error != null) {
//                            Log.e("TAG", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
//                        }
//                        else {
//                            Log.i("TAG", "로그아웃 성공. SDK에서 토큰 삭제됨")
//                        }
//                    }

                        UserLoader.shared.signout()
                        BaseApplication.shared.getSharedPreferences().reset()
                        view.findNavController().navigate(R.id.action_global_splashFragment)

                }
            }

            btnGuide = view.findViewById(R.id.btnGuide)
            btnGuide.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.guide)
                view.findNavController().navigate(action)
            }
            btnTerm = view.findViewById(R.id.btnTerm)
            btnTerm.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.service)
                view.findNavController().navigate(action)
            }
            btnPrivacy = view.findViewById(R.id.btnPrivacy)
            btnPrivacy.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.privacy)
                view.findNavController().navigate(action)
            }
            btnSecession = view.findViewById(R.id.btnSecession)
            btnSecession.setOnClickListener {
                activity?.let {
                    it.popup(
                        it.getString(R.string.Delete_account_title),
                        it.getString(R.string.profile_delete_account)
                    ) {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

                        mGoogleSignInClient.signOut().addOnCompleteListener {
                            Log.e("tjdrnr", "로그아웃성공")
                        }


                        UserLoader.shared.secessionUser {
                            BaseApplication.shared.getSharedPreferences().reset()
                            view.findNavController().navigate(R.id.action_global_splashFragment)
                        }
                    }
                }

            }
            tvAlarmSetting = view.findViewById(R.id.tv_alarm_setting)

            val spannableString = SpannableString(tvAlarmSetting.text.toString())
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            tvAlarmSetting.text = spannableString
            tvAlarmSetting.setOnClickListener {
                openNotificationSettings()
            }

            languageSetting = view.findViewById(R.id.languageSetting)
            tvLanguageSetting = view.findViewById(R.id.tv_language_setting)
            languageSetting.setOnClickListener {
                view.findNavController().navigate(R.id.action_global_languageFragment)
            }
            tvLanguage = view.findViewById(R.id.tv_language)
            var language = BaseApplication.shared.getSharedPreferences().getLocale()

            if (language==null){
            val text = activity?.baseContext?.let { getSystemLanguage(it) }

                if (text != null) {
                    setLocate(text)
                    language = BaseApplication.shared.getSharedPreferences().getLocale()
                }

            }

            when(language){
                "en"->tvLanguage.text = "English"
                "ko"->  tvLanguage.text = "한국어"
                else -> tvLanguage.text = "English"
            }
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        val userId = ProfileFragmentArgs.fromBundle(requireArguments()).userId

        UserLoader.shared.getUser(userId) { user ->
            this.user = user

            user.profile_badge_image_url?.let { profile_badge_image_url ->
                Glide.with(this).load(profile_badge_image_url)
                    .transform(CenterCrop())
                    .into(profileBackgroundView)

            } ?: run {
                profileBackgroundView.setImageResource(android.R.color.transparent)
            }

            user.profile_image_url?.let { profile_image_url ->
                Glide.with(this).load(profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .into(profileView)

            } ?: run {
                profileView.setImageResource(android.R.color.transparent)
            }

            nameLabel.text = user.nameOnly()
            nicknameLabel.text = user.nickname
            nicknameLabel.setOnClickListener {
                when (user.isMe()) {
                    true -> {
                        context?.let { context ->
                            NicknameDialog(context) { user ->
                                this.user = user
                                nicknameLabel.text = "${user.nickname}"

                            }.show()
                        }
                    }
                }
            }

            user.intro?.let { intro ->
                introLabel.text = intro
            } ?: run {
                introLabel.text = ""
            }

            when (user.isMe()) {

                false -> {
                    btnProfile.visibility = View.GONE
                    btnProfileBackground.visibility = View.GONE
                    optionWrapperView.visibility = View.GONE
                    tvAlarmSetting.visibility = View.GONE
                    languageSetting.visibility = View.GONE
                }

                true -> {
                    btnProfile.visibility = View.VISIBLE
                    btnProfileBackground.visibility = View.VISIBLE
                }
            }
        }

        UserLoader.shared.getUserPickedCommentCount(userId) { count ->
            likeCountLabel.text = "$count"
            likeCountLabel.visibility = if (count == 0) View.GONE else View.VISIBLE
        }
    }

    fun openNotificationSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + context?.packageName)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    private fun getSystemLanguage(context: Context): String {
        val systemLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
        return systemLocale.language
    }
    //Locale 객체를 생성특정 지리적, 정치적 또는 문화적 영역을 나타냅니다.
    private fun setLocate(Lang: String) {
        val locale = Locale(Lang) // Local 객체 생성. 인자로는 해당 언어의 축약어가 들어가게 됩니다. (ex. ko, en)
        Locale.setDefault(locale) // 생성한 Locale로 설정을 해줍니다.

        val config = Configuration() //이 클래스는 응용 프로그램이 검색하는 리소스에 영향을 줄 수 있는
        // 모든 장치 구성 정보를 설명합니다.
        config.setLocale(locale) // 현재 유저가 선호하는 언어를 환경 설정으로 맞춰 줍니다.
        requireContext().resources?.updateConfiguration(
            config,
            requireActivity().resources?.displayMetrics
        )
        // Shared에 현재 언어 상태를 저장해 줍니다.
        BaseApplication.shared.getSharedPreferences().setLocale(Lang)
    }
}