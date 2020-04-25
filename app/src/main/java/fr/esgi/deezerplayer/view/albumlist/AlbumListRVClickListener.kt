package fr.esgi.deezerplayer.view.albumlist

import android.view.View
import fr.esgi.deezerplayer.data.model.Album

interface AlbumListRVClickListener {
    fun onRecyclerViewItemClick(view: View, album: Album)
}