package kr.beimsupicures.mycomment.controllers.main.talk

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
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
import kotlinx.android.synthetic.main.activity_full_video.*
import kotlinx.android.synthetic.main.fragment_time_line_talk.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.controllers.main.FullScreenVideoActivity
import kr.beimsupicures.mycomment.extensions.getCurrentPosition
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.removeKey


class TimeLineTalkFragment : BaseFragment() {

    lateinit var player_view: PlayerView
    lateinit var fullscreenButton: ImageView
    lateinit var layoutFullscreen: ConstraintLayout

    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    val sample ="https://myco-original.s3.ap-northeast-2.amazonaws.com/test/IMG_0118test.m3u8"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_line_talk, container, false)
    }

    override fun onResume() {
        super.onResume()

        val currentPosition = BaseApplication.shared.getSharedPreferences().getCurrentPosition()
        Log.e("tjdrnr", "currentPosition" + currentPosition)
        if (currentPosition!=0L) {
            currentPosition?.let {
                player?.seekTo(it)
                player?.playWhenReady = true
            }
            BaseApplication.shared.getSharedPreferences().removeKey("currentPosition")
        } else {
            initializePlayer()
        }


    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            player_view = view.findViewById(R.id.player_view)

            fullscreenButton = view.player_view.findViewById(R.id.exo_fullscreen_icon)
            layoutFullscreen = player_view.findViewById(R.id.layout_fullscreen)

//            layoutFullscreen.setOnClickListener {
//
//
//                player?.playWhenReady = false // pause current video if it's playing
//                startActivity(
//                    FullScreenVideoActivity.newIntent(
//                        requireContext(),
//                        sample,
//                        player?.currentPosition!!
//                    )
//                )
//            }



            player?.addListener(object : Player.EventListener {
                /**
                 * @param playWhenReady - Whether playback will proceed when ready.
                 * @param playbackState - One of the STATE constants.
                 */
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            /**
                             * The player does not have any media to play.
                             */
                        }
                        Player.STATE_BUFFERING -> {
                            /**
                             * The player is not able to immediately play from its current position. This state typically
                             * occurs when more data needs to be loaded.
                             */
                        }
                        Player.STATE_READY -> {
                            /**
                             * The player is able to immediately play from its current position. The player will be playing if
                             * {@link #getPlayWhenReady()} is true, and paused otherwise.
                             */
                        }
                        Player.STATE_ENDED -> {
                            /**
                             * The player has finished playing the media.
                             */
                        }
                        else -> {
                        }
                    }
                }
            })

        }
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
            player_view.player = player
        }

        val mediaSource: MediaSource = buildMediaSource(Uri.parse(sample))

        //prepare
        player!!.prepare(mediaSource, true, false)

        //start,stop
        player!!.playWhenReady = playWhenReady
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player_view.player = null
            player!!.release()
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
}