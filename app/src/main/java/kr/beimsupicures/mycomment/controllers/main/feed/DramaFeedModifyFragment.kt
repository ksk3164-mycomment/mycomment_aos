package kr.beimsupicures.mycomment.controllers.main.feed

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import gun0912.tedimagepicker.builder.TedImagePicker
import jp.wasabeef.richeditor.RichEditor
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.models.FeedDetailModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.getFeedId
import kr.beimsupicures.mycomment.extensions.getLocale
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.setLocale
import java.util.*

class DramaFeedModifyFragment : BaseFragment() {

    lateinit var editor: RichEditor
    lateinit var insertImageLayout: LinearLayout
    lateinit var title: EditText
    var editorEmpty: Boolean = false
    var editorText: String? = null
    var feed_seq = 0

    var feedDetail: FeedDetailModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed_modify, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            var language = BaseApplication.shared.getSharedPreferences().getLocale()

            if (language==null){
                val text = activity?.baseContext?.let { getSystemLanguage(it) }

                if (text != null) {
                    setLocate(text)
                    language = BaseApplication.shared.getSharedPreferences().getLocale()
                }

            }

            when(language){
                "en"->setLocate("en")
                "ko"->  setLocate("ko")
                else -> setLocate("en")
            }

            title = view.findViewById(R.id.title)
            insertImageLayout = view.findViewById(R.id.InsertImageLayout)
            editor = view.findViewById(R.id.editor)
            editor.setEditorFontSize(15)
            editor.setEditorFontColor(Color.BLACK)
            editor.setPadding(16, 16, 16, 16)
            editor.settings.allowFileAccess = true
            val displayMetrics = context?.resources?.displayMetrics
//            val dpHeight = displayMetrics!!.heightPixels / displayMetrics.density
            val dpWidth = displayMetrics!!.widthPixels / displayMetrics.density

            insertImageLayout.setOnClickListener {
                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->
                        editor.focusEditor()
//                        startLoadingUI()
//                        AmazonS3Loader.shared.uploadImage("feed", uri) { url ->
//                            Log.e("tjdrnr", "" + uri)
//                            stopLoadingUI()
                        editor.insertImage(uri.toString(), "", dpWidth.toInt() - 32)
//                        }
                    }
                }
            }


            Log.e("tjdrnr", "TF = $editorEmpty")
            editor.setOnTextChangeListener { text -> // Do Something
                Log.e("tjdrnr", "Editor = $text")

                editorEmpty = text.isNullOrEmpty()
                Log.e("tjdrnr", "TF2 = $editorEmpty")
                editorText = text

            }
        }
    }

    override fun loadModel() {
        super.loadModel()

        feed_seq = BaseApplication.shared.getSharedPreferences().getFeedId()
        FeedLoader.shared.getFeedDetail(feed_seq) { values ->

            val displayMetrics = this.resources.displayMetrics
            val dpWidth =
                displayMetrics!!.widthPixels / displayMetrics.density

            title.setText(values.title)
            editor.html = values.content?.replace(
                """" alt=""""",
                """" alt="" width="${dpWidth.toInt() - 32}""""
            )

            editorText = editor.html.toString()
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
            requireActivity().resources?.displayMetrics
        )
        // Shared에 현재 언어 상태를 저장해 줍니다.
        BaseApplication.shared.getSharedPreferences().setLocale(Lang)
    }

}