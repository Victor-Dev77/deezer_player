package fr.esgi.deezerplayer.view.albumdetail

import android.os.Bundle
import android.util.Log
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
import fr.esgi.deezerplayer.data.repositories.TrackRepository
import fr.esgi.deezerplayer.databinding.AlbumDetailFragmentBinding
import fr.esgi.deezerplayer.view.MainActivity
import fr.esgi.deezerplayer.view.RVClickListener
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModel
import fr.esgi.deezerplayer.view.albumdetail.viewmodel.AlbumDetailViewModelFactory
import fr.esgi.deezerplayer.view.mainBinding
import kotlinx.android.synthetic.main.album_detail_fragment.*

class AlbumDetailFragment : Fragment(), RVClickListener {

    private lateinit var factory: AlbumDetailViewModelFactory
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
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val api = TrackAPI()
        val repository = TrackRepository(api)
        factory =
            AlbumDetailViewModelFactory(
                this@AlbumDetailFragment.requireContext(),
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
        }
        // gere back button -> si player ouvert plein ecran -> le reduit au lieu de retour arriere
       /* activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (playerPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    // reduit UI Player
                    playerPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                } else
                    findNavController().popBackStack()
            }
        })*/
    }

    override fun <Track> onRecyclerViewItemClick(view: View, data: Track) {
        val track = data as fr.esgi.deezerplayer.data.model.Track
        mainBinding.track = track
        mainBinding.album = args.albumItem
        viewModel.setTrackList(viewModel.tracks.value)
        viewModel.setCurrentTrack(track)

        val fragment = activity as MainActivity
        fragment.updatePlayerUI(fragment.findViewById(R.id.player_sliding), track)
        fragment.updateUIPlayingTrack(false)
        viewModel.startPlayer(track, args.albumItem)
    }


    /*override fun onStop() {
        super.onStop()
        if (requireActivity().isChangingConfigurations && viewModel.playerAdapter.isPlaying()) {
            Log.d("toto", "onStop: don't release MediaPlayer as screen is rotating & playing")
        } else {
            viewModel.playerAdapter.release()
            Log.d("toto", "onStop: release MediaPlayer")
        }
    }*/

    // Permet lecture background mais l'app doit etre tjrs ouverte -> detruit quand change d'ecran
    override fun onDestroy() {
        super.onDestroy()
       // viewModel.playerAdapter.release()
        Log.d("toto", "onDestroy: release MediaPlayer")
    }

}
