package fr.esgi.deezerplayer.view.albumdetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs

import fr.esgi.deezerplayer.R

class AlbumDetailFragment : Fragment() {

    private lateinit var viewModel: AlbumDetailViewModel

    private val args by navArgs<AlbumDetailFragmentArgs>()
    private lateinit var titleTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AlbumDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            titleTv = findViewById(R.id.album_titleTV)
        }
        titleTv.text = "ID album: " + args.albumId
    }

}
