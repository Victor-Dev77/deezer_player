package fr.esgi.deezerplayer.features.view.albumlist

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.api.AlbumAPI
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.repository.AlbumRepository
import fr.esgi.deezerplayer.features.BaseViewModelFactory
import fr.esgi.deezerplayer.features.RVClickListener
import kotlinx.android.synthetic.main.album_list_fragment.*

class AlbumListFragment : Fragment(),
    RVClickListener {

    private var listenerNavigation: ((Album) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val api = AlbumAPI()
        val repository = AlbumRepository(api)
        val factory = BaseViewModelFactory { AlbumListViewModel(repository) }
        val viewModel = ViewModelProviders.of(this, factory).get(AlbumListViewModel::class.java)

        // init listener Navigation
        listenerNavigation = this::navigateToAlbumDetail

        // Recupère les albums (api request)
        viewModel.getAlbums()

        // liveData observable => gere le cycle de vie et protege les données
        // ex: rotation de l'ecran, recevoir un appel, perte de batterie, les données sont pas perdus et recréer grace a LiveData
        viewModel.albums.observe(viewLifecycleOwner, Observer { albums ->
            // album_rcv = id recyclerview dans XML <=> comme findviewbyid
            album_rcv.also {
                // init recyclerview
                it.isNestedScrollingEnabled = false
                it.layoutManager = GridLayoutManager(requireContext(), 3)
                it.addItemDecoration(SpaceGrid(3, 30, true))
                it.setHasFixedSize(true)
                it.adapter = AlbumAdapter(albums, this)
            }
        })
    }

    override fun <Album> onRecyclerViewItemClick(view: View, data: Album) {
        listenerNavigation?.invoke(data as fr.esgi.deezerplayer.data.model.Album)
    }

    // Navigation to AlbumDetailFragment with album id in param
    private fun navigateToAlbumDetail(album: Album) {
        findNavController().navigate(
            AlbumListFragmentDirections.actionAlbumListFragmentToAlbumDetailFragment(
                album
            )
        )
    }


    // class decoration cellule recyclerview
    private inner class SpaceGrid(
        private val mSpanCount: Int,
        private val mSpacing: Int,
        private val mIncludeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % mSpanCount
            if (mIncludeEdge) {
                outRect.left = mSpacing - column * mSpacing / mSpanCount
                outRect.right = (column + 1) * mSpacing / mSpanCount
                if (position < mSpanCount) {
                    outRect.top = mSpacing
                }
                outRect.bottom = mSpacing
            } else {
                outRect.left = column * mSpacing / mSpanCount
                outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount
                if (position < mSpanCount) {
                    outRect.top = mSpacing
                }
            }
        }
    }
}

