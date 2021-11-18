package kr.beimsupicures.mycomment.controllers.main.language

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.adapters.TalkTodayAdapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI
import kr.beimsupicures.mycomment.controllers.MainActivity
import kr.beimsupicures.mycomment.extensions.*
import java.util.*
import kotlin.system.exitProcess


class LanguageFragment : BaseFragment() {

    private lateinit var constraintLayoutKO: ConstraintLayout
    private lateinit var constraintLayoutEN: ConstraintLayout
    private lateinit var ivKorean: ImageView
    private lateinit var ivEnglish: ImageView
    private var languageTF = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_language, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            constraintLayoutKO = view.findViewById(R.id.constraintLayoutKO)
            constraintLayoutEN = view.findViewById(R.id.constraintLayoutEN)
            ivEnglish = view.findViewById(R.id.iv_english)
            ivKorean = view.findViewById(R.id.iv_korean)

            // 저장된 언어 코드를 불러온다.
            var language = BaseApplication.shared.getSharedPreferences().getLocale()
            val text = activity?.baseContext?.let { getSystemLanguage(it) }

            if (text != null) {
                setLocate(text)
                language = BaseApplication.shared.getSharedPreferences().getLocale()
            }



            when (language) {
                "en" -> {
                    languageTF = false
                    ivKorean.setImageResource(R.drawable.ic_radio_off)
                    ivEnglish.setImageResource(R.drawable.ic_radio_on)
                }
                "ko" -> {
                    languageTF = true
                    ivKorean.setImageResource(R.drawable.ic_radio_on)
                    ivEnglish.setImageResource(R.drawable.ic_radio_off)
                }
                else -> {
                    languageTF = false
                    ivKorean.setImageResource(R.drawable.ic_radio_off)
                    ivEnglish.setImageResource(R.drawable.ic_radio_on)
                }
            }

            constraintLayoutEN.setOnClickListener {
                if (languageTF) {
                    activity?.popup("", getString(R.string.language_popup_title)) {
                        setLocate("en")
                        languageTF = false
                        view.findNavController().navigate(R.id.action_global_splashFragment)

//                        activity?.parent?.let { it1 -> finishAffinity(it1) }
//                        val intent = Intent(activity, MainActivity::class.java)
//                        startActivity(intent)
//                        exitProcess(0)
                    }
                }
            }
            constraintLayoutKO.setOnClickListener {
                if (!languageTF) {
                    activity?.popup("", getString(R.string.language_popup_title)) {
                        setLocate("ko")
                        languageTF = true
                        view.findNavController().navigate(R.id.action_global_splashFragment)

//                        activity?.parent?.let { it1 -> finishAffinity(it1) }
//                        val intent = Intent(activity, MainActivity::class.java)
//                        startActivity(intent)
//                        exitProcess(0)
                    }
                }
            }

        }
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
            requireContext().resources?.displayMetrics
        )

        // Shared에 현재 언어 상태를 저장해 줍니다.
        BaseApplication.shared.getSharedPreferences().setLocale(Lang)
    }
}
