package fr.esgi.deezerplayer.view.albumdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.api.TrackAPI
import fr.esgi.deezerplayer.data.model.Track
import fr.esgi.deezerplayer.data.model.musicplayer.Player
import fr.esgi.deezerplayer.data.model.musicplayer.PlayerState
import fr.esgi.deezerplayer.data.model.musicplayer.PlayerStateListener
import fr.esgi.deezerplayer.data.repositories.TrackRepository
import fr.esgi.deezerplayer.databinding.AlbumDetailFragmentBinding
import fr.esgi.deezerplayer.util.loadImage
import fr.esgi.deezerplayer.view.RVClickListener
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModel
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModelFactory
import kotlinx.android.synthetic.main.album_detail_fragment.*

class AlbumDetailFragment : Fragment(), PlayerStateListener, RVClickListener, SlidingUpPanelLayout.PanelSlideListener {

    private lateinit var factory: AlbumDetailViewModelFactory
    private lateinit var viewModel: AlbumDetailViewModel
    private lateinit var binding: AlbumDetailFragmentBinding

    // recupere parametres envoyé dans la navigation
    private val args by navArgs<AlbumDetailFragmentArgs>()

    private lateinit var playerPanel: SlidingUpPanelLayout
    private lateinit var playerBar: LinearLayout
    private lateinit var playBtnBar: ImageButton
    private lateinit var pauseBtnBar: ImageButton
    private lateinit var playBtnPlayer: ImageButton
    private lateinit var pauseBtnPlayer: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflate avec DataBindingUtil
        val viewBinding = DataBindingUtil.inflate<AlbumDetailFragmentBinding>(
            inflater,
            R.layout.album_detail_fragment,
            container,
            false
        )
        binding = viewBinding
        // Set variable dans XML (album) par valeur (albumItem)
        viewBinding.album = args.albumItem
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val api = TrackAPI()
        val repository = TrackRepository(api)
        factory =
            AlbumDetailViewModelFactory(
                repository
            )
        viewModel = ViewModelProviders.of(this, factory).get(AlbumDetailViewModel::class.java)

        // Recupère les tracks (api request)
        viewModel.getTracks(args.albumItem.id)

        viewModel.tracks.observe(viewLifecycleOwner, Observer { tracks ->
            // tracks_rcv = id recyclerview dans XML <=> comme findviewbyid
            tracks_rcv.also {
                // init recyclerview
                it.layoutManager = LinearLayoutManager(requireContext())
                // desactive scroll recyclerview donc scroll ce fait avec
                // NestedScrollView dans XML => permet de scroller toute la vue et pas seulement les tracks
                it.isNestedScrollingEnabled = false
                it.addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                )
                it.setHasFixedSize(true)
                it.adapter = TrackAdapter(tracks, this)
            }
        })

        viewModel.playerAdapter.setPlayerStateListener(this@AlbumDetailFragment)

        viewModel.currentTrack.observe(viewLifecycleOwner, Observer { track ->
            binding.track = track
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

            playBtnBar = findViewById(R.id.player_btn_play)
            playBtnPlayer = findViewById(R.id.player_content_btn_play)
            pauseBtnBar = findViewById(R.id.player_btn_pause)
            pauseBtnPlayer = findViewById(R.id.player_content_btn_pause)
            playerBar = findViewById(R.id.player_bar)
            playerPanel = findViewById(R.id.player_sliding)
            playerPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN // caché player bar
            playerPanel.addPanelSlideListener(this@AlbumDetailFragment)

            // Click Listener
            playBtnBar.setOnClickListener { viewModel.playerAdapter.play() }
            playBtnPlayer.setOnClickListener { viewModel.playerAdapter.play() }
            pauseBtnBar.setOnClickListener { viewModel.playerAdapter.pause() }
            pauseBtnPlayer.setOnClickListener { viewModel.playerAdapter.pause() }
        }

        loadImage(
            binding.albumDetailCover,
            args.albumItem.cover,
            binding.albumDetailShimmer
        )

    }

    override fun onTrackFinished() {
        Toast.makeText(requireContext(), "track finish", Toast.LENGTH_SHORT).show()
        updateUIPlayingTrack(false)
        //passer a track suivante
        viewModel.nextTrack()
    }

    override fun onStateChanged(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> updateUIPlayingTrack(true)
            PlayerState.PAUSED -> updateUIPlayingTrack(false)
            else -> Log.d("toto", state.name)
        }
    }

    override fun <Track> onRecyclerViewItemClick(view: View, data: Track) {
        val track = data as fr.esgi.deezerplayer.data.model.Track
        binding.track = track
        viewModel.setCurrentTrack(track)
        // rootview car param view est def dans TrackAdapter et est view du recyclerview
        // donc peut pas acceder aux vues de la cover, title, etc
        val viewRoot = playerPanel.rootView
        updatePlayerUI(viewRoot, track)
        updateUIPlayingTrack(false)
        viewModel.playerAdapter.loadTrack(track.song)
    }

    private fun updatePlayerUI(view: View, track: Track) {
        playerPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED // affiché player bar
        loadImage(
            view.findViewById(R.id.player_content_cover),
            args.albumItem.cover,
            view.findViewById(R.id.player_content_shimmer)
        )
    }

    private fun updateUIPlayingTrack(isPlaying: Boolean) {
        // Hide Play Btn
        playBtnBar.visibility = if (isPlaying) View.GONE else View.VISIBLE
        playBtnPlayer.visibility = if (isPlaying) View.GONE else View.VISIBLE
        // Show Pause Btn
        pauseBtnBar.visibility = if (isPlaying) View.VISIBLE else View.GONE
        pauseBtnPlayer.visibility = if (isPlaying) View.VISIBLE else View.GONE
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        //Log.d("toto", "offset: " + slideOffset)
        // TODO: ici - utiliser CollapsedToolBar OU en fonction val offset set visibility player bar avec anim fadeIn
    }

    override fun onPanelStateChanged(
        panel: View?,
        previousState: SlidingUpPanelLayout.PanelState?,
        newState: SlidingUpPanelLayout.PanelState?
    ) {
        // LE FAIRE DANS OnPanelSlide pour faire effet anim car la trop brusque
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            playerBar.visibility = View.GONE
        }
        else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            playerBar.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        if (requireActivity().isChangingConfigurations && viewModel.playerAdapter.isPlaying()) {
            Log.d("toto", "onStop: don't release MediaPlayer as screen is rotating & playing")
        } else {
            viewModel.playerAdapter.release()
            Log.d("toto", "onStop: release MediaPlayer")
        }
    }

}
