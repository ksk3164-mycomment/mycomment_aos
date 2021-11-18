package kr.beimsupicures.mycomment.controllers.main

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
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
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getPlayPause
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.setCurrentPosition
import kr.beimsupicures.mycomment.extensions.setPlayPause

private const val EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL"
private const val EXTRA_PLAYBACK_POSITION_MS = "EXTRA_PLAYBACK_POSITION_MS"
private const val EXTRA_PLAY_PAUSE = "EXTRA_PLAY_PAUSE"

private const val STATE_PLAYBACK_POSITION_MS = "STATE_PLAYBACK_POSITION_MS"

class FullScreenVideoActivity : AppCompatActivity() {

    companion object {
        fun newIntent(
            packageContext: Context,
            videoUrl: String,
            playbackPositionMs: Long
        ): Intent {
            val intent =
                Intent(packageContext, FullScreenVideoActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_URL, videoUrl)
            intent.putExtra(EXTRA_PLAYBACK_POSITION_MS, playbackPositionMs)
            return intent
        }
    }

    lateinit var fullscreenButton: ImageView
    lateinit var layoutFullscreen: ConstraintLayout
    lateinit var layoutPlayPause: ConstraintLayout
    private lateinit var player: SimpleExoPlayer

    lateinit var volumeControl: SeekBar
    lateinit var audioManager: AudioManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_video)

        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
        var playbackPositionMs = intent.getLongExtra(EXTRA_PLAYBACK_POSITION_MS, 0)
        val playPause = BaseApplication.shared.getSharedPreferences().getPlayPause()
        if (savedInstanceState != null) {
            // The user rotated the screen
            playbackPositionMs = savedInstanceState.getLong(STATE_PLAYBACK_POSITION_MS)
        }

        fullscreenButton = player_view.findViewById(R.id.exo_fullscreen_icon)
        layoutFullscreen = player_view.findViewById(R.id.layout_fullscreen)
        layoutPlayPause = player_view.findViewById(R.id.layout_play_pause)

        fullscreenButton.setImageResource(R.drawable.ic_portraitscreen)

        layoutFullscreen.setOnClickListener {
            finish()
        }

        volumeControl = findViewById(R.id.volume_control)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

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

        val playerView: PlayerView = findViewById(R.id.player_view)
        player = ExoPlayerFactory.newSimpleInstance(this)
//        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
//        val dataSourceFactory = DefaultDataSourceFactory(this, userAgent)
//        val mediaSource: MediaSource =
//            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUrl))
        player.prepare(buildMediaSource(Uri.parse(videoUrl)))
        player.seekTo(playbackPositionMs)
        Log.e("tjdrnr", "playpause" + playPause)

        player.playWhenReady = playPause
        playerView.player = player

        player.addListener(object : Player.EventListener {

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

    override fun onPause() {
        super.onPause()

        BaseApplication.shared.getSharedPreferences().setCurrentPosition(player.currentPosition)
        BaseApplication.shared.getSharedPreferences().setPlayPause(player.isPlaying)

        player.playWhenReady = false
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent: String = Util.getUserAgent(applicationContext, "blackJin")
        return if (uri.lastPathSegment!!.contains("mp3") || uri.lastPathSegment!!.contains("mp4")) {
            ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else if (uri.lastPathSegment!!.contains("m3u8")) {

            //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else {
            ExtractorMediaSource.Factory(DefaultDataSourceFactory(applicationContext, userAgent))
                .createMediaSource(uri)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        player.release()
    }

//    private fun initializePlayer() {
//        if (player == null) {
//            player = ExoPlayerFactory.newSimpleInstance(requireContext())
//
//            //플레이어 연결
//            player_view.player = player
//        }
//        val sample =
//            "https://myco-original.s3.ap-northeast-2.amazonaws.com/dr/output/drstout.m3u8"
//        val mediaSource: MediaSource = buildMediaSource(Uri.parse(sample))
//
//        //prepare
//        player.prepare(mediaSource, true, false)
//
//
//        //start,stop
//        player.playWhenReady = playWhenReady
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(STATE_PLAYBACK_POSITION_MS, player.currentPosition)
    }


    var handler = Handler()

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {

            KeyEvent.KEYCODE_VOLUME_DOWN -> {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                    volumeControl.setProgress(volumeControl.progress - 1, true)

                    if (!layout_volume.isVisible) {
                        layout_volume.visibility = View.VISIBLE
                        handler.postDelayed({
                            layout_volume.visibility = View.INVISIBLE
                        }, 3000)
                    }
                }


            }

            KeyEvent.KEYCODE_VOLUME_UP -> {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    volumeControl.setProgress(volumeControl.progress + 1, true)

                    if (!layout_volume.isVisible) {
                        layout_volume.visibility = View.VISIBLE
                        handler.postDelayed({
                            layout_volume.visibility = View.INVISIBLE
                        }, 3000)
                    }
                }

            }

            KeyEvent.KEYCODE_BACK -> {
                onBackPressed()
            }
        }
        return true
    }
}