package kr.beimsupicures.mycomment.controllers.main.original

import android.content.ActivityNotFoundException
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import kotlinx.android.synthetic.main.fragment_time_line_talk.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.TalkLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.common.keyboard.showKeyboard
import kr.beimsupicures.mycomment.components.adapters.OriginalChapterAdapter
import kr.beimsupicures.mycomment.components.adapters.onClickChapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI
import kr.beimsupicures.mycomment.controllers.main.FullScreenVideoActivity
import kr.beimsupicures.mycomment.controllers.main.feed.DramaFeedFragment
import kr.beimsupicures.mycomment.controllers.main.talk.RealTimeTalkFragment
import kr.beimsupicures.mycomment.extensions.*

class OriginalDetailFragment : BaseFragment(), onClickChapter {

    var original: OriginalModel? = null
    var originalDetail: MutableList<OriginalDetailModel>? = mutableListOf()
    var talk: TalkModel? = null

    val database = FirebaseDatabase.getInstance()

    lateinit var titleLabel: TextView
    lateinit var descLabel: TextView
    lateinit var contentLabel: TextView
    lateinit var layoutPlay: LinearLayout
    lateinit var ivContentImage: ImageView
    lateinit var playerView: PlayerView
    lateinit var rvChapter: RecyclerView
    lateinit var volumeControl: SeekBar

    lateinit var ivShare: ImageView

    lateinit var audioManager: AudioManager

    lateinit var originalChapterAdapter: OriginalChapterAdapter

    lateinit var messageWrapperView: LinearLayout
    lateinit var floatingButton: FloatingActionButton

    lateinit var btnSend: ImageView
    lateinit var messageField: EditText

    val validation: Boolean
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }

    lateinit var viewModel: MyViewModel

    lateinit var fullscreenButton: ImageView
    lateinit var layoutFullscreen: ConstraintLayout
    lateinit var layoutPlayPause: ConstraintLayout
    lateinit var layoutVolume: ConstraintLayout

    private lateinit var viewPager: ViewPager2
    lateinit var tabLayouts: TabLayout
    private var tabTextList = arrayListOf<String>()
