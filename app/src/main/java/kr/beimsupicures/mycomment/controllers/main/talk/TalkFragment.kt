package kr.beimsupicures.mycomment.controllers.main.talk

import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.islamkhsh.CardSliderIndicator
import com.github.islamkhsh.CardSliderViewPager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_talk.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.*
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.components.adapters.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*
import kr.beimsupicures.mycomment.services.MCFirebaseMessagingService
import java.text.SimpleDateFormat
import java.util.*


class TalkFragment : BaseFragment() {

    var ad: MutableList<AdModel> = mutableListOf()

    var ymd_date: String = ""
    var talk: MutableList<TalkModel> = mutableListOf()
    var bookmark: MutableList<TalkModel> = mutableListOf()
    var category: MutableList<TalkModel> = mutableListOf()

    var recommend: MutableList<TalkModel> = mutableListOf()
    var popular: MutableList<TalkModel> = mutableListOf()

    var pickTop: MutableList<PickTopModel> = mutableListOf()

    var recommendList: MutableList<Int> = mutableListOf()
    var popularList: MutableList<Int> = mutableListOf()

    val database = FirebaseDatabase.getInstance()

    lateinit var bannerView: CardSliderViewPager
    lateinit var bannerIndicator: CardSliderIndicator
    lateinit var bannerAdapter: BannerAdapter
    lateinit var tvBookMark: TextView

    lateinit var bookMarkView: RecyclerView
    lateinit var bookMarkAdapter: TalkBookmarkAdapter

    lateinit var rvDrama: RecyclerView
    lateinit var dramaAdapter: TalkTodayAdapter

    lateinit var rvRecommend: RecyclerView
    lateinit var recommendAdapter: TalkRecommendAdapter

    lateinit var rvPopular: RecyclerView
    lateinit var popularAdapter: TalkPopularAdapter

    lateinit var tvFirstSympathyName: TextView
    lateinit var tvSecondSympathyName: TextView
    lateinit var ivFirstProfile: ImageView
    lateinit var ivSecondProfile: ImageView
    lateinit var tvOrganization: TextView

    lateinit var firstProfileWrapper: ConstraintLayout
    lateinit var secondProfileWrapper: ConstraintLayout
    lateinit var layoutSecondSympathy: ConstraintLayout

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var constraintLayout: ConstraintLayout

    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    var shimmer = false

    var eventModel: MutableList<EventModel> = mutableListOf()

