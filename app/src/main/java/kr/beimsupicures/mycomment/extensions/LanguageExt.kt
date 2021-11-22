package kr.beimsupicures.mycomment.extensions

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import kr.beimsupicures.mycomment.components.application.BaseApplication
import java.util.*

fun Context.getSystemLanguage(): String {
    val systemLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.resources.configuration.locales.get(0)
    } else {
        this.resources.configuration.locale
    }
    return systemLocale.language
}

//Locale 객체를 생성특정 지리적, 정치적 또는 문화적 영역을 나타냅니다.
fun Context.setLocate(Lang: String) {
    val locale = Locale(Lang) // Local 객체 생성. 인자로는 해당 언어의 축약어가 들어가게 됩니다. (ex. ko, en)
    Locale.setDefault(locale) // 생성한 Locale로 설정을 해줍니다.

    val config = Configuration() //이 클래스는 응용 프로그램이 검색하는 리소스에 영향을 줄 수 있는
    // 모든 장치 구성 정보를 설명합니다.
    config.setLocale(locale) // 현재 유저가 선호하는 언어를 환경 설정으로 맞춰 줍니다.
    this.resources?.updateConfiguration(
        config,
        this.resources?.displayMetrics
    )
    // Shared에 현재 언어 상태를 저장해 줍니다.
    BaseApplication.shared.getSharedPreferences().setLocale(Lang)
}