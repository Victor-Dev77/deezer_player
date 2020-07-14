package fr.esgi.deezerplayer.features

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.api.TrackAPI
import fr.esgi.deezerplayer.data.model.musicplayer.*
import fr.esgi.deezerplayer.data.repository.TrackRepository
import fr.esgi.deezerplayer.databinding.ActivityMainBinding
import fr.esgi.deezerplayer.features.notification.CreateNotification
import fr.esgi.deezerplayer.features.view.albumlist.AlbumListFragmentDirections
import fr.esgi.deezerplayer.util.Coroutines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

lateinit var mainBinding: ActivityMainBinding

class MainActivity : AppCompatActivity(), PlayerStateListener,
    SlidingUpPanelLayout.PanelSlideListener {

    private lateinit var playerPanel: SlidingUpPanelLayout
    private lateinit var playerBar: LinearLayout
    private lateinit var playBtnBar: ImageButton
    private lateinit var pauseBtnBar: ImageButton
    private lateinit var playBtnPlayer: ImageButton
    private lateinit var pauseBtnPlayer: ImageButton
    private lateinit var seekBarAudio: AppCompatSeekBar
    private lateinit var currentPositionPlay: TextView
    private var mUserIsSeeking = false
    private val player = Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        //DEEPLINK
        //TODO: mettre intent ici + creer singleton + mettre traitement / nav dans albumListFragment a onCreate
        val data: Uri? = intent?.data
        Log.d("toto", "deeplink: " + data?.getQueryParameter("album") + " - track: " + data?.getQueryParameter("track"))
        data?.let {
            try {
                val valueParamAlbum = (it.getQueryParameter("album"))?.toInt()
                val valueParamTrack = (it.getQueryParameter("track"))?.toInt()
                valueParamAlbum?.let {
                    val api = TrackAPI()
                    val repository = TrackRepository(api)
                    Coroutines.ioThenMain(
                        { repository.getAlbum(it) },
                        { albumRes ->
                            Log.d("toto", "album: $albumRes")
                            albumRes?.let { album ->
                                val navHostFragment =
                                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                                val navController: NavController = navHostFragment.navController
                                navController.navigate(
                                    AlbumListFragmentDirections.actionAlbumListFragmentToAlbumDetailFragment(
                                        album, valueParamTrack ?: 0
                                    )
                                )
                            }
                        } // callback
                    )
                }
            } catch (e: Exception) {
                Log.d("toto", "ERROR: parameter deeplink not cast to int")
            }
        }


        // NOTIFICATION
        CreateNotification.createChannel(this)
        CreateNotification.registerReceiver(this)

        playerPanel = findViewById(R.id.player_sliding)
        playerPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN // caché player bar

        seekBarAudio = findViewById(R.id.player_content_seekbar)
        currentPositionPlay = findViewById(R.id.player_content_current_position)
        playBtnBar = findViewById(R.id.player_btn_play)
        playBtnPlayer = findViewById(R.id.player_content_btn_play)
        pauseBtnBar = findViewById(R.id.player_btn_pause)
        pauseBtnPlayer = findViewById(R.id.player_content_btn_pause)
        playerBar = findViewById(R.id.player_bar)
        playerPanel.addPanelSlideListener(this@MainActivity)

        // Click Listener
        playBtnBar.setOnClickListener { player.onTrackPlay() }
        playBtnPlayer.setOnClickListener { player.onTrackPlay() }
        pauseBtnBar.setOnClickListener { player.onTrackPause() }
        pauseBtnPlayer.setOnClickListener { player.onTrackPause() }
        findViewById<ImageButton>(R.id.player_btn_forward).setOnClickListener { nextTrack() }
        findViewById<ImageButton>(R.id.player_content_btn_forward).setOnClickListener { nextTrack() }
        findViewById<ImageButton>(R.id.player_btn_rewind).setOnClickListener { previousTrack() }
        findViewById<ImageButton>(R.id.player_content_btn_rewind).setOnClickListener { previousTrack() }

        player.setPlayerStateListener(this)
        player.setContext(this)

        // gere back button -> si player ouvert plein ecran -> le reduit au lieu de retour arriere
        onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (playerPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    // reduit UI Player
                    playerPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                } else {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val navController: NavController = navHostFragment.navController
                    Log.d("toto", "name nav: " + navController.currentDestination?.id)
                    if (navController.currentDestination?.id == R.id.albumDetailFragment)
                        navController.popBackStack()
                    else
                        finish()
                }
            }
        })

        initializeSeekBar()
    }

    private fun initializeSeekBar() {
        seekBarAudio.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                var userSelectedPosition = 0
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    mUserIsSeeking = true
                }

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        userSelectedPosition = progress
                        currentPositionPlay.text = progress.toString()
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    mUserIsSeeking = false
                    player.seekTo(userSelectedPosition)
                }
            })
    }

    private fun nextTrack() {
        val track = player.onTrackNext()
        if (track != null) {
            mainBinding.track = track
        }
    }

    private fun previousTrack() {
        val track = player.onTrackPrevious()
        if (track != null) {
            mainBinding.track = track
        }
    }

    override fun onStateChanged(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> updateUIPlayingTrack(true)
            PlayerState.PAUSED -> updateUIPlayingTrack(false)
            PlayerState.RESET -> {
                updateUIPlayingTrack(true)
                releasePlayer()
            }
            else -> Log.d("toto", state.name)
        }
    }

    override fun onTrackFinished() {
        updateUIPlayingTrack(false)
        nextTrack()
    }

    override fun onDurationChanged(duration: Int) {
        seekBarAudio.max = duration
    }

    override fun onPositionChanged(position: Int) {
        if (!mUserIsSeeking) {
            seekBarAudio.setProgress(position, true)
            CoroutineScope(Dispatchers.Main).launch {
                currentPositionPlay.text = player.currentPosition.toString()
            }
        }
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {}

    override fun onPanelStateChanged(
        panel: View?,
        previousState: SlidingUpPanelLayout.PanelState?,
        newState: SlidingUpPanelLayout.PanelState?
    ) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            playerBar.visibility = View.GONE
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            playerBar.visibility = View.VISIBLE
        }
    }

    fun updatePlayerUI() {
        playerPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED // affiché player bar
    }

    private fun releasePlayer() {
        playerPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN // caché player bar
    }

    fun updateUIPlayingTrack(isPlaying: Boolean) {
        // Hide Play Btn
        playBtnBar.visibility = if (isPlaying) View.GONE else View.VISIBLE
        playBtnPlayer.visibility = if (isPlaying) View.GONE else View.VISIBLE
        // Show Pause Btn
        pauseBtnBar.visibility = if (isPlaying) View.VISIBLE else View.GONE
        pauseBtnPlayer.visibility = if (isPlaying) View.VISIBLE else View.GONE

        if (isPlaying) {
            val track = Player.getCurrentTrack()
            if (track != null)
                mainBinding.track = track
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateNotification.destroy()
        }
        CreateNotification.unregisterReceiver(this)
    }
}
