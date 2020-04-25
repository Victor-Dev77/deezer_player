package fr.esgi.deezerplayer.view.albumdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.databinding.AlbumDetailFragmentBinding
import fr.esgi.deezerplayer.util.loadImage

class AlbumDetailFragment : Fragment() {

    private lateinit var viewModel: AlbumDetailViewModel
    private lateinit var binding: AlbumDetailFragmentBinding

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
        binding = viewBinding
        // Set variable dans XML (album) par valeur (albumItem)
        viewBinding.album = args.albumItem
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AlbumDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            val toolbar: Toolbar = view.findViewById(R.id.toolbar)
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        }

        loadImage(
            binding.albumDetailCover,
            args.albumItem.cover,
            binding.albumDetailShimmer
        )

    }



}
