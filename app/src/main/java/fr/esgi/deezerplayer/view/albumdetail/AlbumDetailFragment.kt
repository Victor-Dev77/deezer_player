package fr.esgi.deezerplayer.view.albumdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import fr.esgi.deezerplayer.data.repositories.TrackRepository
import fr.esgi.deezerplayer.databinding.AlbumDetailFragmentBinding
import fr.esgi.deezerplayer.util.loadImage
import fr.esgi.deezerplayer.view.RVClickListener
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModel
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModelFactory
import kotlinx.android.synthetic.main.album_detail_fragment.*

class AlbumDetailFragment : Fragment(), RVClickListener {

    private lateinit var factory: AlbumDetailViewModelFactory
    private lateinit var viewModel: AlbumDetailViewModel
    private lateinit var binding: AlbumDetailFragmentBinding

    // recupere parametres envoyé dans la navigation
    private val args by navArgs<AlbumDetailFragmentArgs>()

    private lateinit var playerBar: SlidingUpPanelLayout

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

            playerBar = findViewById(R.id.player_sliding)
            playerBar.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
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
        val viewRoot = player_sliding.rootView
        updatePlayerUI(viewRoot, track)
    }

    private fun updatePlayerUI(view: View, track: Track) {
        playerBar.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        loadImage(
            view.findViewById(R.id.player_content_cover),
            args.albumItem.cover,
            view.findViewById(R.id.player_content_shimmer)
        )
    }

}
