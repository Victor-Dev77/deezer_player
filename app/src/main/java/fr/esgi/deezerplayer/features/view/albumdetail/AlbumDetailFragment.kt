package fr.esgi.deezerplayer.features.view.albumdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.api.TrackAPI
import fr.esgi.deezerplayer.data.model.Track
import fr.esgi.deezerplayer.data.repository.TrackRepository
import fr.esgi.deezerplayer.databinding.AlbumDetailFragmentBinding
import fr.esgi.deezerplayer.util.BindingUtils
import fr.esgi.deezerplayer.features.BaseViewModelFactory
import fr.esgi.deezerplayer.features.MainActivity
import fr.esgi.deezerplayer.features.RVClickListener
import fr.esgi.deezerplayer.features.mainBinding
import kotlinx.android.synthetic.main.album_detail_fragment.*

class AlbumDetailFragment : Fragment(),
    RVClickListener {

    private lateinit var viewModel: AlbumDetailViewModel

    // recupere parametres envoyé dans la navigation
    private val args by navArgs<AlbumDetailFragmentArgs>()

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
        // Set variable dans XML (album) par valeur (albumItem)
        viewBinding.album = args.albumItem
        viewBinding.handler = BindingUtils()
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val api = TrackAPI()
        val repository = TrackRepository(api)
        val factory = BaseViewModelFactory { AlbumDetailViewModel(this@AlbumDetailFragment.requireContext(), repository) }
        viewModel = ViewModelProviders.of(this, factory).get(AlbumDetailViewModel::class.java)

        // Recupère les tracks (api request)
        viewModel.getTracks(args.albumItem.id)

        viewModel.tracks.observe(viewLifecycleOwner, Observer { tracks ->
            if (tracks.isEmpty()) {
                no_tracks.visibility = View.VISIBLE
                tracks_rcv.visibility = View.GONE
            } else {
                no_tracks.visibility = View.GONE
                tracks_rcv.visibility = View.VISIBLE
                // tracks_rcv = id recyclerview dans XML <=> comme findviewbyid
                tracks_rcv.also {
                    // init recyclerview
                    it.layoutManager = LinearLayoutManager(requireContext())
                    // desactive scroll recyclerview donc scroll ce fait avec
                    // NestedScrollView dans XML => permet de scroller toute la vue et pas seulement les tracks
                    it.isNestedScrollingEnabled = false
                    it.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                    it.setHasFixedSize(true)
                    it.adapter = TrackAdapter(tracks, this)
                    val trackDeepLink = tracks.find { song -> song.id == args.trackId }
                    trackDeepLink?.let { track ->
                        clickTrack(track)
                    }
                }
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
    }

    private fun clickTrack(track: Track) {
        mainBinding.track = track
        mainBinding.album = args.albumItem
        mainBinding.handler = BindingUtils()
        viewModel.setTrackList(viewModel.tracks.value)
        viewModel.setCurrentTrack(track)
        viewModel.setAlbum(args.albumItem)

        val fragment = activity as MainActivity
        fragment.updatePlayerUI()
        fragment.updateUIPlayingTrack(false)
        viewModel.startPlayer(track, args.albumItem)
    }

    override fun <Track> onRecyclerViewItemClick(view: View, data: Track) {
        val track = data as fr.esgi.deezerplayer.data.model.Track
        clickTrack(track)
    }

}
