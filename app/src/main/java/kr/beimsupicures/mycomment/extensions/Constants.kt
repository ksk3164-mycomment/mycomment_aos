package kr.beimsupicures.mycomment.extensions

object Constants {

    val Any.TAG: String
        get() {
            val tag = javaClass.simpleName
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

}

object API{
    const val BASE_URL = "https://api.unsplash.com/"


}