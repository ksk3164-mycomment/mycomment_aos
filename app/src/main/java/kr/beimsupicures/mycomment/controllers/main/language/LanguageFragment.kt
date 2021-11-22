package kr.beimsupicures.mycomment.controllers.main.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*


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
            val text = requireContext().getSystemLanguage()

            requireContext().setLocate(text)
            language = BaseApplication.shared.getSharedPreferences().getLocale()



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
                        requireContext().setLocate("en")
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
                        requireContext().setLocate("ko")
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

}