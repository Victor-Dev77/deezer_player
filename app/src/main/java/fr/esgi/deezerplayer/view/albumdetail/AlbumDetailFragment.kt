package fr.esgi.deezerplayer.view.albumdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import fr.esgi.deezerplayer.data.repositories.TrackRepository
import fr.esgi.deezerplayer.databinding.AlbumDetailFragmentBinding
import fr.esgi.deezerplayer.util.loadImage
import fr.esgi.deezerplayer.view.RVClickListener
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModel
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModelFactory
import kotlinx.android.synthetic.main.album_detail_fragment.*

class AlbumDetailFragment : Fragment(), RVClickListener, SlidingUpPanelLayout.PanelSlideListener {

    private lateinit var factory: AlbumDetailViewModelFactory
    private lateinit var viewModel: AlbumDetailViewModel
    private lateinit var binding: AlbumDetailFragmentBinding

    // recupere parametres envoyé dans la navigation
    private val args by navArgs<AlbumDetailFragmentArgs>()

    private lateinit var playerPanel: SlidingUpPanelLayout
    private lateinit var playerBar: LinearLayout


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
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

            playerBar = findViewById(R.id.player_bar)
            playerPanel = findViewById(R.id.player_sliding)
            playerPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN // caché player bar
            playerPanel.addPanelSlideListener(this@AlbumDetailFragment)
        }

        loadImage(
            binding.albumDetailCover,
            args.albumItem.cover,
            binding.albumDetailShimmer
        )

    }

    override fun <Track> onRecyclerViewItemClick(view: View, data: Track) {
        val track = data as fr.esgi.deezerplayer.data.model.Track
        binding.track = track
        // rootview car param view est def dans TrackAdapter et est view du recyclerview
        // donc peut pas acceder aux vues de la cover, title, etc
        val viewRoot = player_sliding.rootView
        updatePlayerUI(viewRoot, track)
    }

    private fun updatePlayerUI(view: View, track: Track) {
        playerPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED // affiché player bar
        loadImage(
            view.findViewById(R.id.player_content_cover),
            args.albumItem.cover,
            view.findViewById(R.id.player_content_shimmer)
        )
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

}