    private val eventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            dataSnapshot.value?.let { value ->
                try {
                    eventModel.clear()
                    for (data in dataSnapshot.children) {
                        val modelResult = data.getValue(EventModel::class.java)
                        eventModel.add(modelResult!!)
                        eventModel = eventModel.filter { it.live == 1 }.toMutableList()
                    }

                } catch (e: NumberFormatException) {
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when (activity?.intent?.getStringExtra("Redirection") ?: null) {
            MCFirebaseMessagingService.Redirection.TalkDetail.value -> {
                activity?.intent?.getStringExtra("Payload")?.let { payload ->
                    val talkModel = makeTalkModel(payload)
                    if (talkModel != null) {
                        val action = NavigationDirections.actionGlobalTalkDetailFragment(talkModel)
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(action)
                    }
                }
            }
            MCFirebaseMessagingService.Redirection.TalkComment.value -> {
                activity?.intent?.getStringExtra("Payload")?.let { payload ->
                    val mentionModel = makeMentionModel(payload)
                    if (mentionModel != null) {
                        AnalyticsLoader.shared.reportMentionConfirm(mentionModel.mention.id)
                        val action =
                            NavigationDirections.actionGlobalTalkDetailFragment(mentionModel.talk)
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(action)
                    }
                }
            }
            MCFirebaseMessagingService.Redirection.FeedComment.value -> {
                activity?.intent?.getStringExtra("Payload")?.let { payload ->
                    val mentionModel = makeMentionModel(payload)
                    if (mentionModel != null) {
                        AnalyticsLoader.shared.reportMentionConfirm(mentionModel.mention.id)
                        val action =
                            NavigationDirections.actionGlobalDramaFeedDetailFragment()
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(action)
                    }
                }
            }
            else -> {
            }

        }

        BaseApplication.shared.getSharedPreferences().setCurrentTalkId(-1)
        activity?.intent?.removeExtra("Redirection")
        activity?.intent?.removeExtra("deepLink")
        activity?.intent?.removeExtra("Payload")

        return inflater.inflate(R.layout.fragment_talk, container, false)

    }

    override fun onResume() {
        super.onResume()
        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            layoutSecondSympathy = view.findViewById(R.id.layout_second_sympathy)

            bannerView = view.findViewById(R.id.bannerView)
            bannerIndicator = view.findViewById(R.id.bannerIndicator)
            val selectedIndicator: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.select_dot)
            bannerIndicator.selectedIndicator = selectedIndicator

            bannerAdapter = BannerAdapter(activity, ad)
            bannerView.adapter = bannerAdapter
            bannerView.layoutParams.height =
                (((resources.configuration.screenWidthDp - 16 - 16).toFloat() / 343F) * 110F).toInt().dp

            tvFirstSympathyName = view.findViewById(R.id.tv_first_sympathy_name)
            tvSecondSympathyName = view.findViewById(R.id.tv_second_sympathy_name)
            ivFirstProfile = view.findViewById(R.id.iv_first_profile)
            ivSecondProfile = view.findViewById(R.id.iv_second_profile)
            firstProfileWrapper = view.findViewById(R.id.first_profile_wrapper)
            secondProfileWrapper = view.findViewById(R.id.second_profile_wrapper)
            tvBookMark = view.findViewById(R.id.tvBookMark)
            tvOrganization = view.findViewById(R.id.tvOrganization)
            rvDrama = view.findViewById(R.id.rvDrama)
            rvPopular = view.findViewById(R.id.rvPopular)

            shimmerFrameLayout = view.findViewById(R.id.shimmer_framelayout)
            constraintLayout = view.findViewById(R.id.constraintLayout)

            constraintLayout.visibility = View.VISIBLE
            shimmerFrameLayout.visibility = View.GONE

            if (!shimmer) {
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                constraintLayout.visibility = View.GONE
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()

                shimmer = true
            }

            swipeRefreshLayout = view.findViewById(R.id.swipe_container)
            swipeRefreshLayout.setOnRefreshListener {
                fetchModel()
                swipeRefreshLayout.isRefreshing = false
            }

            firstProfileWrapper.setOnClickListener {
                Log.e("tjdrnr", "" + BaseApplication.shared.getSharedPreferences().getBlockUser())
                Log.e("tjdrnr", "" + pickTop[0].category_owner_id.toString())
                if (BaseApplication.shared.getSharedPreferences().getBlockUser()
                        ?.contains(pickTop[0].category_owner_id.toString()) == true
                ) {
                    activity?.alert(
                        getString(R.string.Block_alert_user),
                        getString(R.string.Notification)
                    ) {}
                } else {
                    val action =
                        NavigationDirections.actionGlobalProfileFragment(pickTop[0].category_owner_id)
                    it.findNavController().navigate(action)
                }

            }

            secondProfileWrapper.setOnClickListener {
                Log.e("tjdrnr", "" + BaseApplication.shared.getSharedPreferences().getBlockUser())
                if (BaseApplication.shared.getSharedPreferences().getBlockUser()
                        ?.contains(pickTop[1].category_owner_id.toString()) == true
                ) {
                    activity?.alert(
                        getString(R.string.Block_alert_user),
                        getString(R.string.Notification)
                    ) {}
                } else {
                    val action =
                        NavigationDirections.actionGlobalProfileFragment(pickTop[1].category_owner_id)
                    it.findNavController().navigate(action)
                }
            }

            BaseApplication.shared.getSharedPreferences().getUser()?.let {

                var accessToken = BaseApplication.shared.getSharedPreferences().getAccessToken()
                Log.e("성국", "Bearer $accessToken")

                bookMarkAdapter = TalkBookmarkAdapter(activity, this.bookmark)
                bookMarkView = view.findViewById(R.id.rvBookMark)
                bookMarkView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                bookMarkView.adapter = bookMarkAdapter

            } ?: run {

                bookmarkLayout.visibility = View.GONE

            }
            UserLoader.shared.getUserBookmarkTalk { bookmark ->
                this.bookmark = bookmark.toMutableList()
                if (this.bookmark.size > 0) {
                    bookmarkLayout.visibility = View.VISIBLE
                } else {
                    bookmarkLayout.visibility = View.GONE
                }
            }

            tvBookMark.setOnClickListener {
                val action = NavigationDirections.actionGlobalBookmarkFragment()
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(action)
            }


            tvOrganization.setOnClickListener {
                val action = NavigationDirections.actionGlobalOrganizationFragment()
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(action)
            }

            dramaAdapter = TalkTodayAdapter(activity, this.talk)

            rvDrama.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvDrama.adapter = dramaAdapter

            popularAdapter = TalkPopularAdapter(activity, this.popular)
            rvPopular.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvPopular.adapter = popularAdapter

            rvRecommend = view.findViewById(R.id.rvRecommend)
            recommendAdapter = TalkRecommendAdapter(activity, this.recommend)
            rvRecommend.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvRecommend.adapter = recommendAdapter

            database.getReference("event").addValueEventListener(eventListener)

            database.getReference("event").get().addOnSuccessListener { event ->

                val now: Int = SimpleDateFormat("yyyyMMdd").format(Date()).toInt()
                val sharedNow =
                    BaseApplication.shared.getSharedPreferences()
                        .getInt("notice", 0)
                if (now > sharedNow) {
                    if (eventModel.size > 0) {
                        eventModel[0].banner?.let {
                            val noticeTF =
                                BaseApplication.shared.getSharedPreferences()
                                    .getBoolean("noticeTF", false)
                            if (!noticeTF) {
                                BaseApplication.shared.getSharedPreferences().edit().putBoolean(
                                    "noticeTF",
                                    true
                                ).apply()
                                activity?.notice(it) {
                                    val notOpenToday: Int =
                                        SimpleDateFormat("yyyyMMdd").format(Date()).toInt()

                                    BaseApplication.shared.getSharedPreferences().edit()
                                        .putInt("notice", notOpenToday).apply()
                                }
                            }

                        }
                    }
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        }
    }

    override fun fetchModel() {
        super.fetchModel()

        TalkLoader.shared.getTalkList {
            this.talk = it.toMutableList()
            val talkfilter =
                this.talk.filter { model -> model.live == 2 }.sortedBy { data -> data.talk_count }
                    .reversed()
            this.talk = talkfilter.toMutableList()
            dramaAdapter.items = this.talk
            dramaAdapter.notifyDataSetChanged()

        }

        AdLoader.shared.getAdList(true, AdModel.Location.talk) { ad ->
            this.ad = ad
            bannerAdapter.items = this.ad
            bannerAdapter.notifyDataSetChanged()

        }

        UserLoader.shared.getUserBookmarkTalk { bookmark ->
            this.bookmark = bookmark.toMutableList()
            bookMarkAdapter.items = this.bookmark
            bookMarkAdapter.notifyDataSetChanged()

        }

//        startLoadingUI()

        Handler(Looper.getMainLooper()).postDelayed({

            SearchLoader.shared.searchTalk("") { it ->

                category = it.toMutableList()

                database.getReference("mainCategoryList").child("popularList").get()
                    .addOnSuccessListener {
                        it.value?.let { value ->
                            popular.clear()
                            popularList.clear()
                            for (data in it.children) {
                                val modelResult = data.getValue(Int::class.java)
                                popularList.add(modelResult!!)
                            }
                            for (i in popularList.indices) {
                                popular.addAll(category.filter { it.id == popularList[i] }
                                    .toMutableList())
                            }

                            popularAdapter.items = popular
                            popularAdapter.notifyDataSetChanged()

                        }

                    }.addOnFailureListener {
                        Log.e("firebase", "Error getting data", it)
                    }

                database.getReference("mainCategoryList").child("recommendList").get()
                    .addOnSuccessListener {
                        it.value?.let { value ->
                            recommend.clear()
                            recommendList.clear()
                            for (data in it.children) {
                                val modelResult = data.getValue(Int::class.java)
                                recommendList.add(modelResult!!)
                            }
                            for (i in recommendList.indices) {
                                recommend.addAll(category.filter { it.id == recommendList[i] }
                                    .toMutableList())
                            }

                            recommendAdapter.items = recommend
                            recommendAdapter.notifyDataSetChanged()

                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            constraintLayout.visibility = View.VISIBLE
                        }

                    }.addOnFailureListener {
                        Log.e("firebase", "Error getting data", it)
                    }


            }

        }, 100) // 0.5초 정도 딜레이를 준 후 시작


        val day = Calendar.getInstance()
        day.add(Calendar.DATE, -1)
        var beforeDate: String = SimpleDateFormat("yyMMdd").format(day.time)

        ymd_date = beforeDate

        PickTopLoader.shared.getPickTop(ymd_date) { values ->

            pickTop = values.toMutableList()
            if (pickTop.size > 0) {

                Glide.with(this).load(pickTop[0].profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .override(Target.SIZE_ORIGINAL)
                    .fallback(R.drawable.bg_profile_original)
                    .into(ivFirstProfile)
                tvFirstSympathyName.text = pickTop[0].nickname

                if (pickTop.size >= 2) {
                    layoutSecondSympathy.visibility = View.VISIBLE
                    Glide.with(this).load(pickTop[1].profile_image_url)
                        .transform(CenterCrop(), CircleCrop())
                        .override(Target.SIZE_ORIGINAL)
                        .fallback(R.drawable.bg_profile_original)
                        .into(ivSecondProfile)
                    tvSecondSympathyName.text = pickTop[1].nickname
                }

            } else {
                day.add(Calendar.DATE, -1)
                beforeDate = SimpleDateFormat("yyMMdd").format(day.time)

                ymd_date = beforeDate
                PickTopLoader.shared.getPickTop(ymd_date) { values ->
                    pickTop = values.toMutableList()
                    Glide.with(this).load(pickTop[0].profile_image_url)
                        .transform(CenterCrop(), CircleCrop())
                        .override(Target.SIZE_ORIGINAL)
                        .fallback(R.drawable.bg_profile_original)
                        .into(ivFirstProfile)
                    tvFirstSympathyName.text = pickTop[0].nickname

                    if (pickTop.size >= 2) {
                        layoutSecondSympathy.visibility = View.VISIBLE
                        Glide.with(this).load(pickTop[1].profile_image_url)
                            .transform(CenterCrop(), CircleCrop())
                            .override(Target.SIZE_ORIGINAL)
                            .fallback(R.drawable.bg_profile_original)
                            .into(ivSecondProfile)
                        tvSecondSympathyName.text = pickTop[1].nickname
                    }
                }
            }
        }

    }


//    fun sort(talk: MutableList<TalkModel>): MutableList<TalkModel> {
//        val onair = talk.filter { it.onAir }.sortedBy { data -> data.talk_count }.reversed()
//        val standby = talk.filter { !it.onAir }.sortedBy { data -> data.talk_count }.reversed()
//
//        var sorted = onair.toMutableList()
//        sorted.addAll(standby)
//
//        return sorted
//    }
}


fun makeTalkModel(payload: String): TalkModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, TalkModel::class.java)
}

fun makeMentionModel(payload: String): MentionModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, MentionModel::class.java)
}