//    private val tabTextList = arrayListOf(activity?.getString(R.string.realtimetalk),activity?.getString(R.string.DramaFeed))

    private var player: SimpleExoPlayer? = null
    var playTF = false

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    var sample = "https://myco-original.s3.ap-northeast-2.amazonaws.com/test/IMG_0118test.m3u8"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java) }!!
        viewModel.getReply.observe(viewLifecycleOwner, EventObserver { t ->
            messageField.setText(t)
            showKeyboard(requireActivity(), messageField)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_original_detail, container, false)
    }

    override fun onResume() {
        super.onResume()


        if (playTF) {
            val currentPosition = BaseApplication.shared.getSharedPreferences().getCurrentPosition()
            if (currentPosition != 0L) {
                currentPosition?.let {
                    player?.seekTo(it)
                    val playPause = BaseApplication.shared.getSharedPreferences().getPlayPause()
                    player?.playWhenReady = playPause
                    BaseApplication.shared.getSharedPreferences().removeKey("playPause")
                }
                BaseApplication.shared.getSharedPreferences().removeKey("currentPosition")
            }
        }
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            ivShare = view.findViewById(R.id.ivShare)

            titleLabel = view.findViewById(R.id.titleLabel)
            descLabel = view.findViewById(R.id.descLabel)
            layoutPlay = view.findViewById(R.id.layout_play)
            ivContentImage = view.findViewById(R.id.ivContentImage)
            playerView = view.findViewById(R.id.player_view)
            contentLabel = view.findViewById(R.id.contentLabel)
            rvChapter = view.findViewById(R.id.rvChapter)
            volumeControl = view.findViewById(R.id.volume_control)

            audioManager = activity?.getSystemService(AUDIO_SERVICE) as AudioManager
            activity?.getString(R.string.realtimetalk)?.let { tabTextList.add(it) }
            activity?.getString(R.string.DramaFeed)?.let { tabTextList.add(it) }


            TalkLoader.shared.getTalk(original?.talk_id!!) {
                talk = it
                viewPager.adapter = CustomFragmentStateAdapter(talk!!, this)
                TabLayoutMediator(tabLayouts, viewPager) { tab, position ->
                    tab.text = tabTextList[position]
                }.attach()
                viewPager.isUserInputEnabled = false

                viewPager.apply {
                    (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                }
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (position == 0) {
                            messageWrapperView.visibility = View.VISIBLE
                            floatingButton.visibility = View.GONE
                        } else {
                            messageField.setText("")
                            messageWrapperView.visibility = View.GONE
                            floatingButton.visibility = View.VISIBLE
                        }
                    }

                })
            }


            volumeControl.max = audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volumeControl.progress = audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC)

            volumeControl.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })



            originalChapterAdapter = OriginalChapterAdapter(activity, originalDetail!!, this)
            rvChapter = view.findViewById(R.id.rvChapter)
            rvChapter.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvChapter.adapter = originalChapterAdapter

            tabLayouts = view.findViewById(R.id.tabLayout)
            viewPager = view.findViewById(R.id.viewPager2)

            messageWrapperView = view.findViewById(R.id.messageWrapperView)
            floatingButton = view.findViewById(R.id.floating_button)

            if (original?.chapter?.size!! > 0) {

                Glide.with(this)
                    .load(original?.chapter!![0].thumbnail_url)
                    .transform(CenterCrop())
                    .override(Target.SIZE_ORIGINAL)
                    .into(ivContentImage)

            }

            layoutPlay.setOnClickListener {
                BaseApplication.shared.getSharedPreferences().getUser()?.let {
                    layoutPlay.visibility = View.GONE
                    ivContentImage.visibility = View.GONE
                    playerView.visibility = View.VISIBLE
                    playTF = true
                    initializePlayer()

                    onStarClicked(
                        database.getReference("original").child("list").child("0").child("chapter")
                            .child("0")
                    )

                } ?: run {
                    activity?.let { activity ->
                        activity.popup(activity.getString(R.string.Doyouwantlogin), activity.getString(R.string.Login)) {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }

            }

            original.let { original ->

                titleLabel.text = original?.title
                descLabel.text = original?.description
                contentLabel.text = original?.content

            }

            fullscreenButton = view.player_view.findViewById(R.id.exo_fullscreen_icon)
            layoutPlayPause = view.player_view.findViewById(R.id.layout_play_pause)
            layoutVolume = view.findViewById(R.id.layout_volume)
            layoutFullscreen = playerView.findViewById(R.id.layout_fullscreen)

            layoutFullscreen.setOnClickListener {
                BaseApplication.shared.getSharedPreferences().setPlayPause(player?.isPlaying!!)
                player?.playWhenReady = false // pause current video if it's playing
                startActivity(
                    FullScreenVideoActivity.newIntent(
                        requireContext(),
                        sample,
                        player?.currentPosition!!
                    )
                )
            }

            floatingButton.setOnClickListener {
                BaseApplication.shared.getSharedPreferences().getUser()?.let {
                    val action = NavigationDirections.actionGlobalDramaFeedWriteFragment()
                    view.findNavController().navigate(action)
                } ?: run {
                    activity?.let { activity ->
                        activity.popup(activity.getString(R.string.Doyouwantlogin), activity.getString(R.string.Login)) {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }
            }

            messageField = view.findViewById(R.id.messageField)
            messageField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    context?.let { context ->
                        btnSend.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                if (validation) R.drawable.send else R.drawable.send_g
                            )
                        )
                    }
                }
            })
            btnSend = view.findViewById(R.id.btnSend)

            btnSend.setOnClickListener {

                BaseApplication.shared.getSharedPreferences().getUser()?.let {

                    when (validation) {
                        true -> {

                            viewModel.setMessage(messageField.text.toString())
                            messageField.setText("")
                            hideKeyboard()

                        }
                        false -> {
                        }
                    }

                } ?: run {

                    activity?.let { activity ->
                        activity.popup(activity.getString(R.string.Doyouwantlogin), activity.getString(R.string.Login)) {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }


            }

            ivShare.setOnClickListener {
                BaseApplication.shared.getSharedPreferences().getUser()?.let { user ->
                    startLoadingUI()
                    val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(getCheckDeepLink()) //정보를 담는 json 사이트를 넣자!!
                        .setDomainUriPrefix("https://mycomment.page.link/")
                        .setAndroidParameters(
                            DynamicLink.AndroidParameters.Builder(activity?.packageName.toString())
                                .build()
                        )
                        .setIosParameters(
                            DynamicLink.IosParameters.Builder("kr.beimsupicures.mycomment")
                                .setAppStoreId("1492390423")
                                .build()
                        )
                        .setNavigationInfoParameters(
                            DynamicLink.NavigationInfoParameters.Builder()
                                .setForcedRedirectEnabled(true)
                                .build()
                        ).setSocialMetaTagParameters(
                            DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("마이코멘트 - 드라마톡")
                                .setDescription(original?.title.toString())
                                .build()
                        )
                        .buildDynamicLink()
                    val dylinkuri = dynamicLink.uri //긴 URI
                    Log.e("tjdrnr", "long uri : $dylinkuri")
                    //짧은 URI사용
                    FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLongLink(dylinkuri)
                        .buildShortDynamicLink()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                stopLoadingUI()
                                val shortLink: Uri? = task.result?.shortLink
                                val flowchartLink: Uri? = task.result?.previewLink
                                Log.e("tjdrnr", "short uri : $shortLink") //짧은 URI
                                Log.e("tjdrnr", "flowchartLink uri : $flowchartLink") //짧은 URI
                                try {
                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.type = "text/plain"
                                    intent.putExtra(
                                        Intent.EXTRA_TEXT,
                                        shortLink.toString()
                                    ) // text는 공유하고 싶은 글자

                                    val chooser = Intent.createChooser(intent, "공유하기")
                                    startActivity(chooser)
                                } catch (e: ActivityNotFoundException) {

                                }

                            } else {
                                Log.e("tjdrnr", "exception" + task.exception.toString())
                            }
                        }
                } ?: run {
                    activity?.let { activity ->
                        activity.popup(activity.getString(R.string.Doyouwantlogin), activity.getString(R.string.Login)) {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }
            }

        }
    }

    override fun loadModel() {
        super.loadModel()
        original = OriginalDetailFragmentArgs.fromBundle(requireArguments()).viewmodel
        Log.e("tjdrnr", "original = " + original)
        original?.talk_id?.let { BaseApplication.shared.getSharedPreferences().setPostTalkId(it) }

        originalDetail = original?.chapter

        Log.e("tjdrnr", "originalDetail = " + originalDetail)

        sample = originalDetail!![0].video_url.toString()

    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent: String = Util.getUserAgent(requireContext(), "blackJin")
        return if (uri.lastPathSegment!!.contains("mp3") || uri.lastPathSegment!!.contains("mp4")) {
            ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else if (uri.lastPathSegment!!.contains("m3u8")) {

            //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else {
            ExtractorMediaSource.Factory(DefaultDataSourceFactory(requireContext(), userAgent))
                .createMediaSource(uri)
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(requireContext())

            //플레이어 연결
            playerView.player = player
        }

        val mediaSource: MediaSource = buildMediaSource(Uri.parse(sample))

        //prepare
        player?.prepare(mediaSource, true, false)

        //start,stop
        player?.playWhenReady = playWhenReady

        player?.addListener(object : Player.EventListener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        Log.e("tjdrnr", "STATE_IDLE")
                    }
                    Player.STATE_BUFFERING -> {
                        layoutPlayPause.visibility = View.INVISIBLE
                    }
                    Player.STATE_READY -> {
                        layoutPlayPause.visibility = View.VISIBLE
                    }
                    Player.STATE_ENDED -> {
                        Log.e("tjdrnr", "STATE_ENDED")
                    }
                    else -> {
                    }
                }
            }
        })
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            playerView.player = null
            player?.release()
            player = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onPause() {

        player?.playWhenReady = false
        super.onPause()
    }

    class CustomFragmentStateAdapter(
        var viewModel: TalkModel,
        fragmentActivity: OriginalDetailFragment
    ) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RealTimeTalkFragment(viewModel)
                else -> DramaFeedFragment(viewModel)
            }
        }
    }

    override fun onClick(chapter: Int) {
        BaseApplication.shared.getSharedPreferences().getUser()?.let {
            sample = originalDetail?.get(chapter)?.video_url.toString()

            onStarClicked(
                database.getReference("original").child("list").child("0").child("chapter")
                    .child(chapter.toString())
            )

            layoutPlay.visibility = View.GONE
            ivContentImage.visibility = View.GONE
            playerView.visibility = View.VISIBLE
            playTF = true
            initializePlayer()

        } ?: run {
            activity?.let { activity ->
                activity.popup(activity.getString(R.string.Doyouwantlogin), activity.getString(R.string.Login)) {
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_signInFragment)
                }
            }
        }
    }

    private fun onStarClicked(database: DatabaseReference) {
        database.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(OriginalDetailModel::class.java)
                    ?: return Transaction.success(mutableData)

                p.view_count = p.view_count + 1

                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                Log.e("tjdrnr", "count update")
            }
        })
    }

    //deeplink uri만들기
    private fun getCheckDeepLink(): Uri {
        // 인증 할 key값 생성 후 넣기
        return Uri.parse("https://mycomment.kr/original?seq=0")
    }
}